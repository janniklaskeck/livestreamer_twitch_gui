package app.lsgui.gui;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.streamInfoPanel.StreamInfoPanel;
import app.lsgui.gui.streamList.StreamList;
import app.lsgui.service.twitch.TwitchProcessor;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public class MainController {

    private static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static StreamList streamList;
    private static StreamInfoPanel streamInfoPanel;

    @FXML
    private ComboBox<Void> qualityComboBox;

    @FXML
    private ComboBox<Void> serviceComboBox;

    @FXML
    private BorderPane contentBorderPane;

    @FXML
    private ToolBar toolBarLeft;

    @FXML
    private ToolBar toolBarRight;

    @FXML
    private Button importStreamsButton;

    @FXML
    public void initialize() {
        LOGGER.debug("INIT MainController");

        streamList = new StreamList();
        streamInfoPanel = new StreamInfoPanel(null);

        contentBorderPane.setLeft(streamList);
        contentBorderPane.setCenter(streamInfoPanel);

        Button addButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS_SQUARE);
        addButton.setOnAction(event -> {
            addAction();
        });
        Button removeButton = GlyphsDude.createIconButton(FontAwesomeIcon.MINUS_SQUARE);
        removeButton.setOnAction(event -> {
            removeAction();
        });
        Button importButton = GlyphsDude.createIconButton(FontAwesomeIcon.USERS);
        importButton.setOnAction(event -> {
            importStreams();
        });
        toolBarLeft.getItems().add(addButton);
        toolBarLeft.getItems().add(removeButton);
        toolBarLeft.getItems().add(importButton);

        Button settingsButton = GlyphsDude.createIconButton(FontAwesomeIcon.COG);
        settingsButton.setOnAction(event -> openSettings());
        toolBarRight.getItems().add(settingsButton);
    }

    private void openSettings() {

    }

    private void addAction() {
        Dialog<Boolean> dialog = new Dialog<Boolean>();
        dialog.setTitle("Add Stream to current Service");
        ButtonType bt = new ButtonType("Submit", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bt, ButtonType.CANCEL);

        BorderPane ap = new BorderPane();
        TextField tf = new TextField();
        ap.setCenter(tf);
        dialog.getDialogPane().setContent(ap);

        Node submitButton = dialog.getDialogPane().lookupButton(bt);
        submitButton.setDisable(true);

        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(button -> {
            if (TwitchProcessor.instance().channelExists(tf.getText().trim()) && !tf.getText().trim().equals("")) {
                return true;
            }
            return false;
        });

        Platform.runLater(() -> tf.requestFocus());

        Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get()) {
                streamList.addStream(tf.getText().trim());
            }
        }
    }

    private void removeAction() {
        streamList.removeSelectedStream();
    }

    private void importStreams() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Import Twitch.tv followed Streams");
        dialog.setContentText("Please enter your Twitch.tv Username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            streamList.addFollowedStreams(result.get());
        }
    }
}
