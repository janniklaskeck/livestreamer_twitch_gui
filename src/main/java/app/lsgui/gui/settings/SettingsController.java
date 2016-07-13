package app.lsgui.gui.settings;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.LsGUIController;
import app.lsgui.gui.LsGUIWindow;
import app.lsgui.gui.chat.ChatWindow;
import app.lsgui.settings.Settings;
import app.lsgui.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGUIController.class);

    @FXML
    private CheckBox sortCheckBox;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField oauthTextField;

    @FXML
    private ChoiceBox<String> styleChoiceBox;

    @FXML
    private Button exeBrowseButton;

    @FXML
    public void initialize() {// NOSONAR
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
	    final String style = SettingsController.class.getResource("/styles/" + newValue + ".css").toExternalForm();

	    Utils.clearStyleSheetsFromStage(LsGUIWindow.getRootStage());
	    Utils.clearStyleSheetsFromStage(SettingsWindow.getSettingsStage());
	    Utils.clearStyleSheetsFromStage(ChatWindow.getChatStage());

	    Utils.addStyleSheetToStage(LsGUIWindow.getRootStage(), style);
	    Utils.addStyleSheetToStage(SettingsWindow.getSettingsStage(), style);
	    Utils.addStyleSheetToStage(ChatWindow.getChatStage(), style);
	});

	exeBrowseButton.setOnAction(event -> {
	    final FileChooser exeFileChooser = new FileChooser();
	    exeFileChooser.setTitle("Choose Livestreamer.exe file");
	    exeFileChooser.getExtensionFilters().add(new ExtensionFilter("EXE", "*.exe"));
	    final File exeFile = exeFileChooser.showOpenDialog(LsGUIWindow.getRootStage());
	    if (exeFile != null) {
		Settings.instance().setLivestreamerExePath(exeFile.getAbsolutePath());
	    }
	});
    }

    private void setupStyleChoiceBox() {
	styleChoiceBox.getItems().add("DarkStyle");
	styleChoiceBox.getItems().add("LightStyle");
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
