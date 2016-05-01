package app.lsgui.gui.chat;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainWindow;
import app.lsgui.gui.settings.SettingsWindow;
import app.lsgui.service.Settings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChatWindow {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsWindow.class);
    private Stage chatStage;
    private String channel;
    private FXMLLoader loader;

    public ChatWindow(final String channel) {
        this.channel = channel;
        chatStage = new Stage();

        loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/ChatWindow.fxml"));
        Parent root = loadFXML();
        setupStage(root, chatStage);
    }

    private Parent loadFXML() {
        try {
            return loader.load();
        } catch (IOException e) {
            LOGGER.error("ERROR while loading chat fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Stage chatStage) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ChatWindow.class
                .getResource("/styles/" + Settings.instance().getWindowStyle() + ".css").toExternalForm());

        chatStage.setMinHeight(400.0);
        chatStage.setMinWidth(600.0);

        chatStage.getProperties().put("channel", channel);
        chatStage.setTitle("Livestreamer GUI v3.0 Chat - " + channel);
        chatStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        chatStage.setScene(scene);
        chatStage.initModality(Modality.NONE);
        chatStage.show();

        chatStage.setOnCloseRequest(event -> ((ChatController) loader.getController()).disconnect());
    }

    public void connect() {
        loader.<ChatController> getController().connect();
    }

}
