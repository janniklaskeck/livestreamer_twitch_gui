package app.lsgui.gui.settings;

import org.controlsfx.control.PropertySheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private PropertySheet propertySheet;

    @FXML
    private AnchorPane settingsAnchorPane;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");

        propertySheet = new PropertySheet();


        settingsAnchorPane.getChildren().add(propertySheet);
        AnchorPane.setTopAnchor(propertySheet, 0.0);
        AnchorPane.setBottomAnchor(propertySheet, 0.0);
        AnchorPane.setRightAnchor(propertySheet, 0.0);
        AnchorPane.setLeftAnchor(propertySheet, 0.0);

    }

}
