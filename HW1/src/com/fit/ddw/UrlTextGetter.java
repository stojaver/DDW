package com.fit.ddw;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class UrlTextGetter {
	
	static String article;
	static String title;
	static String caption;
	static org.jsoup.nodes.Document getText;
	
	public static String getArticle(String url){
		try {
			getText = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements articleele = getText.select("div#article__body");
		article = articleele.text();	
		return article;		
	}
	
	public static String getTitle(String url){
		try {
			getText = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		title = getText.select("h1").text();
		return title;		
	}
	
	public static String getCaption(String url){
		try {
			getText = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements captionele = getText.select("div.article__deck");
		caption = captionele.text();
		return caption;		
	}
	

}
