# CPSC304-SuperRent-Car-Rental-System

A cloud-connected rental car management system

Download the desktop application:  [Windows Executable]()   |   [JAR File]()  (Mac/Windows)     # TODO: Add links to .exe and .jar

> NOTE! You need an internet connection to use this application since the database is hosted in the cloud.

In this README:

* [Run the application](#run-the-application)
    * Using the Windows Executable
    * Using the JAR file (requires Java 8)
    * In IntelliJ IDE (requires Maven plugin)

* [Where to find in the code:](#where-to-find-in-the-code)
    * [All the SQL queries](./src/main/java/model/Queries.java) 
    * [Establishing connection to DB and JDBC calls](./src/main/java/model/Database.java)
    * [UI Components.](./src/main/java/gui) We used JavaFx, the fxml files are in [views](./src/main/java/gui/views), 
    while the UI components are in [controllers](./src/main/java/gui/controllers).

* [How to use the application:](#how-to-use-the-application)

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
## Run the application

## Where to find in the code

## How to use the application
#### Customer:
* View the number of available cars for a given car type/location/time:
You must indicate the time period that you are interested in, and then you can use the drop down lists to refine the search
results. You can choose a specific car type, and a location.

![Diagram 1](./pictures/pic1.png "AnyBranch, AnyType, Customer Find Car")

![Diagram 2](./pictures/pic2.png "Customer Finds CarType, Looks at Car and then Chooses to Reserve")

After choosing the car, the customer clicks on Start Reservation, and then they are moved to the next window where they can enter their info

![Diagram 3](./pictures/pic3.png "Customer Reserves, if Exisitng, it auto-fills the fields")

I the customer is an existing one, then their fields are auto filled, otherwise, they fill the fields and are added to the system.
You'll also notice that the customer gets back their reservation confirmation number. 

#### Clerk:
When the GUI launches, it automatically goes into the Customer view. To change to the Clerk view, you have to on the Clerk View

![Diagram 4](./pictures/pic4.png "To Move to Clerk View Click Here")

 This is the screen that the clerk gets. It is split into a section for the ongoing reservations, which includes every Reservation with an end date in the future,
 and a car that has not been picked up yet. And another section which is ongoing rentals, which includes all the cars that have been rented out but not yet returned.
 
 ![Diagram 5](./pictures/pic5.png "Clerk View")
 
 To start a rental without a reservation , the clerk clicks on Start Rental Without Reservation
 
 ![Diagram 6](./pictures/pic6.png "Start without Reservation ")
 
This will take them to a Reservation screen similar to the one that the customer would get, except without the choice to
 choose a start time since that would be set to the current time. And once they pick the car type they want to reserve, they will be sent to a new screen to add the 
 information of the customer.
 
 ![Diagram 7](./pictures/pic7.png "Clerk View")

If the Reservation did exis
