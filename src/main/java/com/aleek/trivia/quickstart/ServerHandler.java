package com.aleek.trivia.quickstart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ServerHandler {
	public String server_name, database_name, database_user, database_password, database_locator, table_name=null;
	public String[] table = {"TADcityweathers", "TADcountry_populations"};
	private static Connection database_connection;
	static Statement database_statement = null;
	
	public ServerHandler() {
		super();
		/*Server Connection Declarations*/
		server_name = "Yoga-T";
		database_name = "Trivia_App_Database";
		table_name = "TADcityweathers";
		database_user = "populator";
		database_password = "Only4thepopulator";
		database_locator = "jdbc:sqlserver://" + server_name 
				+ ";databaseName=" + database_name 
				+ ";user=" + database_user 
				+ ";password=" + database_password;
		
		if(table_name==null)
			table_name = table[0];
	}
	
	public ServerHandler(String server_name, String database_name, String database_user, String database_password) {
		super();
		this.server_name = server_name;
		this.database_name = database_name;
		this.database_user = database_user;
		this.database_password = database_password;
		
		if(table_name==null) 
			table_name = table[0];
		
		database_locator = "jdbc:sqlserver://" + server_name 
				+ ";databaseName=" + database_name 
				+ ";user=" + database_user 
				+ ";password=" + database_password;
	}
	
	/*-------------------------Drivers-------------------------*/
	public Boolean initializeDatabaseCommunication() {
		Boolean isHealthy = false;
		
		/*Connection Health Test*/
		println("Environment testing has started---------");
		println("Connection Health Test Started");
		println("Testing connection health to jdbc:sqlserver://" + server_name 
				+ ";databaseName=" + database_name 
				+ ";user=" + database_user 
				+ ";password=" + "*****");
		
		try {
			connectDatabase();
			println("Connection Health Test Succesful: Nice! connected no problems...");
			isHealthy=true;/*<--Health Signs*/
		}
		catch (SQLException e) {
			println("Could not connect to Host Server...L\n"
				  + "Health check failed...check stacktrace");
			isHealthy=false;/*<--NOT Health Signs*/
			e.printStackTrace();
			return isHealthy;
		}
		catch (Exception e) {
			println("Unexpected Exception...L\n"
					  + "Health check failed...check stacktrace");
			isHealthy=false;/*<--NOT Health Signs*/
			e.printStackTrace();
			return isHealthy;
		}
		
		
		/*Purge Health Test*/
		println("Purge Health Test Started");
		try{
			purgeTable();
			isHealthy=true;/*<--Health Signs*/
			println("Purge Health Test Succesful: Looking healthy so far...");
		} 
		catch(Exception e) {
			println("Health check failed...check stacktrace");
			isHealthy=false;/*<--NOT Health Signs*/
			e.printStackTrace();
			return isHealthy;
		}
		
		/*Insertion Health Test*/
		println("Insertion Health Test Started");
		try {
				/*SQL Statement {Test Insert}*/
				String SQL = "INSERT INTO " + table_name + " (ID, TestColumn) "
						   + "VALUES (69, 'Test Insert')";
				
				/*Execute SQL statement*/
				database_statement.execute(SQL);
				println("--SQL STATEMENT: " + SQL);
				
				/*Sign health*/
				println("Insertion Health Test Succesful: Everything looks good ready to work...");
				purgeTable();isHealthy=true;
			} 
		catch (SQLException e) {
			println("Something wrong with the SQL statement");
			isHealthy=false;e.printStackTrace();
			return isHealthy;
		} 
		catch (Exception e) {
			println("~~UNEXPECTED ERROR: Review stack trace~~");
			e.printStackTrace();isHealthy=false;
			return isHealthy;
		}

		println("Environment testing: Environment testing complete and healthy");
		/*Return healthy status*/
		return isHealthy;
	}

	public void purgeTable() throws SQLException {
		/*--Purge table*/
		String SQL = "DELETE FROM " + table_name;
		database_statement.execute(SQL);
		println("--SQL STATEMENT: " + SQL);
	}

	public void connectDatabase() throws SQLException{		
		/*--Connection to Server*/
		println("Connecting to database: Establishing connection");
	
		database_connection = DriverManager.getConnection(database_locator); 
		database_statement = database_connection.createStatement();
		
		println("Connecting to database: Connection successful");
	}

	/*-------------------------Utilities-----------------------*/
	public static void print(Object print) {System.out.print(print);}
	
	public static void println(Object print) {System.out.println(print);}
	
	public static void println() {System.out.print("\n");}
}
