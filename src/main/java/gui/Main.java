package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    Scene scene;
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/customerCarSearch.fxml"));
        loader.setController(new customerCarSearch(this));
        Pane root = loader.load();
        primaryStage.setTitle(GUIConfig.APP_TITLE);
        this.scene = new Scene(root, GUIConfig.WINDOW_WIDTH, GUIConfig.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("views/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        this.primaryStage = primaryStage;
    }

    public void setRoot(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setController(new Controller(this));
        Pane root = loader.load();
        scene.setRoot(root);
        //primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
