# CPSC304-SuperRent-Car-Rental-System

A cloud-connected rental car management system

Download the desktop application:  [Windows Executable]()   |   [JAR File]()  (Mac/Windows)     # TODO: Add links to .exe and .jar

> NOTE! You need an internet connection to use this application since the database is hosted in the cloud.

In this README:

* Run the application
    * Using the Windows Executable
    * Using the JAR file (requires Java 8)
    * In IntelliJ IDE (requires Maven plugin)
    
* Where to find in the code:
    * All the SQL queries
    * Establishing connection to DB and JDBC calls
    * UI Components
    
* How to use the application:

    // TODO: Attach wireframe here if time permits
    
    * Customer:
        * View the number of available cars for a given car type/location/time
        * See details of the specific cars for a given car type/location
        * Make a reservation
    * Clerk/Staff:
        * Switch to the Clerk view
        * View active rentals and reservations and filter by confirmation number and customer
        * Start a rental for an active reservation
        * Create a return for an active rental
        * View Daily Reports:
            * View Daily Reports for Rentals:
                * For all locations
                * For a specific location
            * View Daily Reports for Returns:
                * For all locations
                * For a specific location
    * Errors and troubleshooting:
        * What happens if I put an invalid value?
        * Database connection error