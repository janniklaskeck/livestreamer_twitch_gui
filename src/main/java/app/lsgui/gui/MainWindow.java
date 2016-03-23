package app.lsgui.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainWindow extends Application {

    private static FXMLLoader loader;

    @Override
    public void init() {
        loader = new FXMLLoader();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = loader.load(getClass().getResourceAsStream(("/MainWindow.fxml")));
            Scene scene = new Scene(root);

            //scene.getStylesheets().add(getClass().getResource("lightStyle.css").toString());

            primaryStage.setTitle("Livestreamer GUI v3.0");
            //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icon.jpg")));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
