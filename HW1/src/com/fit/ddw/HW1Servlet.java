package com.fit.ddw;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HW1Servlet
 */
@WebServlet("/HW1Servlet")
public class HW1Servlet extends HttpServlet {
	
	String htmlResponse;
	private static final long serialVersionUID = 1L;  
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HW1Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String url = request.getParameter("url");
		
		GateClient gate = new GateClient();
		String[] field = gate.run(url);
		
		PrintWriter writer = response.getWriter();
		
		htmlResponse = "<html>";
        htmlResponse += "<h1>" + UrlTextGetter.getTitle(url) + "</h1>"; 
        
        htmlResponse += "<a href="+ url + ">National Geographic - " + UrlTextGetter.getTitle(url) + "</a>";
        
        htmlResponse += "<p>";
        
        for(int i=0; i < 15; i++){
            htmlResponse += field[i] + ", ";	
        }
        
        htmlResponse += "</p>";       
        htmlResponse += "<p>" + UrlTextGetter.getCaption(url) + "</p>";
        htmlResponse += "<p>" + UrlTextGetter.getArticle(url) + "</p>";
		htmlResponse += "</html>";
		
        //System.out.println(keywords[0].marks); // prints 99
		
		writer.println(htmlResponse);
	}

}
