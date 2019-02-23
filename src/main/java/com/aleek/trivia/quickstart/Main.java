package com.aleek.trivia.quickstart;

import net.aksingh.owmjapis.api.APIException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {
	public static boolean update_weather = true;
	public static boolean update_population = true;
	public static String userResponse = "";
	public static int interval;
	
	public static String[]  cities = {
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
            "San Francisco"
    };
	
	//-------------------------DRIVERS-------------------------
 	public static void main(String[] args) throws APIException, SQLException {

 		//getWeatherQuestion(15);
 		
 		
 		ServerHandler serverConnection = getCreds();
 		WeatherHandler weatherHandler = new WeatherHandler(serverConnection);
 		PopulationHandler populationHandler = new PopulationHandler(serverConnection);
 		
 		if(update_weather)
 			weatherHandler.informDatabase();
 		
 		if(update_population)
 			populationHandler.informDatabase();
 		
 		print("Finished task");
 		
	}
 	
 	private static void getWeatherQuestion(int amount){
        ArrayList<Integer> city_codes = getCityCodes(amount),
                           rightAnswers = new ArrayList<>(),
                           wrongAnswers1 = new ArrayList<>(),
                           wrongAnswers2 = new ArrayList<>(),
                           wrongAnswers3 = new ArrayList<>();
        ArrayList<String> questions = new ArrayList<>();

        for(int i = 0; i<amount; i++){
            int[] cachedWeathers = new int[4];
            cachedWeathers[0] = getWeather(city_codes.get(i));

            for(int r = 0; r<3; r++){
                Random random = new Random();
                cachedWeathers[r+1] = getWeather(random.nextInt(cities.length));
            }

            if(cachedWeathers[0]==cachedWeathers[1]){
                cachedWeathers[1]+=13;
            }

            if(cachedWeathers[1]==cachedWeathers[2]){
                cachedWeathers[1]-=4;
            }

            if (cachedWeathers[1]==cachedWeathers[3]){
                cachedWeathers[1]-=20;
            }

            rightAnswers.add(cachedWeathers[0]);
            wrongAnswers1.add(cachedWeathers[1]);
            wrongAnswers2.add(cachedWeathers[2]);
            wrongAnswers3.add(cachedWeathers[3]);
            
            println(rightAnswers.get(i));
            println(wrongAnswers1.get(i));
            println(wrongAnswers2.get(i));
            println(wrongAnswers3.get(i));
            
            Random random2 = new Random();
            int randomString = random2.nextInt(3);
            String formattedQuestion = null;
            switch (randomString){
                case 0:
                    formattedQuestion = "What is the weather in " + cities[city_codes.get(i)] +"?";
                    break;
                case 1:
                    formattedQuestion = "Can you guess the weather in " + cities[city_codes.get(i)] +"?";
                    break;
                case 2:
                    formattedQuestion = "Which is the current weather in " + cities[city_codes.get(i)] +"?";
                    break;
            }
            
            questions.add(formattedQuestion);
            println(formattedQuestion);
        }

    }

    private static int getWeather(int city_code){
        int temp = 0;
        try{
            String json;

            URL url = new URL("http://192.168.1.189/api/Tadcityweathers/" + city_code);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if(connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                json = reader.readLine();
                JSONObject jsonObject = new JSONObject(json);

                temp = jsonObject.getInt("curTemp");
            }
        }
        catch (MalformedURLException e){

        }
        catch (IOException e){

        }
        catch (JSONException e){

        }

        return temp;
    }

    private static ArrayList<Integer> getCityCodes(int amount){
        Random random = new Random();
        ArrayList<Integer> city_codes = new ArrayList<>();

        for (int i = 0; i<amount; i++)
            city_codes.add(random.nextInt(cities.length));

        return city_codes;
    }
 	
 	public static int getInterval() {
 		return --interval;
 	}
	
 	public static ServerHandler getCreds() {
 		Scanner in = new Scanner(System.in);
 		ServerHandler serverConnection = new ServerHandler();
 		
 		/*Header*/
 		print("This program will populate the specified SQL server with\n"
 			  + "weather information. The SQL server MUST be set-up already\n"
 			  + "and MUST have a table named <TADcityweathers> located within it\n"
 			  + "Without this, you will without a doubt recieve a fatal error.\n\n"
 			  + "This program was made by and for the use of Malik Tillman\n"
 			  + "and should under any circumstances be used without his expressed\n"
 			  + "permission.\n\n"
 			  + "Would you like to change the defualt configurations? [Y/N/S]\n"
 			  + "**Type 'S' to see current configurations**"
 			  + "\n:");
 		
 		/*Change Configuration Questions*/
 		int ticker = 1;
 		while(incorrectInput(in.nextLine(), "y/n/s")) {
 			if(ticker>=5) { 
 				println("\nToo many incorrect inputs...\nTerminating program");
 				System.exit(0);
 				break;
 			}
 			else
 				println("\nIncorrect input: Y/N/S are the only acceptable inputs");
 			
 			ticker++;
 		}
 		
 		/*Yes Case*/
 		if(userResponse.equalsIgnoreCase("y")) {
 			/*API Key Question*/
 			print("\nLeave blank to keep configuration's default value\n"
 				+ "API Key: ");
 			String API_KEY = in.nextLine();
 			if(API_KEY.equalsIgnoreCase(""));
 			
 			/*Server Question*/
 			print("Server: ");
 			String server_name = in.nextLine();
 	 		if(server_name.equalsIgnoreCase(""))
 	 			server_name = serverConnection.server_name;
 	 		
 	 		/*Database Question*/
 	 		print("Database: ");
 	 		String database_name = in.nextLine();
 	 		if(database_name.equalsIgnoreCase(""))
 	 			database_name = serverConnection.database_name;
 	 		
 	 		/*Username Question*/
 	 		print("Username: ");
 	 		String database_user = in.nextLine();
 	 		if(database_user.equalsIgnoreCase(""))
 	 			database_user = serverConnection.database_user;
 	 		
 	 		/*Password Question*/
 	 		print("Password: ");
 	 		String database_password = in.nextLine();
 	 		if(database_password.equalsIgnoreCase(""))
 	 			database_password = serverConnection.database_password;
 	 		
 	 		/*Populate Weathers Question*/
 	 		print("Populate Weathers[y/n]? ");
 	 		int ticker3 = 1;
 	 		while(incorrectInput(in.nextLine(), "y/n")) {
 	 			if(ticker3>=5) { 
 	 				println("\nToo many incorrect inputs...\nTerminating program");
 	 				System.exit(0);
 	 				break;
 	 			}
 	 			else
 	 				println("\nIncorrect input: Y/N/S are the only acceptable inputs");
 	 			
 	 			ticker3++;
 	 		}
 	 		if(userResponse.equalsIgnoreCase("y")) 
 	 			update_weather = true;
 	 		if(userResponse.equalsIgnoreCase("n")) 
 	 			update_weather = false;
 	 		
 	 		/*Populate Populations Question*/
 	 		print("Populate Populations[y/n]? ");
 	 		int ticker4 = 1;
 	 		while(incorrectInput(in.nextLine(), "y/n")) {
 	 			if(ticker4>=5) { 
 	 				println("\nToo many incorrect inputs...\nTerminating program");
 	 				System.exit(0);
 	 				break;
 	 			}
 	 			else
 	 				println("\nIncorrect input: Y/N/S are the only acceptable inputs");
 	 			
 	 			ticker3++;
 	 		}
 	 		if(userResponse.equalsIgnoreCase("y")) 
 	 			update_population = true;
 	 		if(userResponse.equalsIgnoreCase("n")) 
 	 			update_population = false;
 	 		
 	 		in.close();
 	 		
 	 		/*Start Connection*/
 	 		print("\nStarting task\n");
 			ServerHandler userServerConnection = new ServerHandler(server_name, database_name, database_user, database_password);
 			
 			return userServerConnection;
 		}
 		/*No Case*/
 		else if(userResponse.equalsIgnoreCase("n")) {
 			in.close();
 			print("\nStarting task\n");
 			
 			return serverConnection;
 		}
 		/*See Current Configurations Case*/
 		else {
 			/*Show Current Configurations*/
 			print("\nServer:   " + serverConnection.server_name 
 			    + "\nDatabase: " + serverConnection.database_name
 			    + "\nUsername: " + serverConnection.database_user
 			    + "\nPassword: " + "******"
 			    + "\n\nDo you want to edit these configurations? [Y/N]\n");
 			
 			/*Change Configuration Questions*/
 			int ticker2 = 1;
 	 		while(incorrectInput(in.nextLine(), "y/n")) {
 	 			if(ticker2>=5) { 
 	 				println("\nToo many incorrect inputs...\nTerminating program");
 	 				System.exit(0);
 	 				break;
 	 			}
 	 			else
 	 				println("\nIncorrect input: Y/N/S are the only acceptable inputs");
 	 			
 	 			ticker2++;
 	 		}
 	 		
 	 		/*Yes Case*/
 	 		if(userResponse.equalsIgnoreCase("y")) {
 	 			/*Server Question*/
 	 			print("\nType <default> to keep configuration's default value\nServer:   ");
 	 	 		String server_name = in.nextLine();
 	 	 		if(server_name.equalsIgnoreCase("default"))
 	 	 			server_name = serverConnection.server_name;
 	 	 		
 	 	 		/*Database Question*/
 	 	 		print("Database: ");
 	 	 		String database_name = in.nextLine();
 	 	 		if(database_name.equalsIgnoreCase("default"))
 	 	 			database_name = serverConnection.database_name;
 	 	 		
 	 	 		/*Username Question*/
 	 	 		print("Username: ");
 	 	 		String database_user = in.nextLine();
 	 	 		if(database_user.equalsIgnoreCase("default"))
 	 	 			database_user = serverConnection.database_user;
 	 	 		
 	 	 		/*Password Question*/
 	 	 		print("Password: ");
 	 	 		String database_password = in.nextLine();
 	 	 		if(database_password.equalsIgnoreCase("default"))
 	 	 			database_password = serverConnection.database_password;
 	 	 		
 	 	 		/*Populate Weathers Question*/
 	 	 		print("Populate Weathers[y/n]? ");
 	 	 		int ticker3 = 1;
 	 	 		while(incorrectInput(in.nextLine(), "y/n")) {
 	 	 			if(ticker3>=5) { 
 	 	 				println("\nToo many incorrect inputs...\nTerminating program");
 	 	 				System.exit(0);
 	 	 				break;
 	 	 			}
 	 	 			else
 	 	 				println("\nIncorrect input: Y/N/S are the only acceptable inputs");
 	 	 			
 	 	 			ticker3++;
 	 	 		}
 	 	 		if(userResponse.equalsIgnoreCase("y")) 
 	 	 			update_weather = true;
 	 	 		if(userResponse.equalsIgnoreCase("n")) 
 	 	 			update_weather = false;
 	 	 		
 	 	 		/*Populate Populations Question*/
 	 	 		print("Populate Populations[y/n]? ");
 	 	 		int ticker4 = 1;
 	 	 		while(incorrectInput(in.nextLine(), "y/n")) {
 	 	 			if(ticker4>=5) { 
 	 	 				println("\nToo many incorrect inputs...\nTerminating program");
 	 	 				System.exit(0);
 	 	 				break;
 	 	 			}
 	 	 			else
 	 	 				println("\nIncorrect input: Y/N/S are the only acceptable inputs");
 	 	 			
 	 	 			ticker3++;
 	 	 		}
 	 	 		if(userResponse.equalsIgnoreCase("y")) 
 	 	 			update_population = true;
 	 	 		if(userResponse.equalsIgnoreCase("n")) 
 	 	 			update_population = false;
 	 	 		
 	 	 		in.close();
 	 	 		
 	 	 		/*Create Server Connection with User Determined Configurations
 	 	 		  Show Current Configurations*/
 	 	 		print("\nStarting task with these updated configurations"
 	 	 		    + "\nServer:   " + server_name 
 	 			    + "\nDatabase: " + database_name
 	 			    + "\nUsername: " + database_user
 	 			    + "\nPassword: " + database_password
 	 			    + "\nStarting task...");
 	 	 		
 	 			ServerHandler userServerConnection = new ServerHandler(server_name, database_name, database_user, database_password);
 	 			
 	 			return userServerConnection;
 	 		}
 	 		/*Configurations seen but no changes will be made*/
 	 		else {
 	 			in.close();
 	 			print("\nStarting task...\n");
 	 			
 	 			return serverConnection;
 	 		}	
 		}
 	}
 	
 	public static boolean incorrectInput(String in, String schema) {
 		/*This variable lives*/
 		userResponse = in;
 		
 		switch(schema){
 			case "y/n/s": 
 				if(in.equalsIgnoreCase("y") || in.equalsIgnoreCase("n")||in.equalsIgnoreCase("s"))
 					return false;
 				else
 					return true;
 			case "y/n": 
 				if(in.equalsIgnoreCase("y") || in.equalsIgnoreCase("n"))
					return false;
				else
					return true;
 			default: print("incorrect schema (fix in project)... Terminating now");
 				System.exit(0);
 		}
 		
 		return true;
 	}
 	
	/*-------------------------Utilities-----------------------*/
	public static void print(Object print) {System.out.print(print);}
	
	public static void println(Object print) {System.out.println(print);}
	
	public static void println() {System.out.print("\n");}
}








































