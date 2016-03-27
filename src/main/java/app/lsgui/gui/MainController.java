package app.lsgui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.streamInfoPanel.StreamInfoPanel;
import app.lsgui.gui.streamList.StreamList;
import app.lsgui.serviceapi.twitch.TwitchProcessor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;

public class MainController {

    private static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static StreamList streamList;
    private static StreamInfoPanel streamInfoPanel;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button settingsButton;

    @FXML
    private ComboBox<Void> qualityComboBox;

    @FXML
    private ComboBox<Void> serviceComboBox;

    @FXML
    private BorderPane contentBorderPane;

    @FXML
    public void initialize() {
        LOGGER.debug("INIT MainController");

        streamList = new StreamList();
        streamInfoPanel = new StreamInfoPanel(null);

        contentBorderPane.setLeft(streamList);
        contentBorderPane.setCenter(streamInfoPanel);
    }

    @FXML
    private void addAction() {

    }

    @FXML
    private void removeAction() {

    }

    @FXML
    private void onSettingsClicked() {

    }

    @FXML
    private void importStreams() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Import Twitch.tv followed Streams");
        dialog.setContentText("Please enter your Twitch.tv Username:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Set<String> list = TwitchProcessor.instance().getListOfFollowedStreams(result.get());
                    List<String> list2 = new ArrayList<String>();
                    list2.addAll(list);
                    streamList.getStreams()
                            .setValue(FXCollections.observableArrayList(FXCollections.observableList(list2)));
                }
            });
        }
    }
}
