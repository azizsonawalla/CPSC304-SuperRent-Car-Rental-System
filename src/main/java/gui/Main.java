package gui;

import gui.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Entities.Reservation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    private Scene scene;
    public Map<String, Pair<Pane, Controller>> scenePanes;
    public ExecutorService pool;

    // Inter-scene data
    public Reservation customerResInProgress;

    public Main() {
        super();
        pool = Executors.newFixedThreadPool(2*Runtime.getRuntime().availableProcessors()+1);
        scenePanes = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        initializeScenePanesMap();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/customerCarSearch.fxml"));
        loader.setController(new customerCarSearch(this));
        Pane root = loader.load();
        primaryStage.setTitle(GUIConfig.APP_TITLE);
        this.scene = new Scene(root, GUIConfig.WINDOW_WIDTH, GUIConfig.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("views/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
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
        fxml = GUIConfig.CUSTOMER_CAR_SEARCH;
        c = new customerCarSearch(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = GUIConfig.CUSTOMER_MAKE_RES;
        c = new customerMakeReservation(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        // Clerk
        fxml = GUIConfig.CLERK_RESERVATION_RENTAL_SEARCH;
        c = new clerkReservationRentalSearch(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = GUIConfig.CLERK_CAR_SEARCH;
        c = new clerkCarSearch(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = GUIConfig.CLERK_DAILY_REPORTS;
        c = new clerkDailyReports(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = GUIConfig.CLERK_MAKE_RESERVATION;
        c = new clerkMakeReservation(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = GUIConfig.CLERK_START_RENTAL;
        c = new clerkStartRental(this);
        scenePanes.put(fxml, new Pair<>(getPane(fxml, c), c));

        fxml = GUIConfig.CLERK_SUBMIT_RETURN;
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
        pool.shutdownNow();
        // TODO: Close all db connections
    }

    public static void main(String[] args) {
        launch(args);
    }
}
