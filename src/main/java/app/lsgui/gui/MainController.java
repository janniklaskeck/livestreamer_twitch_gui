package app.lsgui.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.streamInfoPanel.StreamInfoPanel;
import app.lsgui.gui.streamList.StreamList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private ComboBox qualityComboBox;

    @FXML
    private ComboBox serviceComboBox;

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

}
