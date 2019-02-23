package com.aleek.trivia.quickstart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import org.json.JSONObject;

import com.google.common.net.UrlEscapers;

import org.json.JSONArray;
import org.json.JSONException;

public class PopulationHandler {
	private static String[] countries = {
		"France",
		"United Kingdom",
		"United States",
		"Germany",
		"Japan",
		"Cuba",
		"Finland",
		"Italy",
		"Canada",
		"Nigeria",
		"Ghana",
		"South Africa",
		"United Arab Emirates",
		"China",
		"Thailand",
		"India",
		"Poland",
		"Vietnam",
		"Slovenia",
		"Middle Africa",
		"Guyana",
		"Eastern Africa",
		"Czech Republic",
		"AFRICA",
		"EUROPE"
	};
	private ServerHandler server;
	
	public PopulationHandler(ServerHandler server) {
		super();
		
		/*Create Connection Handler*/
		this.server = server;
	}
	
	/*-------------------------Drivers-------------------------*/
	public void informDatabase() {
		server.table_name = server.table[1];
		/*Check for stability*/
		println("informDatabase Starting: Populating table");
		if(server.initializeDatabaseCommunication()) {
			int sqlWrites = 0;
			long time = 0, eTime = 0, sTime = System.nanoTime();
			
			/*Populate table with weather data*/
			String SQL = null;
			for(int id = 0; id <= countries.length-1; id++) {
				try {
					/*Fetch data*/
					int population = fetch(id);
					
					/*Write SQL statement*/
					SQL = "INSERT INTO " + server.table_name + " (id, countryName, population) "
							   + "VALUES (" + (id+1) + ", '" + countries[id] + "', " + population + ")";
					
					/*Execute SQL statement*/
					ServerHandler.database_statement.execute(SQL);
					
					/*Log writes*/
					println("informDatabase: SQL written to database : " + SQL);
					sqlWrites++;
				}
				
				/*Error Handling*/
				catch(MalformedURLException e) {
					println("informDatabase Cancelled: URL is malformed (See stacktrace)\n");
					e.printStackTrace();
					println("\nTerminating Application...");
					System.exit(0);
				} 
				catch (SQLException e) {
					println("informDatabase Cancelled: SQL execution failed check statement structure\n");
					e.printStackTrace();
					println("\nTerminating Application...");
					System.exit(0);
				}
				catch (IOException e) {
					println("informDatabase-fetch Cancelled: Fetching failed (See stacktrace)\n");
					e.printStackTrace();
					println("\nTerminating Application...");
					System.exit(0);
				}
				catch (JSONException e) {
					println("informDatabase-jsonParse Cancelled: Fetching failed (See stacktrace)\n");
					e.printStackTrace();
					println("\nTerminating Application...");
					System.exit(0);
				}
				catch (Exception e) {
					println("~~UNEXPECTED ERROR: Review stack trace~~");
					e.printStackTrace();
					println("\nTerminating Application...");
					System.exit(0);
				}
				
				
			}
			eTime = System.nanoTime();
			time = (eTime - sTime) /1000000000;
			
			println("\n\n------------------------------------------------------------------"
					  + "\ninformDatabase Complete: Database successfully populated"
					  + "\nDatabase: " + server.database_name
					  + "\nSQL Writes made: " + sqlWrites
					  + "\nTime taken: " + time + " seconds"
					  + "\nApplication terminating");	
		}
		else {
			println("informDatabase Cancelled: Communications to the database is not healthy\n"
				  + "Finished Database Population");
			System.exit(0);
		}
		
		println("informDatabase Complete: Database successfully populated");
	}
	
	public int fetch(int id) throws MalformedURLException, IOException, JSONException{
		println("\n------------------------------------------------------------------\n"
			  + "Fetch for " + countries[id] + " started\n\n");
		
		/*JSON Declarations*/
		String json = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		
		/*Connect to API via HTTP*/
		String unencoded = "http://api.population.io/1.0/population/" + countries[id] + "/today-and-tomorrow/?format=json",
			   encoded = UrlEscapers.urlFragmentEscaper().escape(unencoded);
		
		URL url = new URL(encoded);
		println("informDatabase-fetch: Establishing HTTP connection to Population Api"
			  + "\nURL Attempting: " + url.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		/*200 ResponseCode Means Lit*/
		if(connection.getResponseCode() == 200) {
			println("informDatabase-fetch: Connection to Population Api established: Response Code {" + connection.getResponseCode() + "}");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			/*Capture JSON*/
			json = reader.readLine();
			jsonObject = new JSONObject(json);
			jsonArray = jsonObject.getJSONArray("total_population");
		} 
		else {
			println("informDatabase-fetch Cancelled: Response code of {" + connection.getResponseCode() 
			      + "} recieved\nTerminating Application...");
			System.exit(0);
		}
		
		/*Kinda Important */
		connection.disconnect();
		println("Connection disconnecting: JSON captured succesfully and returned\n"
			  + "JsonArray: " + jsonArray.getJSONObject(0).getInt("population"));
		
		return jsonArray.getJSONObject(0).getInt("population");
	}
	
	/*-------------------------Utilities-----------------------*/
	public static void print(Object print) {System.out.print(print);}
	
	public static void println(Object print) {System.out.println(print);}
	
	public static void println() {System.out.print("\n");}
	
	
}
