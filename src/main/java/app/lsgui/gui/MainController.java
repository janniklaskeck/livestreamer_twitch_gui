package app.lsgui.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class MainController {

	private static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

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
	public void initialize() {
		LOGGER.debug("INIT MainController");
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
