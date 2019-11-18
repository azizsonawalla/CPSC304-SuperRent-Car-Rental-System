package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/clerkReservationRentalSearch.fxml"));
        primaryStage.setTitle(GUIConfig.APP_TITLE);
        Scene scene = new Scene(root, GUIConfig.WINDOW_WIDTH, GUIConfig.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("views/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
