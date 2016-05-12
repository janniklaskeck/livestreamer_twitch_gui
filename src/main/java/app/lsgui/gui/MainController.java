package app.lsgui.gui;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.channelinfopanel.ChannelInfoPanel;
import app.lsgui.gui.channellist.ChannelList;
import app.lsgui.gui.settings.SettingsController;
import app.lsgui.gui.settings.SettingsWindow;
import app.lsgui.model.Channel;
import app.lsgui.model.Service;
import app.lsgui.service.Settings;
import app.lsgui.service.twitch.TwitchAPIClient;
import app.lsgui.service.twitch.TwitchChannelUpdateService;
import app.lsgui.utils.Utils;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static final String OFFLINEQUALITY = "Channel is offline";

    private ChannelList channelList;
    private ChannelInfoPanel channelInfoPanel;
    private ProgressIndicator updateProgressIndicator;

    @FXML
    private ComboBox<String> qualityComboBox;

    @FXML
    private ComboBox<Service> serviceComboBox;

    @FXML
    private BorderPane contentBorderPane;

    @FXML
    private ToolBar toolBarTop;

    @FXML
    public void initialize() {
        setupServiceComboBox();
        setupChannelList();
        setupQualityComboBox();
        setupChannelInfoPanel();
        setupToolbar();
    }

    private void setupQualityComboBox() {
        qualityComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(OFFLINEQUALITY)) {
                Settings.instance().setQuality(newValue);
            }
        });
    }

    private void setupServiceComboBox() {
        if (Settings.instance().getStreamServices().isEmpty()) {
            Settings.instance().getStreamServices().add(new Service("Twitch.tv", "http://twitch.tv/"));
        }
        serviceComboBox.getItems().addAll(Settings.instance().getStreamServices());
        serviceComboBox.setCellFactory(listView -> new ServiceCell());
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                if (service == null) {
                    return null;
                }
                return service.getName().get();
            }

            @Override
            public Service fromString(String string) {
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
                    Channel value = newValue == null ? oldValue : newValue;
                    qualityComboBox.setItems(FXCollections.observableArrayList(value.getAvailableQualities()));
                    if (qualityComboBox.getItems().size() > 1) {
                        final String quality = Settings.instance().getQuality();
                        if (qualityComboBox.getItems().contains(quality)) {
                            qualityComboBox.getSelectionModel().select(quality);
                        } else {
                            qualityComboBox.getSelectionModel().select("Best");
                        }
                    } else {
                        qualityComboBox.getSelectionModel().select(0);
                    }
                });
        channelList.getStreams().bind(serviceComboBox.getSelectionModel().getSelectedItem().getChannels());
        contentBorderPane.setLeft(channelList);
    }

    private void setupChannelInfoPanel() {
        channelInfoPanel = new ChannelInfoPanel(serviceComboBox, qualityComboBox);
        channelInfoPanel.getModelProperty().bind(channelList.getModelProperty());
        contentBorderPane.setCenter(channelInfoPanel);
    }

    private void setupToolbar() {
        Button addButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS);
        addButton.setOnAction(event -> addAction());
        Button removeButton = GlyphsDude.createIconButton(FontAwesomeIcon.MINUS);
        removeButton.setOnAction(event -> removeAction());
        Button importButton = GlyphsDude.createIconButton(FontAwesomeIcon.USERS);
        importButton.setOnAction(event -> importFollowedChannels());

        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, addButton);
        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, removeButton);
        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, importButton);

        toolBarTop.getItems().add(toolBarTop.getItems().size() - 1, new Separator());

        updateProgressIndicator = new ProgressIndicator();
        TwitchChannelUpdateService.getActiveChannelServicesProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.size() > 0) {
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
    }

    private void changeService(final Service newService) {
        LOGGER.debug("Change Service to {}", newService.getName().get());
        channelList.getStreams().bind(newService.getChannels());
    }

    private void openSettings() {
        SettingsWindow sw = new SettingsWindow(contentBorderPane.getScene().getWindow());
        sw.showAndWait();
    }

    private void addAction() {
        final Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Add Channel to current Service");
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        final String style = SettingsController.class
                .getResource("/styles/" + Settings.instance().getWindowStyle() + ".css").toExternalForm();
        Utils.addStyleSheetToStage(dialogStage, style);
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        final ButtonType bt = new ButtonType("Submit", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bt, ButtonType.CANCEL);

        final BorderPane ap = new BorderPane();
        final TextField tf = new TextField();
        ap.setCenter(tf);
        dialog.getDialogPane().setContent(ap);

        final Node submitButton = dialog.getDialogPane().lookupButton(bt);
        submitButton.setDisable(true);

        tf.textProperty()
                .addListener((observable, oldValue, newValue) -> submitButton.setDisable(newValue.trim().isEmpty()));

        dialog.setResultConverter(button -> {
            if ("".equals(tf.getText().trim()) || button.equals(ButtonType.CANCEL)) {
                return false;
            }
            if (TwitchAPIClient.instance().channelExists(tf.getText().trim())) {
                return true;
            }
            return false;
        });

        tf.requestFocus();

        final Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent() && result.get()) {
            addChannelToCurrentService(tf.getText().trim());
        }
    }

    private void removeAction() {
        final Channel toRemove = channelList.getListView().getSelectionModel().getSelectedItem();
        removeChannelFromCurrentService(toRemove);
    }

    private void importFollowedChannels() {
        final Dialog<Boolean> dialog = new Dialog<>();
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        final String style = SettingsController.class
                .getResource("/styles/" + Settings.instance().getWindowStyle() + ".css").toExternalForm();
        Utils.addStyleSheetToStage(dialogStage, style);
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        dialog.setTitle("Import Twitch.tv followed Channels");
        dialog.setContentText("Please enter your Twitch.tv Username:");
        final ButtonType bt = new ButtonType("Import", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bt, ButtonType.CANCEL);

        final BorderPane ap = new BorderPane();
        final TextField tf = new TextField();
        ap.setCenter(tf);
        dialog.getDialogPane().setContent(ap);

        final Node submitButton = dialog.getDialogPane().lookupButton(bt);
        submitButton.setDisable(true);

        tf.textProperty()
                .addListener((observable, oldValue, newValue) -> submitButton.setDisable(newValue.trim().isEmpty()));

        dialog.setResultConverter(button -> {
            if ("".equals(tf.getText().trim()) || button.equals(ButtonType.CANCEL)) {
                return false;
            }
            if (TwitchAPIClient.instance().channelExists(tf.getText().trim())) {
                return true;
            }
            return false;
        });

        tf.requestFocus();

        final Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent() && result.get()) {
            addFollowedChannelsToCurrentService(tf.getText().trim());
        }
    }

    private void addChannelToCurrentService(final String channel) {
        serviceComboBox.getSelectionModel().getSelectedItem().addChannel(channel);
    }

    private void addFollowedChannelsToCurrentService(final String channel) {
        serviceComboBox.getSelectionModel().getSelectedItem().addFollowedChannels(channel);
    }

    private void removeChannelFromCurrentService(final Channel channel) {
        serviceComboBox.getSelectionModel().getSelectedItem().removeSelectedChannel(channel);
    }
}
