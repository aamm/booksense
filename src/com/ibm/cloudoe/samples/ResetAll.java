package com.ibm.cloudoe.samples;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.AllDocsRequest;

/**
 * REST resource that erases all data.
 * 
 * @author akira
 */
@Path("/resetall")
public class ResetAll {

	@GET
	public void process() {
		ServiceBuilder serviceBuilder = new ServiceBuilder();
		
		System.out.println("Cleaning the MySQL database...");
		Connection connection = serviceBuilder.mySQLConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("delete from book");
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Cleaning the NoSQL database...");
		Database cloudant = serviceBuilder.cloudantDB();
		AllDocsRequest request = cloudant.getAllDocsRequestBuilder().build();
		try {
			Map<String, String> map = request.getResponse().getIdsAndRevs();
			for (String key : map.keySet()) {
				System.out.println("Removing from Cloudant: id = " + key);
				cloudant.remove(key, map.get(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}