package app.lsgui.gui.chat;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.settings.Settings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class ChatWindow {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatWindow.class);
    private static Stage chatStage;
    private String channel;
    private FXMLLoader loader;

    public ChatWindow(final String channel) {
        this.channel = channel;
        setChatStage(new Stage());

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

    private void setupStage(final Parent root, final Stage stage) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ChatWindow.class
                .getResource("/styles/" + Settings.instance().getWindowStyle() + ".css").toExternalForm());

        stage.setMinHeight(400.0);
        stage.setMinWidth(600.0);

        stage.getProperties().put("channel", channel);
        stage.setTitle(channel + " - Livestreamer GUI v3.0 Chat");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        stage.setScene(scene);
        stage.initModality(Modality.NONE);
        stage.show();

        stage.setOnCloseRequest(event -> {
            ((ChatController) loader.getController()).disconnect();
            setChatStage(null);
        });
    }

    /**
     * Start Connecting to IRC Server
     */
    public void connect() {
        loader.<ChatController> getController().connect();
    }

    public static Stage getChatStage() {
        return chatStage;
    }

    private static void setChatStage(final Stage stage) {
        chatStage = stage;
    }
}
