package com.ibm.cloudoe.samples;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.personality_insights.v2.model.Profile;

@Path("/addBook")
public class AddNewBook {

	@GET
	public String process(@QueryParam("id") long id) {
		
		System.out.println("Getting book " + id + "...");
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		/*
		 * Uncomment the following line to download books from the Project Gutenberg.
		 * 
		 * Unfortunately, The Project Gutenberg replies with a 403 error and bans IPs
		 * that it detects to be using bots to get books.
		 */
		// HttpGet httpGet = new HttpGet(String.format("http://www.gutenberg.org/cache/epub/%d/pg%d.txt", id, id));
		
		/*
		 * Download the book from the Book Sense site, to simulate downloading it
		 * from The Project Gutenberg.
		 */
		HttpGet httpGet = new HttpGet(String.format("http://book-sense.mybluemix.net/books/pg%d.txt", id));
		
		/*
		 * Project Gutenberg won't let us download contents if our User-Agent
		 * is "Apache-HttpClient/4.5.1 (Java/1.8.0_66)", which is the one that
		 * the Apache HTTP lib uses. That is why we need to present ourselves
		 * as the wget tool. Not a big lie, since wget is also not a web browser
		 * but a tool to download contents from using HTTP.
		 */
		httpGet.setHeader("User-Agent", "Wget/1.15 (linux-gnu)");
		CloseableHttpResponse response;
		try {
			System.out.println("Getting book contents...");
			
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			/*
			 * We first dump all bytes to a file. This helps debugging, so we
			 * can check the received data on the Bluemix dashboard.
			 */
			File bookFile = new File("book-" + id + ".txt");
			FileOutputStream fos = new FileOutputStream(bookFile);
			entity.writeTo(fos);
			fos.close();
			
			/*
			 * Read the whole book in a ByteArrayOutputStream and convert it
			 * into a big String object called bookString.
			 */
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream fis = new FileInputStream(bookFile);
			while (fis.available() > 0) {
				byte[] buffer = new byte[Math.min(fis.available(), 1024)];
				fis.read(buffer);
				baos.write(buffer);
			}
			fis.close();
			baos.flush();
			baos.close();
			String bookString = new String(baos.toByteArray());
			
			System.out.println("Done.");
			
			ServiceBuilder serviceBuilder = new ServiceBuilder();
			
			System.out.println("Asking Watson to calculate the personality insights.");
			Profile profile = serviceBuilder.personalityInsights().getProfile(bookString);
			
			/*
			 * Retrieves basic book information (title and author) from
			 * the downloaded file.
			 */
			Book book = BookParser.parse(bookString);
			
			System.out.println("Saving data in MySQL DB...");
			Connection connection = serviceBuilder.mySQLConnection();
			Statement statement = connection.createStatement();
			statement.execute(String.format("INSERT INTO BOOK VALUES (%d, \"%s\", \"%s\", \"%s\")",
					id,
					book.getTitle(),
					book.getAuthor(),
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date())));
			connection.close();
			
			System.out.println("Saving JSON result in Cloudant DB...");
			JsonObject jsonObject = (JsonObject) new JsonParser().parse(profile.toString());
			jsonObject.addProperty("bookId", id);
			jsonObject.addProperty("_id", Long.toString(id));
			serviceBuilder.cloudantDB().save(jsonObject);
			System.out.println("DONE!");
			
			/*
			 * The Javascript application expects to receive OK...
			 */
			return "OK";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*
		 * ...otherwise it will show an error message. We could
		 * have implemented more informative error messages such
		 * as "the book was already registered", but we leave
		 * such improvement for furure releases.
		 */
		return "Error!";
	}
}