package app.lsgui.gui.main;

import app.lsgui.utils.Settings;
import javafx.scene.control.ComboBox;

public final class QualityComboBox extends ComboBox<String> {

    private static final String OFFLINEQUALITY = "Channel is offline";

    public QualityComboBox() {
        // Empty Constructor
    }

    public void initialize() {
        getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (!OFFLINEQUALITY.equals(newValue)) {
                Settings.getInstance().setQuality(newValue);
            }
        });
    }

}
