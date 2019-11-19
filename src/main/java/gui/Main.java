package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    private Scene scene;
    private Map<String, Pair<Pane, Controller>> scenePanes;
    public ExecutorService pool;

    public Main() {
        super();
        pool = Executors.newFixedThreadPool(2*Runtime.getRuntime().availableProcessors()+1);
        scenePanes = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        initializeSceneControllers();
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

    public void setRoot(String fxml) {
        Pane root = scenePanes.get(fxml).getKey();
        scenePanes.get(fxml).getValue().refreshAll();
        scene.setRoot(root);
    }

    private void initializeSceneControllers() throws Exception {
        String fxml;
        Controller c;

        fxml = GUIConfig.CUSTOMER_CAR_SEARCH;
        c = new customerCarSearch(this);
        scenePanes.put(fxml, new Pair<Pane, Controller>(getPane(fxml, c), c));

        fxml = GUIConfig.CLERK_RENTAL_RES_SEARCH;
        c = new clerkReservationRental(this);
        scenePanes.put(fxml, new Pair<Pane, Controller>(getPane(fxml, c), c));
        // TODO: add others
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
