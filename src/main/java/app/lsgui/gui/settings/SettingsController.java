/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.gui.settings;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.LsGUIWindow;
import app.lsgui.gui.chat.ChatWindow;
import app.lsgui.settings.Settings;
import app.lsgui.utils.LsGuiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    @FXML
    private CheckBox sortCheckBox;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField oauthTextField;

    @FXML
    private ChoiceBox<String> styleChoiceBox;

    @FXML
    private ChoiceBox<Integer> gamesToLoadChoiceBox;

    @FXML
    private ChoiceBox<Integer> channelsToLoadChoiceBox;

    @FXML
    private Button exeBrowseButton;

    @FXML
    private Hyperlink updateLink;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");
        setupStyleChoiceBox();
        setupLoadChoiceBoxes();
        final Settings settings = Settings.getInstance();

        sortCheckBox.setSelected(settings.getSortTwitch().get());
        oauthTextField.setText(settings.getTwitchOAuth());
        usernameTextField.setText(settings.getTwitchUser());
        gamesToLoadChoiceBox.getSelectionModel().select(Integer.valueOf(settings.getMaxGamesLoad()));
        channelsToLoadChoiceBox.getSelectionModel().select(Integer.valueOf(settings.getMaxChannelsLoad()));

        sortCheckBox.setOnAction(event -> settings.getSortTwitch().setValue(sortCheckBox.isSelected()));
        usernameTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> settings.setTwitchUser(newValue));
        oauthTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> settings.setTwitchOAuth(newValue));
        styleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            settings.setWindowStyle(newValue);
            final String style = SettingsController.class.getResource("/styles/" + newValue + ".css").toExternalForm();

            LsGuiUtils.clearStyleSheetsFromStage(LsGUIWindow.getRootStage());
            LsGuiUtils.clearStyleSheetsFromStage(SettingsWindow.getSettingsStage());
            LsGuiUtils.clearStyleSheetsFromStage(ChatWindow.getChatStage());

            LsGuiUtils.addStyleSheetToStage(LsGUIWindow.getRootStage(), style);
            LsGuiUtils.addStyleSheetToStage(SettingsWindow.getSettingsStage(), style);
            LsGuiUtils.addStyleSheetToStage(ChatWindow.getChatStage(), style);
        });

        gamesToLoadChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> settings.setMaxGamesLoad(newValue));

        channelsToLoadChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> settings.setMaxChannelsLoad(newValue));

        exeBrowseButton.setOnAction(event -> {
            final FileChooser exeFileChooser = new FileChooser();
            exeFileChooser.setTitle("Choose Livestreamer.exe file");
            exeFileChooser.getExtensionFilters().add(new ExtensionFilter("EXE", "*.exe"));
            final File exeFile = exeFileChooser.showOpenDialog(LsGUIWindow.getRootStage());
            if (exeFile != null) {
                Settings.getInstance().setLivestreamerExePath(exeFile.getAbsolutePath());
            }
        });
        final String updateLinkString = settings.getUpdateLink().get();
        if (updateLinkString != null && !"".equals(updateLinkString)) {
            updateLink.setText("New Version available!");
            updateLink.setOnAction(event -> LsGuiUtils.openURLInBrowser(settings.getUpdateLink().get()));
        } else {
            updateLink.setDisable(true);
        }

    }

    private void setupLoadChoiceBoxes() {
        for (int i = 10; i < 100; i += 10) {
            gamesToLoadChoiceBox.getItems().add(i);
            channelsToLoadChoiceBox.getItems().add(i);
        }
        final Settings settings = Settings.getInstance();
        final int maxGamesToLoad = settings.getMaxGamesLoad();
        final int maxChannelsToLoad = settings.getMaxChannelsLoad();
        gamesToLoadChoiceBox.getSelectionModel().select(maxGamesToLoad);
        channelsToLoadChoiceBox.getSelectionModel().select(maxChannelsToLoad);
    }

    private void setupStyleChoiceBox() {
        styleChoiceBox.getItems().add("DarkStyle");
        styleChoiceBox.getItems().add("LightStyle");
        styleChoiceBox.getSelectionModel().select(Settings.getInstance().getWindowStyle());
    }

    @FXML
    protected void cancelSettingsAction() {
        SettingsWindow.getSettingsStage().hide();
        SettingsWindow.getSettingsStage().close();
    }

    @FXML
    protected void saveSettingsAction() {
        Settings.getInstance().saveSettings();
        SettingsWindow.getSettingsStage().hide();
        SettingsWindow.getSettingsStage().close();
    }
}
