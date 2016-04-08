package app.lsgui.gui.channelinfopanel;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainWindow;
import app.lsgui.model.Channel;
import app.lsgui.model.Service;
import app.lsgui.model.twitch.TwitchChannel;
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

public class ChannelInfoPanel extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelInfoPanel.class);
    private static final String CHANNELINFOPANELFXML = "fxml/channelinfopanel.fxml";
    private static FXMLLoader loader;

    private ComboBox<Service> serviceComboBox;
    private ComboBox<String> qualityComboBox;

    private ObjectProperty<Channel> modelProperty;

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

        modelProperty.addListener((observable, oldValue, newValue) -> {
            Channel valueStreamModel = newValue == null ? oldValue : newValue;
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

        Button startStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLAY);
        startStreamButton.setOnAction(event -> startStream());

        Button recordStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD);
        recordStreamButton.setOnAction(event -> recordStream());

        Button openChatButton = GlyphsDude.createIconButton(FontAwesomeIcon.COMMENT);
        openChatButton.setOnAction(event -> openChat());

        Button openBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.EDGE);
        openBrowserButton.setOnAction(event -> openBrowser());

        buttonBox.getItems().add(startStreamButton);
        buttonBox.getItems().add(recordStreamButton);
        buttonBox.getItems().add(openChatButton);
        buttonBox.getItems().add(openBrowserButton);
    }

    public void setStream(Channel model) {
        modelProperty.setValue(model);
    }

    private void startStream() {
        if (modelProperty.get() != null) {
            String url = buildURL();
            String quality = buildQuality();
            Utils.startLivestreamer(url, quality);
        }
    }

    private void recordStream() {
        final String url = buildURL();
        final String quality = buildQuality();

        FileChooser recordFileChooser = new FileChooser();
        recordFileChooser.setTitle("Choose Target file");
        recordFileChooser.getExtensionFilters().add(new ExtensionFilter("MPEG4", ".mpeg4"));
        final File recordFile = recordFileChooser.showSaveDialog(MainWindow.getRootStage());
        Utils.recordLivestreamer(url, quality, recordFile);
    }

    private void openChat() {
        LOGGER.info("CHAT NOT IMPLEMENTED");
    }

    private void openBrowser() {
        Utils.openURLInBrowser(serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get()
                + modelProperty.get().getName().get());
    }

    /**
     * @return the modelProperty
     */
    public ObjectProperty<Channel> getModelProperty() {
        return modelProperty;
    }

    /**
     * @param modelProperty
     *            the modelProperty to set
     */
    public void setModelProperty(ObjectProperty<Channel> modelProperty) {
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
