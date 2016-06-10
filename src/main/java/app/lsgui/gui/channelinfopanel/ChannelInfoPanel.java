package app.lsgui.gui.channelinfopanel;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.channel.twitch.TwitchChannel;
import app.lsgui.model.service.IService;
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

public class ChannelInfoPanel extends BorderPane { // NOSONAR

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelInfoPanel.class);
    private static final String CHANNELINFOPANELFXML = "fxml/ChannelInfoPanel.fxml";
    private FXMLLoader loader;

    private ComboBox<IService> serviceComboBox;
    private ComboBox<String> qualityComboBox;

    private ObjectProperty<IChannel> channelProperty;

    private WrappedImageView previewImageView;

    private Label channelDescription;
    private Label channelUptime;
    private Label channelViewers;
    private Label channelGame;

    private Button openChatButton;
    private Button startStreamButton;
    private Button recordStreamButton;
    private Button openInBrowserButton;

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private CheckBox notifyCheckBox;

    @FXML
    private GridPane descriptionGrid;

    @FXML
    private ToolBar buttonBox;

    /**
     *
     * @param serviceComboBox
     * @param qualityComboBox
     */
    public ChannelInfoPanel(ComboBox<IService> serviceComboBox, ComboBox<String> qualityComboBox) {
	channelProperty = new SimpleObjectProperty<>();

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
	setupChannelListener();

    }

    private void setupChannelListener() {
	channelProperty.addListener((observable, oldValue, newValue) -> {
	    final IChannel selectedChannel = newValue;
	    if (selectedChannel != null) {
		if (Utils.isTwitchChannel(selectedChannel)) {
		    bindToTwitchChannel(selectedChannel);
		} else {
		    bindToGenericChannel(selectedChannel);
		}
	    }
	});
    }

    private void bindToTwitchChannel(final IChannel selectedChannel) {
	final TwitchChannel twitchChannel = (TwitchChannel) selectedChannel;
	previewImageView.imageProperty().bind((twitchChannel).getPreviewImage());
	channelDescription.textProperty().bind((twitchChannel).getDescription());
	channelUptime.textProperty().bind((twitchChannel).getUptimeString());
	channelUptime.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CLOCK_ALT));
	channelViewers.textProperty().bind((twitchChannel).getViewersString());
	channelViewers.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
	channelGame.textProperty().bind((twitchChannel).getGame());
	channelGame.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
	openChatButton.setDisable(false);
	startStreamButton.disableProperty().bind(twitchChannel.isOnline().not().or(twitchChannel.getIsPlaylist()));
	recordStreamButton.disableProperty().bind(twitchChannel.isOnline().not().or(twitchChannel.getIsPlaylist()));
    }

    private void bindToGenericChannel(final IChannel channel) {
	channelDescription.textProperty().bind(channel.getName());
	previewImageView.imageProperty().unbind();
	channelUptime.textProperty().unbind();
	channelUptime.setGraphic(null);
	channelViewers.textProperty().unbind();
	channelViewers.setGraphic(null);
	channelGame.textProperty().unbind();
	channelGame.setGraphic(null);
	openChatButton.setDisable(true);
	startStreamButton.disableProperty().unbind();
	recordStreamButton.disableProperty().unbind();
	startStreamButton.setDisable(false);
	recordStreamButton.setDisable(false);
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

	startStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLAY);
	startStreamButton.setOnAction(event -> startStream());

	recordStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD);
	recordStreamButton.setOnAction(event -> recordStream());

	openChatButton = GlyphsDude.createIconButton(FontAwesomeIcon.COMMENT);
	openChatButton.setOnAction(event -> openChat());

	openInBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.EDGE);
	openInBrowserButton.setOnAction(event -> openBrowser());

	buttonBox.getItems().add(startStreamButton);
	buttonBox.getItems().add(recordStreamButton);
	buttonBox.getItems().add(openChatButton);
	buttonBox.getItems().add(openInBrowserButton);
    }

    public void setStream(final IChannel channel) {
	channelProperty.setValue(channel);
    }

    private void startStream() {
	if (Utils.isChannelOnline(channelProperty.get())) {
	    final String url = buildUrl();
	    final String quality = getQuality();
	    LivestreamerUtils.startLivestreamer(url, quality);
	}
    }

    private void recordStream() {
	final IService service = serviceComboBox.getSelectionModel().getSelectedItem();
	Utils.recordStream(service, channelProperty.get());
    }

    private void openChat() {
	Utils.openTwitchChat(channelProperty.get());
    }

    private void openBrowser() {
	if (channelProperty.get() != null) {
	    Utils.openURLInBrowser(buildUrl());
	}
    }

    public ObjectProperty<IChannel> getChannelProperty() {
	return channelProperty;
    }

    public void setChannelProperty(ObjectProperty<IChannel> channelProperty) {
	this.channelProperty = channelProperty;
    }

    private String getQuality() {
	return qualityComboBox.getSelectionModel().getSelectedItem();
    }

    private String buildUrl() {
	final String serviceUrl = serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get();
	final String channel = channelProperty.get().getName().get();
	return Utils.buildUrl(serviceUrl, channel);
    }
}
