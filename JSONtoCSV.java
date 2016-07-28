/*
 * ***************************************************************
 * JSONtoCSV.java        		 		  27/07/2016
 * Copyright(c)
 *
 * Version History:
 * -------------------------------------------------------
 * Date         Author		             Nature of Change
 * -------------------------------------------------------
 *
 * 27/07/2016   Balakrishnan Kulasekaran  Base Version
 *
 * ****************************************************************
 */
package com.sample.workout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JSONtoCSV {
	
	/**
	 * Variable to load Script Engine
	 */
	private ScriptEngine loScriptEngine;
	
	/**
	 * Variable to hold CityName passed via argument.
	 */
	private String cityName;
	
	
	/**
	 * @return the cityName
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * initializing Nashorn Script Engine
	 */
	public void initializeScriptEngine() {
		loScriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
	}
	
	/**
	 * parsingJSON to Javascript
	 */
	public void parseJSON() throws Exception{
		this.loScriptEngine.eval(new InputStreamReader(getClass().getResourceAsStream("json2csv.js")));
		Invocable invocable=(Invocable)loScriptEngine;
		Object result=invocable.invokeFunction("json2csv",getJSONByCityName(getCityName()));
		writeOutput((String)result);		
	}
	
	/**
	 * main method
	 */
	public static void main(String args[]) throws Exception {
		System.out.println("Begin Conversion");
		long start = System.currentTimeMillis();
		if(args[0] == null || args[0].trim().length() == 0) {
			System.out.println("Invalid City Name");
			return;
		}
		args[0] = args[0].replace("\"", "");
		System.out.println("City Name " + args[0]);
		JSONtoCSV loJsontoCSV = new JSONtoCSV();
		loJsontoCSV.setCityName(args[0]);
		loJsontoCSV.initializeScriptEngine();
		loJsontoCSV.parseJSON();
		long stop = System.currentTimeMillis();
		System.out.println("Conversion took " + (stop - start) + "ms.");
		System.out.println("End Conversion");
	}
	
	
	/**
	 * Invoke the Api by CityName and get the JSON response
	 * @param cityName passed via argument
	 * @return JsonResponse
	 */
	public String getJSONByCityName(String lsCityName) throws Exception {
    	URL url;
    	URLConnection urlConnection;
    	HttpURLConnection connection = null;
    	String responseJSON = null;
        try
        {
           url = new URL("http://api.goeuro.com/api/v2/position/suggest/en/"+lsCityName);
           urlConnection = url.openConnection();
           urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");           
           if(urlConnection instanceof HttpURLConnection) {
              connection = (HttpURLConnection) urlConnection;
           }
           else {
              System.out.println("Please enter an Valid HTTP URL.");
           }
           if(connection.HTTP_OK == connection.getResponseCode()) {
        	   responseJSON = read(connection.getInputStream());
           }else {
        	   String errorResponse = read(connection.getErrorStream());
        	   System.out.println(errorResponse);
           }
                             
        }catch(IOException e)
        {
           e.printStackTrace();
        }
        return responseJSON;
    }
	
	
	/**
	 * To read the value from the Stream (Input/Error) 
	 * @param poStream
	 */
	public String read(InputStream poStream) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(poStream,"utf-8"))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
	
	/**
	 * To write the response to csv file
	 * @param psOutput
	 */
	public void writeOutput(String psOutput) {
		PrintWriter pw = null;
		try {
		    pw = new PrintWriter(new File("output.csv"));
		    pw.write(psOutput);
		    pw.close();
		    System.out.println("!!output.csv generated!!");
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		}

	}
}
