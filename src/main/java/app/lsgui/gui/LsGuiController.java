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
public final class LsGuiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGuiController.class);
    private static final String OFFLINEQUALITY = "Channel is offline";
    private static final int CORDER_RADIUS = 4;

    private ChannelList channelList;
    private Button importButton;
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

    public LsGuiController() {
        // Empty Constructor
    }

    @FXML
    public void initialize() {
        this.setupServiceComboBox();
        this.setupChannelList();
        this.setupQualityComboBox();
        this.setupChannelInfoPanel();
        this.setupToolbar();
    }

    private void setupQualityComboBox() {
        this.qualityComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (!OFFLINEQUALITY.equals(newValue)) {
                Settings.getInstance().setQuality(newValue);
            }
        });
    }

    private void setupServiceComboBox() {
        if (Settings.getInstance().getStreamServices().isEmpty()) {
            Settings.getInstance().getStreamServices().add(new TwitchService("Twitch.tv", "http://twitch.tv/"));
        }
        this.serviceComboBox.itemsProperty().bind(Settings.getInstance().getStreamServices());
        this.serviceComboBox.setCellFactory(listView -> new ServiceCell());
        this.serviceComboBox.setConverter(new StringConverter<IService>() {
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
        this.serviceComboBox.getSelectionModel().select(0);
        this.serviceComboBox.valueProperty()
                .addListener((observable, oldValue, newValue) -> this.changeService(newValue));
    }

    private void setupChannelList() {
        this.channelList = new ChannelList();
        this.channelList.getListView().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        this.qualityComboBox.itemsProperty().bind(newValue.getAvailableQualities());
                        if (this.qualityComboBox.getItems().size() > 1) {
                            final String quality = Settings.getInstance().getQuality();
                            if (this.qualityComboBox.getItems().contains(quality)) {
                                this.qualityComboBox.getSelectionModel().select(quality);
                            } else {
                                this.qualityComboBox.getSelectionModel().select("Best");
                            }
                        } else {
                            this.qualityComboBox.getSelectionModel().select(0);
                        }
                    }
                });
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        this.channelList.getStreams().bind(service.getChannelProperty());
        this.channelList.getListView().setUserData(service);
        this.contentBorderPane.setLeft(this.channelList);
    }

    private void setupChannelInfoPanel() {
        final ChannelInfoPanel channelInfoPanel = new ChannelInfoPanel(this.serviceComboBox, this.qualityComboBox);
        channelInfoPanel.getChannelProperty().bind(this.channelList.getSelectedChannelProperty());
        this.contentBorderPane.setCenter(channelInfoPanel);
    }

    private void setupToolbar() {
        this.addButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS);
        this.addButton.setOnAction(event -> this.addAction());
        final Button removeButton = GlyphsDude.createIconButton(FontAwesomeIcon.MINUS);
        removeButton.setOnAction(event -> this.removeAction());
        this.importButton = GlyphsDude.createIconButton(FontAwesomeIcon.USERS);
        this.importButton.setOnAction(event -> this.importFollowedChannels());
        this.twitchBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.SEARCH);
        this.twitchBrowserButton.setOnAction(event -> this.openTwitchBrowser());

        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, this.addButton);
        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, removeButton);
        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, this.importButton);
        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, this.twitchBrowserButton);
        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, new Separator());

        final ProgressIndicator updateProgressIndicator = new ProgressIndicator();
        updateProgressIndicator.setVisible(false);
        TwitchChannelUpdateService.getActiveChannelServicesProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                updateProgressIndicator.setVisible(true);
            } else {
                updateProgressIndicator.setVisible(false);
            }
        });

        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, updateProgressIndicator);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(Region.USE_PREF_SIZE);
        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, spacer);
        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size() - 1, new Separator());
        Button settingsButton = GlyphsDude.createIconButton(FontAwesomeIcon.COG);
        settingsButton.setOnAction(event -> this.openSettings());
        this.toolBarTop.getItems().add(this.toolBarTop.getItems().size(), settingsButton);
        this.hasPopOver = new SimpleBooleanProperty(false);
    }

    private void changeService(final IService newService) {
        LOGGER.debug("Change Service to {}", newService.getName().get());
        this.channelList.getStreams().bind(newService.getChannelProperty());
        this.channelList.getListView().setUserData(newService);
        if (LsGuiUtils.isTwitchService(newService)) {
            this.importButton.setDisable(false);
            this.twitchBrowserButton.setDisable(false);
        } else {
            this.importButton.setDisable(true);
            this.twitchBrowserButton.setDisable(true);
        }
    }

    private void openSettings() {
        final SettingsWindow sw = new SettingsWindow(this.contentBorderPane.getScene().getWindow());
        sw.showAndWait();
    }

    private void addAction() {
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null) {
            this.createAddDialog(service);
        }
    }

    private void createAddDialog(final IService service) {
        if (this.hasPopOver.get() && this.popOver != null) {
            this.popOver.hide();
        }
        this.popOver = new PopOver();
        this.hasPopOver.bind(this.popOver.showingProperty());
        this.popOver.getRoot().getStylesheets().add(
                getClass().getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        final Scene scene = this.addButton.getScene();

        final Window sceneWindow = scene.getWindow();
        final Point2D windowCoord = new Point2D(sceneWindow.getX(), sceneWindow.getY());
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final Point2D nodeCoord = this.addButton.localToScene(0.0, 25.0);
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
            this.popOver.hide();
        });
        submitButton.setDefaultButton(true);

        cancelButton.setOnAction(event -> this.popOver.hide());
        dialogBox.getChildren().add(addChannelButton);
        dialogBox.getChildren().add(addServiceButton);

        this.popOver.setContentNode(dialogBox);
        this.popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
        this.popOver.setCornerRadius(CORDER_RADIUS);
        this.popOver.setTitle("Add new Channel or Service");
        this.popOver.show(this.addButton.getParent(), clickX, clickY);
    }

    private void removeAction() {
        final IChannel channel = this.channelList.getListView().getSelectionModel().getSelectedItem();
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (channel != null && service != null) {
            LsGuiUtils.removeChannelFromService(channel, service);
        } else if (channel == null && service != null && this.serviceComboBox.getItems().size() > 1) {
            this.serviceComboBox.getSelectionModel().select(0);
            LsGuiUtils.removeService(service);
        }
    }

    private void importFollowedChannels() {
        final TwitchService service = (TwitchService) this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null) {
            this.createImportPopOver(service);
        }
    }

    private void createImportPopOver(final TwitchService service) {
        if (this.hasPopOver.get() && this.popOver != null) {
            this.popOver.hide();
        }
        this.popOver = new PopOver();
        this.hasPopOver.bind(this.popOver.showingProperty());
        this.popOver.getRoot().getStylesheets().add(
                getClass().getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        final Scene scene = this.importButton.getScene();

        final Window sceneWindow = scene.getWindow();
        final Point2D windowCoord = new Point2D(sceneWindow.getX(), sceneWindow.getY());
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final Point2D nodeCoord = this.importButton.localToScene(0.0, 25.0);
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
            this.popOver.hide();
        });
        submitButton.setDefaultButton(true);

        cancelButton.setOnAction(event -> this.popOver.hide());

        this.popOver.setContentNode(dialogBox);
        this.popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
        this.popOver.setCornerRadius(CORDER_RADIUS);
        this.popOver.setTitle("Import followed Twitch.tv Channels");
        this.popOver.show(this.importButton.getParent(), clickX, clickY);
    }

    private void openTwitchBrowser() {
        final BrowserWindow browser = new BrowserWindow(this.twitchBrowserButton.getScene().getWindow());
        browser.showAndWait();
    }
}
