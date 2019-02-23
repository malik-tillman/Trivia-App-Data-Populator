package com.aleek.trivia.quickstart;

import java.sql.SQLException;

import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;

public class WeatherHandler {
    private static String[] cities = {
            "Paris",
            "London",
            "New York",
            "Berlin",
            "Tokyo",
            "Moscow",
            "Rome",
            "Venice",
            "Toronto",
            "Lagos",
            "Accra",
            "Cape Town",
            "Dubai",
            "Hong Kong",
            "Bangkok",
            "Mumbai",
            "Chicago",
            "Houston",
            "San Francisco",
    };
    private String API_KEY;
	private OWM open_weather_map;
	private static OWM.Unit data_units;
	private ServerHandler server;
	
	public WeatherHandler(ServerHandler server) {
		super();
		
		/*Create Connection Handler*/
		this.server = server;
		
		/*true=fahrenheit   false=Celsius*/ 
		boolean isImperial = true; 
		data_units = (isImperial) ? OWM.Unit.IMPERIAL : OWM.Unit.METRIC;
		
		/*Authentication key for API*/
		/*https://home.openweathermap.org/api_keys*/
		API_KEY = "ad8643c62aefd15f324e65a3359e39e4"; 
		open_weather_map = new OWM(API_KEY); 
	}
	
	public WeatherHandler(ServerHandler server, String API_KEY) {
		super();
		
		/*Create Connection Handler*/
		this.server = server;
				
		/*true=fahrenheit   false=Celsius*/  
		boolean isImperial = true; 
		data_units = (isImperial) ? OWM.Unit.IMPERIAL : OWM.Unit.METRIC;
		
		/*Authentication key for API*/
		/*https://home.openweathermap.org/api_keys*/
		this.API_KEY = API_KEY;
		open_weather_map = new OWM(API_KEY); 
	}
	
	/*-------------------------Drivers-------------------------*/
    public void informDatabase(){
    	server.table_name = server.table[0];
 		/*Check for stability*/
    	println("informDatabase Starting: Populating table");
		if(server.initializeDatabaseCommunication()) {
			int sqlWrites = 0;
			long time = 0, eTime = 0, sTime = System.nanoTime();
			
			/*Populate table with weather data*/
			String SQL = null;
			for(int id = 0; id <= cities.length-1; id++) {
				try {
						/*Fetch data*/
						int temperature_data = fetch(id);
						
						/*Write SQL statement*/
						SQL = "INSERT INTO " + server.table_name + " (ID, CityName, CurTemp) "
								   + "VALUES (" + (id+1) + ", '" + cities[id] + "', " + temperature_data + ")";
						
						/*Execute SQL statement*/
						ServerHandler.database_statement.execute(SQL);
						
						/*Log writes*/
						println("informDatabase: SQL written to database : \n" + SQL);
						
						sqlWrites++;
				}
				
				/*Error Handling*/
				catch (APIException e) {
					println("informDatabase Cancelled: Error with OWM API. Check build paths have compatibility with drivers (See stacktrace)\n");
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
				catch (Exception e) {
					println("~~UNEXPECTED ERROR: Review stack trace~~");
					e.printStackTrace();
				}
			}
			eTime = System.nanoTime();
			time = (eTime - sTime) /1000000000;
			
			println("\n\n------------------------------------------------------------------"
					  + "\ninformDatabase Complete: Database successfully populated"
					  + "\nDatabase: " + server.database_name
					  + "\nSQL Writes made: " + sqlWrites
					  + "\nTime taken: " + time + " seconds"
					  + "\nFinished Database Weather");
		}
		else {
			println("informDatabase Cancelled: Communications to the database is not healthy\n"
					  + "Terminating Application...");
				System.exit(0);
		}
		
		
		
		
	}
 	
    public int fetch(int id) throws APIException{
    	println("\n------------------------------------------------------------------\n"
  			  + "Fetch for " + cities[id] + " started\n");
    	
		/*Variable will hold Min/Max/Current temperature in Array*/
		int temperature_data;
		
		/*Make Call for weather data*/
		open_weather_map.setUnit(data_units);
		CurrentWeather city_weather_data = open_weather_map.currentWeatherByCityName(cities[id]);
		println("informDatabase-fetch: Connection established : retrieving data...");
		
		/*Retrieve data*/
		temperature_data = city_weather_data.getMainData().getTemp().intValue();
		println("informDatabase-fetch: Data from API retrieved : returning data");
		
		return temperature_data;
	}
	
    /*-------------------------Utilities-----------------------*/
	public static void print(Object print) {System.out.print(print);}
	
	public static void println(Object print) {System.out.println(print);}
	
	public static void println() {System.out.print("\n");}
}
