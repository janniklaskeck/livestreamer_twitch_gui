package app.lsgui.gui;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.streamInfoPanel.ChannelInfoPanel;
import app.lsgui.gui.streamlist.ChannelList;
import app.lsgui.model.Service;
import app.lsgui.model.Channel;
import app.lsgui.service.Settings;
import app.lsgui.service.twitch.TwitchProcessor;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.util.StringConverter;

public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private ChannelList streamList;
    private ChannelInfoPanel streamInfoPanel;

    @FXML
    private ComboBox<String> qualityComboBox;

    @FXML
    private ComboBox<Service> serviceComboBox;

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

        streamList = new ChannelList();
        streamInfoPanel = new ChannelInfoPanel(serviceComboBox, qualityComboBox);
        streamInfoPanel.getModelProperty().bind(streamList.getModelProperty());

        streamList.getListView().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    Channel value = newValue == null ? oldValue : newValue;
                    qualityComboBox.setItems(FXCollections.observableArrayList(value.getAvailableQualities()));
                    if (qualityComboBox.getItems().size() > 1) {
                        qualityComboBox.getSelectionModel().select("best");
                    } else {
                        qualityComboBox.getSelectionModel().select(0);
                    }
                });

        if (Settings.instance().getStreamServices().isEmpty()) {
            Settings.instance().getStreamServices().add(new Service("Twitch.tv", "http://twitch.tv/"));
        }
        serviceComboBox.getItems().addAll(Settings.instance().getStreamServices());
        serviceComboBox.setCellFactory(listView -> new ServiceCell());
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service object) {
                if (object == null) {
                    return null;
                }
                return object.getName().get();
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });
        serviceComboBox.getSelectionModel().select(0);
        streamList.getStreams().bind(serviceComboBox.getSelectionModel().getSelectedItem().getChannels());
        serviceComboBox.valueProperty().addListener((observable, oldValue, newValue) -> changeService(newValue));

        contentBorderPane.setLeft(streamList);
        contentBorderPane.setCenter(streamInfoPanel);

        Button addButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS_SQUARE);
        addButton.setOnAction(event -> addAction());
        Button removeButton = GlyphsDude.createIconButton(FontAwesomeIcon.MINUS_SQUARE);
        removeButton.setOnAction(event -> removeAction());
        Button importButton = GlyphsDude.createIconButton(FontAwesomeIcon.USERS);
        importButton.setOnAction(event -> importStreams());
        toolBarLeft.getItems().add(addButton);
        toolBarLeft.getItems().add(removeButton);
        toolBarLeft.getItems().add(importButton);

        Button settingsButton = GlyphsDude.createIconButton(FontAwesomeIcon.COG);
        settingsButton.setOnAction(event -> openSettings());
        toolBarRight.getItems().add(settingsButton);
    }

    private void changeService(final Service newService) {
        LOGGER.debug("Change Service to {}", newService.getName().get());
        streamList.getStreams().bind(newService.getChannels());
    }

    private void openSettings() {
        LOGGER.debug("Settings not implemented");
    }

    private void addAction() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Add Stream to current Service");
        ButtonType bt = new ButtonType("Submit", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bt, ButtonType.CANCEL);

        BorderPane ap = new BorderPane();
        TextField tf = new TextField();
        ap.setCenter(tf);
        dialog.getDialogPane().setContent(ap);

        Node submitButton = dialog.getDialogPane().lookupButton(bt);
        submitButton.setDisable(true);

        tf.textProperty()
                .addListener((observable, oldValue, newValue) -> submitButton.setDisable(newValue.trim().isEmpty()));

        dialog.setResultConverter(button -> {
            if (TwitchProcessor.instance().channelExists(tf.getText().trim()) && !"".equals(tf.getText().trim())) {
                return true;
            }
            return false;
        });

        Platform.runLater(() -> tf.requestFocus());

        Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent() && result.get()) {
            serviceComboBox.getSelectionModel().getSelectedItem().addStream(tf.getText().trim());
        }
    }

    private void removeAction() {
        Channel toRemove = streamList.getListView().getSelectionModel().getSelectedItem();
        serviceComboBox.getSelectionModel().getSelectedItem().removeSelectedStream(toRemove);
    }

    private void importStreams() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Import Twitch.tv followed Streams");
        dialog.setContentText("Please enter your Twitch.tv Username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            serviceComboBox.getSelectionModel().getSelectedItem().addFollowedStreams(result.get());
        }
    }
}
