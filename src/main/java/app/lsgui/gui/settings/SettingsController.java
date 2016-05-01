package app.lsgui.gui.settings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainController;
import app.lsgui.gui.MainWindow;
import app.lsgui.service.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
    private ChoiceBox<String> styleChoiceBox;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");
        setupStyleChoiceBox();
        Settings st = Settings.instance();

        sortCheckBox.setSelected(st.getSortTwitch().get());
        oauthTextField.setText(st.getTwitchOAuth());
        usernameTextField.setText(st.getTwitchUser());

        sortCheckBox.setOnAction(event -> st.getSortTwitch().setValue(sortCheckBox.isSelected()));
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> st.setTwitchUser(newValue));
        oauthTextField.textProperty().addListener((observable, oldValue, newValue) -> st.setTwitchOAuth(newValue));
        styleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            st.setWindowStyle(newValue);
            String style = SettingsController.class.getResource("/styles/" + newValue + ".css").toExternalForm();
            MainWindow.getRootStage().getScene().getStylesheets().clear();
            SettingsWindow.getSettingsStage().getScene().getStylesheets().clear();
            MainWindow.getRootStage().getScene().getStylesheets().add(style);
            SettingsWindow.getSettingsStage().getScene().getStylesheets().add(style);
        });
    }

    private void setupStyleChoiceBox() {
        try {
            List<String> files = IOUtils.readLines(
                    SettingsController.class.getClassLoader().getResourceAsStream("styles/"), StandardCharsets.UTF_8);
            for (final String name : files) {
                styleChoiceBox.getItems().add(name.split("\\.")[0]);
            }
        } catch (IOException e) {
            LOGGER.error("ERROR while loading styles", e);
        }
        styleChoiceBox.getSelectionModel().select(Settings.instance().getWindowStyle());
    }

    @FXML
    protected void cancelSettingsAction() {
        SettingsWindow.getSettingsStage().hide();
        SettingsWindow.getSettingsStage().close();
    }

    @FXML
    protected void saveSettingsAction() {
        Settings.instance().saveSettings();
        SettingsWindow.getSettingsStage().hide();
        SettingsWindow.getSettingsStage().close();
    }
}
