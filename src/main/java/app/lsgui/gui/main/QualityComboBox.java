package app.lsgui.gui.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.utils.Settings;
import javafx.scene.control.ComboBox;

public final class QualityComboBox extends ComboBox<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QualityComboBox.class);
    private static final String OFFLINEQUALITY = "Channel is offline";

    public QualityComboBox() {
        // Empty Constructor
    }

    public void initialize() {
        getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (!OFFLINEQUALITY.equals(newValue)) {
                LOGGER.debug("Set selected Quality to {}", newValue);
                Settings.getInstance().setQuality(newValue);
            }
        });
    }

}
