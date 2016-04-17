package app.lsgui.gui.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainController;
import app.lsgui.service.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @FXML
    private CheckBox sortCheckBox;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField oauthTextField;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");

        Settings st = Settings.instance();

        sortCheckBox.setSelected(st.getSortTwitch().get());
        oauthTextField.setText(st.getTwitchOAuth());
        usernameTextField.setText(st.getTwitchUser());

        sortCheckBox.setOnAction(event -> {
            st.getSortTwitch().setValue(sortCheckBox.isSelected());
            
        });
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> st.setTwitchUser(newValue));
        oauthTextField.textProperty().addListener((observable, oldValue, newValue) -> st.setTwitchOAuth(newValue));
    }

    @FXML
    protected void cancelSettingsAction() {
        SettingsWindow.getSettingsStage().close();
    }

    @FXML
    protected void saveSettingsAction() {
        Settings.instance().saveSettings();
        SettingsWindow.getSettingsStage().close();
    }

}
