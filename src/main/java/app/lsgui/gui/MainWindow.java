package app.lsgui.gui;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.service.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainWindow extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);
    private static FXMLLoader loader;

    @Override
    public void init() {
        loader = new FXMLLoader();
        Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Uncaught Exception on JavaFX Thread", e);
                LOGGER.error("Exiting JavaFX Thread...");
                Platform.exit();
            }
        });
        Platform.setImplicitExit(false);
        Settings.instance();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = loader.load(getClass().getResourceAsStream(("/MainWindow.fxml")));
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/lightStyle.css").toString());

            primaryStage.setTitle("Livestreamer GUI v3.0");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Settings.instance().saveSettings();
                Platform.exit();
            }
        });

        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Settings.instance().saveSettings();
                Platform.exit();
            }
        });
    }

}
