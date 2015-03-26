package com.fit.ddw;


import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CreoleRegister;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Node;
import gate.ProcessingResource;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.undo.UndoManager;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class GateClient {

	public
	String title;
	String caption;
	String article;
	String meta;
	int nmb;
	String[] kw;
	int[] cn; 
	String[] fin;
	int count = 0;
	boolean isNew = true;
	
    // corpus pipeline
    private static SerialAnalyserController annotationPipeline = null;
    
    // whether the GATE is initialised
    private static boolean isGateInitilised = false;
    
	public String[] run(String url) throws IOException {
		
        if(!isGateInitilised){
            
            // initialise GATE
            initialiseGate();            
        }  	
        title = UrlTextGetter.getTitle(url);
        article = UrlTextGetter.getArticle(url);
        caption = UrlTextGetter.getCaption(url);
	
	
        try {                
            ProcessingResource documentResetPR = (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR");
            ProcessingResource tokenizerPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.DefaultTokeniser");
            ProcessingResource sentenceSplitterPR = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter");
            ProcessingResource posTagger = (ProcessingResource) Factory.createResource("gate.creole.POSTagger");
            
            // locate the JAPE grammar file
            File japeOrigFile = new File("C:/Users/Veru/Documents/DDW/podstJm.jape");
            java.net.URI japeURI = japeOrigFile.toURI();
            
            // create feature map for the transducer
            FeatureMap transducerFeatureMap = Factory.newFeatureMap();
            try {
                // set the grammar location
                transducerFeatureMap.put("grammarURL", japeURI.toURL());
                // set the grammar encoding
                transducerFeatureMap.put("encoding", "UTF-8");
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL of JAPE grammar");
                System.out.println(e.toString());
            }
            
            // create an instance of a JAPE Transducer processing resource
            ProcessingResource japeTransducerPR = (ProcessingResource) Factory.createResource("gate.creole.Transducer", transducerFeatureMap);

            // create corpus pipeline
            annotationPipeline = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");

            // add the processing resources (modules) to the pipeline
            annotationPipeline.add(documentResetPR);
            annotationPipeline.add(tokenizerPR);
            annotationPipeline.add(sentenceSplitterPR);
            annotationPipeline.add(posTagger);
            annotationPipeline.add(japeTransducerPR);
            
            // create a document
            Document document = Factory.newDocument(title + " " + caption + " " + article);
            System.out.println(title + " \n" + caption + " \n" + article + " \n" + meta);
            
            // create a corpus and add the document
            Corpus corpus = Factory.newCorpus("");
            corpus.add(document);

            // set the corpus to the pipeline
            annotationPipeline.setCorpus(corpus);

            //run the pipeline
            annotationPipeline.execute();

            // loop through the documents in the corpus
            for(int i=0; i< corpus.size(); i++){

                Document doc = corpus.get(i);

                // get the default annotation set
                AnnotationSet as_default = doc.getAnnotations();

                FeatureMap futureMap = null;
                // get all Token annotations
                AnnotationSet annSetTokens = as_default.get("Nouns",futureMap);
                nmb = annSetTokens.size();
                System.out.println("Number of Token annotations: " + annSetTokens.size());


                kw = new String[nmb];
                cn = new int[nmb];
                fin = new String[nmb];
                
                ArrayList tokenAnnotations = new ArrayList(annSetTokens);

                // looop through the Token annotations
               for(int j = 0; j < tokenAnnotations.size(); ++j) {
            	   isNew = true;
                    // get a token annotation
                    Annotation token = (Annotation)tokenAnnotations.get(j);

                    // get the underlying string for the Token
                    Node isaStart = token.getStartNode();
                    Node isaEnd = token.getEndNode();
                    String underlyingString = doc.getContent().getContent(isaStart.getOffset(), isaEnd.getOffset()).toString().toLowerCase();
                    System.out.println("Token: " + underlyingString);
                    
/*----------------------------------------------------*/
                   for(int k = 0; k < j; k++){ 
                	   if(underlyingString.equals(kw[k])){
                		   cn[k] = cn[k] + 1;
                		   isNew = false;
                		//   System.out.println("Na pozici " + k + " editovan pocet " + cn[k] + " u slova " + kw[k]);   
                	   } 
                   }
                   
                   if(isNew == true){       			   
                	   kw[count] = underlyingString;
        			   cn[count] = 1;
        			 //  System.out.println("Pridano nove slovo " + underlyingString + " na pozici " + count);
        			   count++;
                   }
                   
                   bubbleSort();
                   
                   combineStrings();
                                    
                   
                } 
            }
            
            
            for(int fn = 0; fn < kw.length; fn++){
            	System.out.println("Slovo " + kw[fn] + " se nachazi " + cn[fn]);            	
            }
            
            
        } catch (GateException ex) {
            Logger.getLogger(GateClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        return fin;
	}	

	
private void combineStrings() {
	for (int i = 0; i < kw.length - 1; i++) {
		fin[i] = kw[i] + " (" + cn[i] + ")"; 
	}     
		
}


	private void bubbleSort() {     
		for (int i = 0; i < cn.length - 1; i++) {
			for (int j = 0; j < cn.length - i - 1; j++) {
				if(cn[j] < cn[j+1]){
					String tmpKw = kw[j]; 
					int tmpCn = cn[j];
					cn[j] = cn[j+1];
					kw[j] = kw[j+1];
					cn[j+1] = tmpCn;
					kw[j+1] = tmpKw;
				}
     	   	}
		}       
	}



	private void initialiseGate() {
        
        try {
            // set GATE home folder
            // Eg. /Applications/GATE_Developer_7.0
            File gateHomeFile = new File("D:/programy/GATE");
            Gate.setGateHome(gateHomeFile);
            
            // set GATE plugins folder
            // Eg. /Applications/GATE_Developer_7.0/plugins            
            File pluginsHome = new File("D:/programy/GATE/plugins");
            Gate.setPluginsHome(pluginsHome);            
            
            // set user config file (optional)
            // Eg. /Applications/GATE_Developer_7.0/user.xml
            Gate.setUserConfigFile(new File("D:/programy/GATE", "user.xml"));            
            
            // initialise the GATE library
            Gate.init();
            
            // load ANNIE plugin
            CreoleRegister register = Gate.getCreoleRegister();
            URL annieHome = new File(pluginsHome, "ANNIE").toURL();
            register.registerDirectories(annieHome);
            
            // flag that GATE was successfuly initialised
            isGateInitilised = true;
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(GateClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GateException ex) {
            Logger.getLogger(GateClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
	
}
