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
package app.lsgui.gui;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.channelinfopanel.ChannelInfoPanel;
import app.lsgui.gui.channellist.ChannelList;
import app.lsgui.gui.settings.SettingsWindow;
import app.lsgui.gui.twitchbrowser.BrowserWindow;
import app.lsgui.model.channel.IChannel;
import app.lsgui.model.service.IService;
import app.lsgui.model.service.TwitchService;
import app.lsgui.rest.twitch.TwitchChannelUpdateService;
import app.lsgui.settings.Settings;
import app.lsgui.utils.LsGuiUtils;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class LsGUIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGUIController.class);

    private static final String OFFLINEQUALITY = "Channel is offline";

    private ChannelList channelList;
    private ChannelInfoPanel channelInfoPanel;
    private ProgressIndicator updateProgressIndicator;

    private Button importButton;
    private Button removeButton;
    private Button addButton;
    private Button twitchBrowserButton;

    private BooleanProperty hasPopOver;
    private PopOver popOver;

    @FXML
    private ComboBox<String> qualityComboBox;

    @FXML
    private ComboBox<IService> serviceComboBox;

    @FXML
    private BorderPane contentBorderPane;

    @FXML
    private ToolBar toolBarTop;

    @FXML
    public void initialize() { // NOSONAR
        setupServiceComboBox();
        setupChannelList();
        setupQualityComboBox();
        setupChannelInfoPanel();
        setupToolbar();
    }

    private void setupQualityComboBox() {
        qualityComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (!OFFLINEQUALITY.equals(newValue)) {
                Settings.getInstance().setQuality(newValue);
            }
        });
    }

    private void setupServiceComboBox() {
        if (Settings.getInstance().getStreamServices().isEmpty()) {
            Settings.getInstance().getStreamServices().add(new TwitchService("Twitch.tv", "http://twitch.tv/"));
        }
        serviceComboBox.itemsProperty().bind(Settings.getInstance().getStreamServices());
        serviceComboBox.setCellFactory(listView -> new ServiceCell());
        serviceComboBox.setConverter(new StringConverter<IService>() {
            @Override
            public String toString(IService service) {
                if (service == null) {
                    return null;
                }
                return service.getName().get();
            }

            @Override
            public IService fromString(String string) {
                return null;
            }
        });
        serviceComboBox.getSelectionModel().select(0);
        serviceComboBox.valueProperty().addListener((observable, oldValue, newValue) -> changeService(newValue));
    }

    private void setupChannelList() {
        channelList = new ChannelList();

        channelList.getListView().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        qualityComboBox.itemsProperty().bind(newValue.getAvailableQualities());
                        if (qualityComboBox.getItems().size() > 1) {
                            final String quality = Settings.getInstance().getQuality();
                            if (qualityComboBox.getItems().contains(quality)) {
                                qualityComboBox.getSelectionModel().select(quality);
                            } else {
                                qualityComboBox.getSelectionModel().select("Best");
                            }
                        } else {
                            qualityComboBox.getSelectionModel().select(0);
                        }
                    }
                });
        final IService service = serviceComboBox.getSelectionModel().getSelectedItem();
        channelList.getStreams().bind(service.getChannelProperty());
        channelList.getListView().setUserData(service);
        contentBorderPane.setLeft(channelList);
    }

    private void setupChannelInfoPanel() {
        channelInfoPanel = new ChannelInfoPanel(serviceComboBox, qualityComboBox);
        channelInfoPanel.getChannelProperty().bind(channelList.getSelectedChannelProperty());
        contentBorderPane.setCenter(channelInfoPanel);
    }

    private void setupToolbar() {
        addButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS);
        addButton.setOnAction(event -> addAction());
        removeButton = GlyphsDude.createIconButton(FontAwesomeIcon.MINUS);
        removeButton.setOnAction(event -> removeAction());
        importButton = GlyphsDude.createIconButton(FontAwesomeIcon.USERS);
        importButton.setOnAction(event -> importFollowedChannels());
        twitchBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.SEARCH);
        twitchBrowserButton.setOnAction(event -> openTwitchBrowser());

        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, addButton);
        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, removeButton);
        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, importButton);
        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, twitchBrowserButton);
        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, new Separator());

        updateProgressIndicator = new ProgressIndicator();
        updateProgressIndicator.setVisible(false);
        TwitchChannelUpdateService.getActiveChannelServicesProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                updateProgressIndicator.setVisible(true);
            } else {
                updateProgressIndicator.setVisible(false);
            }
        });

        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, updateProgressIndicator);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(Region.USE_PREF_SIZE);
        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, spacer);

        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, new Separator());

        Button settingsButton = GlyphsDude.createIconButton(FontAwesomeIcon.COG);
        settingsButton.setOnAction(event -> openSettings());
        toolBarTop.getItems().add(toolBarTop.getItems().size(), settingsButton);

        hasPopOver = new SimpleBooleanProperty(false);
    }

    private void changeService(final IService newService) {
        LOGGER.debug("Change Service to {}", newService.getName().get());
        channelList.getStreams().bind(newService.getChannelProperty());
        channelList.getListView().setUserData(newService);
        if (LsGuiUtils.isTwitchService(newService)) {
            importButton.setDisable(false);
            twitchBrowserButton.setDisable(false);
        } else {
            importButton.setDisable(true);
            twitchBrowserButton.setDisable(true);
        }
    }

    private void openSettings() {
        final SettingsWindow sw = new SettingsWindow(contentBorderPane.getScene().getWindow());
        sw.showAndWait();
    }

    private void addAction() {
        final IService service = serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null) {
            createAddDialog(service);
        }
    }

    private void createAddDialog(final IService service) {
        if (hasPopOver.get() && popOver != null) {
            popOver.hide();
        }
        popOver = new PopOver();
        hasPopOver.bind(popOver.showingProperty());
        popOver.getRoot().getStylesheets().add(
                getClass().getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        final Scene scene = addButton.getScene();

        final Window sceneWindow = scene.getWindow();
        final Point2D windowCoord = new Point2D(sceneWindow.getX(), sceneWindow.getY());
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final Point2D nodeCoord = addButton.localToScene(0.0, 25.0);
        final double clickX = Math.round(windowCoord.getX() + sceneCoord.getY() + nodeCoord.getX());
        final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());

        final Insets inset = new Insets(8);

        final VBox dialogBox = new VBox();
        dialogBox.setPadding(inset);

        final HBox buttonBox = new HBox();
        final Button submitButton = new Button("Submit");
        final Button cancelButton = new Button("Cancel");

        final HBox nameBox = new HBox();
        final Label nameLabel = new Label("Name ");
        final TextField nameTextField = new TextField();
        nameBox.getChildren().add(nameLabel);
        nameBox.getChildren().add(nameTextField);

        final HBox urlBox = new HBox();
        final Label urlLabel = new Label("URL ");
        final TextField urlTextField = new TextField();
        urlBox.getChildren().add(urlLabel);
        urlBox.getChildren().add(urlTextField);

        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(cancelButton);

        final Button addChannelButton = new Button("Add Channel");
        final Button addServiceButton = new Button("Add Service");
        addChannelButton.setOnAction(event -> {
            dialogBox.getChildren().clear();
            dialogBox.getChildren().add(nameBox);
            dialogBox.getChildren().add(buttonBox);
        });

        addServiceButton.setOnAction(event -> {
            dialogBox.getChildren().clear();
            dialogBox.getChildren().add(nameBox);
            dialogBox.getChildren().add(urlBox);
            dialogBox.getChildren().add(buttonBox);
        });

        submitButton.setOnAction(event -> {
            if (dialogBox.getChildren().contains(urlBox)) {
                final String serviceName = nameTextField.getText();
                final String serviceUrl = urlTextField.getText();
                LOGGER.info("Adding service");
                LsGuiUtils.addService(serviceName, serviceUrl);
            } else {
                final String channelName = nameTextField.getText();
                LOGGER.info("Adding channel");
                LsGuiUtils.addChannelToService(channelName, service);
            }
            popOver.hide();
        });

        cancelButton.setOnAction(event -> popOver.hide());
        dialogBox.getChildren().add(addChannelButton);
        dialogBox.getChildren().add(addServiceButton);

        popOver.setContentNode(dialogBox);
        popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
        popOver.setCornerRadius(4);
        popOver.setTitle("Add new Channel or Service");
        popOver.show(addButton.getParent(), clickX, clickY);
    }

    private void removeAction() {
        final IChannel channel = channelList.getListView().getSelectionModel().getSelectedItem();
        final IService service = serviceComboBox.getSelectionModel().getSelectedItem();
        if (channel != null && service != null) {
            LsGuiUtils.removeChannelFromService(channel, service);
        } else if (channel == null && service != null && serviceComboBox.getItems().size() > 1) {
            serviceComboBox.getSelectionModel().select(0);
            LsGuiUtils.removeService(service);
        }
    }

    private void importFollowedChannels() {
        final TwitchService service = (TwitchService) serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null) {
            createImportPopOver(service);
        }
    }

    private void createImportPopOver(final TwitchService service) {
        if (hasPopOver.get() && popOver != null) {
            popOver.hide();
        }
        popOver = new PopOver();
        hasPopOver.bind(popOver.showingProperty());
        popOver.getRoot().getStylesheets().add(
                getClass().getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        final Scene scene = importButton.getScene();

        final Window sceneWindow = scene.getWindow();
        final Point2D windowCoord = new Point2D(sceneWindow.getX(), sceneWindow.getY());
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final Point2D nodeCoord = importButton.localToScene(0.0, 25.0);
        final double clickX = Math.round(windowCoord.getX() + sceneCoord.getY() + nodeCoord.getX());
        final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());

        final Insets inset = new Insets(8);

        final VBox dialogBox = new VBox();
        dialogBox.setPadding(inset);

        final HBox buttonBox = new HBox();
        final Button submitButton = new Button("Import");
        final Button cancelButton = new Button("Cancel");
        final TextField nameTextField = new TextField();

        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(cancelButton);

        dialogBox.getChildren().add(nameTextField);
        dialogBox.getChildren().add(buttonBox);

        submitButton.setOnAction(event -> {
            final String username = nameTextField.getText();
            LsGuiUtils.addFollowedChannelsToService(username, service);
            popOver.hide();
        });

        cancelButton.setOnAction(event -> popOver.hide());

        popOver.setContentNode(dialogBox);
        popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
        popOver.setCornerRadius(4);
        popOver.setTitle("Import followed Twitch.tv Channels");
        popOver.show(importButton.getParent(), clickX, clickY);
    }

    private void openTwitchBrowser() {
        final BrowserWindow browser = new BrowserWindow(twitchBrowserButton.getScene().getWindow());
        browser.showAndWait();
    }
}
