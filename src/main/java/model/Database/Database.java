package model.Database;

import model.Util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Interface for the SuperRent database
 */
public class Database {

    private String HOST = "jdbc:mysql://35.247.80.246/superrent";
    private String USERNAME = "root";
    private String PASSWORD = "bobobo";

    private Connection conn;

    /**
     * Constructor for Database class. Establishes a connection to the database and initializes the connection field.
     * @throws Exception if there was an error connecting to the database
     */
    public Database() throws Exception {
        Log.log("Establishing connection to database...");
        try {
            this.conn = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
            if (this.conn == null) throw new Exception("Connection object is null");
        } catch (Exception e) {
            throw new Exception("Error getting connection to database", e);
        }
        Log.log("Successfully connected to database!");
    }

    /**
     * Checks all the tables required for the system and creates them if they don't exist
     * @throws Exception if there was an error creating any of the tables
     */
    public void createTables() throws Exception {
        // TODO: implement this
        // NOTE: Use the queries set in the static Queries class, for example:
        String query = Queries.CreateTables.CREATE_TABLE_RESERVATIONS;
        throw new Exception("Method createTables() is not implemented");
    }



}
