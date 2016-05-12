package app.lsgui.gui.channelinfopanel;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainWindow;
import app.lsgui.gui.chat.ChatWindow;
import app.lsgui.model.IChannel;
import app.lsgui.model.Service;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.utils.LivestreamerUtils;
import app.lsgui.utils.Utils;
import app.lsgui.utils.WrappedImageView;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ChannelInfoPanel extends BorderPane { // NOSONAR

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelInfoPanel.class);
    private static final String CHANNELINFOPANELFXML = "fxml/ChannelInfoPanel.fxml";
    private static FXMLLoader loader;

    private ComboBox<Service> serviceComboBox;
    private ComboBox<String> qualityComboBox;

    private ObjectProperty<IChannel> modelProperty;

    private WrappedImageView previewImageView;

    private Label channelDescription;
    private Label channelUptime;
    private Label channelViewers;
    private Label channelGame;

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private CheckBox notifyCheckBox;

    @FXML
    private GridPane descriptionGrid;

    @FXML
    private ToolBar buttonBox;

    public ChannelInfoPanel(ComboBox<Service> serviceComboBox, ComboBox<String> qualityComboBox) {
        LOGGER.debug("Construct StreamInfoPanel");
        modelProperty = new SimpleObjectProperty<>();

        this.serviceComboBox = serviceComboBox;
        this.qualityComboBox = qualityComboBox;
        loader = new FXMLLoader();
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load(getClass().getClassLoader().getResourceAsStream(CHANNELINFOPANELFXML));
        } catch (IOException e) {
            LOGGER.error("ERROR while loading ChannelInfoPanel FXML", e);
        }
        setupChannelInfoPanel();
        setupModelListener();

    }

    private void setupModelListener() {
        modelProperty.addListener((observable, oldValue, newValue) -> {
            IChannel valueStreamModel = newValue == null ? oldValue : newValue;
            if (valueStreamModel.getClass().equals(TwitchChannel.class)) {
                previewImageView.imageProperty().bind(((TwitchChannel) valueStreamModel).getPreviewImage());
                channelDescription.textProperty().bind(((TwitchChannel) valueStreamModel).getDescription());

                channelUptime.textProperty().bind(((TwitchChannel) valueStreamModel).getUptimeString());
                channelUptime.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CLOCK_ALT));

                channelViewers.textProperty().bind(((TwitchChannel) valueStreamModel).getViewersString());
                channelViewers.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));

                channelGame.textProperty().bind(((TwitchChannel) valueStreamModel).getGame());
                channelGame.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
            } else {
                channelDescription.textProperty().bind(modelProperty.get().getName());
            }
        });
    }

    private void setupChannelInfoPanel() {
        previewImageView = new WrappedImageView(null);
        rootBorderPane.setCenter(previewImageView);

        channelDescription = new Label();
        channelDescription.setWrapText(true);
        channelUptime = new Label();
        channelViewers = new Label();
        channelGame = new Label();

        descriptionGrid.add(channelGame, 0, 0, 1, 1);
        descriptionGrid.add(channelViewers, 0, 1, 1, 1);
        descriptionGrid.add(channelUptime, 0, 2, 1, 1);
        descriptionGrid.add(channelDescription, 0, 3, 1, 1);

        final Button startStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLAY);
        startStreamButton.setOnAction(event -> startStream());

        final Button recordStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD);
        recordStreamButton.setOnAction(event -> recordStream());

        final Button openChatButton = GlyphsDude.createIconButton(FontAwesomeIcon.COMMENT);
        openChatButton.setOnAction(event -> openChat());

        final Button openBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.EDGE);
        openBrowserButton.setOnAction(event -> openBrowser());

        buttonBox.getItems().add(startStreamButton);
        buttonBox.getItems().add(recordStreamButton);
        buttonBox.getItems().add(openChatButton);
        buttonBox.getItems().add(openBrowserButton);
    }

    public void setStream(final IChannel model) {
        modelProperty.setValue(model);
    }

    private void startStream() {
        if (modelProperty.get() != null && modelProperty.get().isOnline().get()) {
            final String url = buildURL();
            final String quality = buildQuality();
            LivestreamerUtils.startLivestreamer(url, quality);
        }
    }

    private void recordStream() {
        if (modelProperty.get() != null && !"".equals(modelProperty.get().getName().get())
                && modelProperty.get().isOnline().get()) {
            final String url = buildURL();
            final String quality = buildQuality();

            final FileChooser recordFileChooser = new FileChooser();
            recordFileChooser.setTitle("Choose Target file");
            recordFileChooser.getExtensionFilters().add(new ExtensionFilter("MPEG4", ".mpeg4"));
            final File recordFile = recordFileChooser.showSaveDialog(MainWindow.getRootStage());
            if (recordFile != null) {
                LivestreamerUtils.recordLivestreamer(url, quality, recordFile);
            }
        }
    }

    private void openChat() {
        if (modelProperty.get() != null && !"".equals(modelProperty.get().getName().get())) {
            final String channel = modelProperty.get().getName().get();
            ChatWindow cw = new ChatWindow(channel);
            cw.connect();
        }
    }

    private void openBrowser() {
        if (modelProperty.get() != null && !"".equals(modelProperty.get().getName().get())) {
            final String channel = modelProperty.get().getName().get();
            Utils.openURLInBrowser(serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get() + channel);
        }
    }

    public ObjectProperty<IChannel> getModelProperty() {
        return modelProperty;
    }

    public void setModelProperty(ObjectProperty<IChannel> modelProperty) {
        this.modelProperty = modelProperty;
    }

    private String buildURL() {
        return serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get()
                + modelProperty.get().getName().get();
    }

    private String buildQuality() {
        return qualityComboBox.getSelectionModel().getSelectedItem();
    }
}
