package com.ibm.cloudoe.samples;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * REST resource that provides a list of books in the JSON format.
 * 
 * @author akira
 */
@Path("/listBooks")
public class ListBooks {

	@GET
	public String process() {
		ServiceBuilder serviceBuilder = new ServiceBuilder();
		Connection connection = serviceBuilder.mySQLConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("select * from book");
			ResultSet resultSet = statement.executeQuery();
			JsonArray array = new JsonArray();
			while(resultSet.next()) {
				JsonObject jsonBook = new JsonObject();
				jsonBook.addProperty("id", resultSet.getLong(1));
				jsonBook.addProperty("title", resultSet.getString(2));
				jsonBook.addProperty("author", resultSet.getString(3));
				array.add(jsonBook);
			}
			return array.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Error!";
	}
}