package gui;

import gui.controllers.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Entities.Rental;
import model.Entities.Reservation;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private Scene scene;
    public Map<String, Pair<Pane, Controller>> scenePanes;

    /* Inter-scene data */
    // Customer Car Search -> Customer Make Reservation
    public Reservation customerResInProgress;
    // Clerk Res Rental Search -> Clerk Start Rental
    public Reservation clerkReservationToStart;
    // Clerk Res Rental Search -> Clerk Start Return
    public Rental clerkRentalToBeReturned;
    // Clerk Car Search -> Clerk Make Reservation
    public Reservation clerkResInProgress;


    public Main() {
        super();
        scenePanes = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        // show splash
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/splash.fxml"));
        loader.setController(new splash());
        Pane root = loader.load();
        this.scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        primaryStage.setTitle(Config.APP_TITLE);
        scene.getStylesheets().add(getClass().getResource("views/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        Platform.runLater(initializeAndSet);
        primaryStage.show();
    }

    public void switchScene(String fxml) {
        Pane root = scenePanes.get(fxml).getKey();
        scenePanes.get(fxml).getValue().refreshAll();
        scene.setRoot(root);
    }

    private void initializeScenePanesMap() throws Exception {
        String fxml;
        Controller c;

        // Customer
        fxml = Config.CUSTOMER_CAR_SEARCH;
        c = new customerCarSearch(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = Config.CUSTOMER_MAKE_RES;
        c = new customerMakeReservation(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        // Clerk
        fxml = Config.CLERK_RESERVATION_RENTAL_SEARCH;
        c = new clerkReservationRentalSearch(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = Config.CLERK_CAR_SEARCH;
        c = new clerkCarSearch(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = Config.CLERK_DAILY_REPORTS;
        c = new clerkDailyReports(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = Config.CLERK_MAKE_RESERVATION;
        c = new clerkMakeReservation(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = Config.CLERK_START_RENTAL;
        c = new clerkStartRental(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = Config.CLERK_SUBMIT_RETURN;
        c = new clerkSubmitReturn(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));
    }

    private Pane getPane(String fxml, Controller c) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setController(c);
        return loader.load();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private Runnable initializeAndSet = () -> {
        try {
            this.initializeScenePanesMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switchScene(Config.CUSTOMER_CAR_SEARCH);
    };

    public static void main(String[] args) {
        launch(args);
    }
}
