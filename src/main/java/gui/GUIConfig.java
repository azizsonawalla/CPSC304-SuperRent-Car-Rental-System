package gui;

public class GUIConfig {

    static int WINDOW_WIDTH = 1000;
    static int WINDOW_HEIGHT = 800;

    static String APP_TITLE = "SuperRent Car Rental System";

    /* Scene mappings */
    private static String FXML_PATH_TEMPLATE = "views/%s.fxml";
    // Customer
    public static String CUSTOMER_CAR_SEARCH = String.format(FXML_PATH_TEMPLATE, "customerCarSearch");
    // Clerk
    public static String CLERK_RENTAL_RES_SEARCH = String.format(FXML_PATH_TEMPLATE, "clerkReservationRentalSearch");
}
