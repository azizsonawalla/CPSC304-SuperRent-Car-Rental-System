package gui;

public class Config {

    static int WINDOW_WIDTH = 1000;
    static int WINDOW_HEIGHT = 800;

    static String APP_TITLE = "SuperRent Car Rental System";

    /* Scene mappings */
    private static String FXML_PATH_TEMPLATE = "views/%s.fxml";
    // Customer
    public static String CUSTOMER_CAR_SEARCH = String.format(FXML_PATH_TEMPLATE, "customerCarSearch");
    public static String CUSTOMER_MAKE_RES = String.format(FXML_PATH_TEMPLATE, "customerMakeReservation");
    // Clerk
    public static String CLERK_RESERVATION_RENTAL_SEARCH = String.format(FXML_PATH_TEMPLATE, "clerkReservationRentalSearch");
    public static String CLERK_CAR_SEARCH = String.format(FXML_PATH_TEMPLATE, "clerkCarSearch");
    public static String CLERK_MAKE_RESERVATION = String.format(FXML_PATH_TEMPLATE, "clerkMakeReservation");
    public static String CLERK_START_RENTAL = String.format(FXML_PATH_TEMPLATE, "clerkStartRental");
    public static String CLERK_SUBMIT_RETURN = String.format(FXML_PATH_TEMPLATE, "clerkSubmitReturn");
    public static String CLERK_DAILY_REPORTS = String.format(FXML_PATH_TEMPLATE, "clerkDailyReports");
}
