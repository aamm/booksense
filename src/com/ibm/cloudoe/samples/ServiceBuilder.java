package com.ibm.cloudoe.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.personality_insights.v2.PersonalityInsights;

/**
 * Not the ideal way to obtain resources, but good enough for this proof of
 * concept. We could have used dependency injection (Spring or Pico Container
 * for instance) instead.
 * 
 * @author akira
 *
 */
public class ServiceBuilder {

	private static JsonObject jsonObject;
	private static PersonalityInsights personalityInsights = null;

	public ServiceBuilder(String json) {
		jsonObject = (JsonObject) new JsonParser().parse(json);
	}
	
	public ServiceBuilder(File jsonFile) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(jsonFile);
		InputStreamReader reader = new InputStreamReader(fis);
		jsonObject = (JsonObject) new JsonParser().parse(reader);
	}
	
	public ServiceBuilder() {
		jsonObject = (JsonObject) new JsonParser().parse(System.getenv("VCAP_SERVICES"));
	}
	
	public PersonalityInsights personalityInsights() {
		/*
		 * Lazy initialization
		 */
		if (personalityInsights == null) {
			JsonObject credentialsObject = ((com.google.gson.JsonArray) jsonObject
					.get("personality_insights")).get(0).getAsJsonObject()
					.get("credentials").getAsJsonObject();
			String username = credentialsObject.get("username").getAsString();
			String password = credentialsObject.get("password").getAsString();
			personalityInsights = new PersonalityInsights();
			personalityInsights.setUsernameAndPassword(username, password);
		}
		return personalityInsights;

	}

	public Connection mySQLConnection() {
		
		/*
		 * Each access to the database will start a new connection.
		 * For sure not a good practice in terms of performance but
		 * good enough for this proof of concept.
		 */
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonObject credentialsObject = ((com.google.gson.JsonArray) jsonObject
				.get("cleardb")).get(0).getAsJsonObject()
				.get("credentials").getAsJsonObject();
		String username = credentialsObject.get("username").getAsString();
		String password = credentialsObject.get("password").getAsString();
		String jdbcUrl = credentialsObject.get("jdbcUrl").getAsString();
		Properties info = new Properties();
		info.setProperty("username", username);
		info.setProperty("password", password);
		try {
			return DriverManager.getConnection(jdbcUrl, info);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public Database cloudantDB() {
		
		JsonObject credentialsObject = ((com.google.gson.JsonArray) jsonObject
				.get("cloudantNoSQLDB")).get(0).getAsJsonObject()
				.get("credentials").getAsJsonObject();
		String username  = credentialsObject.get("username").getAsString();
		String password  = credentialsObject.get("password").getAsString();
		String urlString = credentialsObject.get("url").getAsString();
		
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		CloudantClient client = ClientBuilder.url(url)
	            .username(username)
	            .password(password)
	            .build();
		
		return client.database("book-insights", false);
	}
	
}
