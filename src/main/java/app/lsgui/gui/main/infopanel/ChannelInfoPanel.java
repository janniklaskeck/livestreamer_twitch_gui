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
package app.lsgui.gui.main.infopanel;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.utils.LivestreamerUtils;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.TwitchUtils;
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
import javafx.stage.Stage;

public class ChannelInfoPanel extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelInfoPanel.class);
    private static final String CHANNELINFOPANELFXML = "fxml/ChannelInfoPanel.fxml";

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

    public ChannelInfoPanel(ComboBox<IService> serviceComboBox, ComboBox<String> qualityComboBox) {
        this.channelProperty = new SimpleObjectProperty<>();

        this.serviceComboBox = serviceComboBox;
        this.qualityComboBox = qualityComboBox;
        final FXMLLoader loader = new FXMLLoader();
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load(getClass().getClassLoader().getResourceAsStream(CHANNELINFOPANELFXML));
        } catch (IOException e) {
            LOGGER.error("ERROR while loading ChannelInfoPanel FXML", e);
        }
        this.setupChannelInfoPanel();
        this.setupChannelListener();

    }

    private void setupChannelListener() {
        this.channelProperty.addListener((observable, oldValue, newValue) -> {
            final IChannel selectedChannel = newValue;
            if (selectedChannel != null) {
                if (TwitchUtils.isTwitchChannel(selectedChannel)) {
                    this.bindToTwitchChannel((TwitchChannel) selectedChannel);
                } else {
                    this.bindToGenericChannel(selectedChannel);
                }
            }
        });
    }

    private void setupChannelInfoPanel() {
        this.previewImageView = new WrappedImageView(null);
        this.rootBorderPane.setCenter(this.previewImageView);

        this.channelDescription = new Label();
        this.channelDescription.setWrapText(true);
        this.channelUptime = new Label();
        this.channelViewers = new Label();
        this.channelGame = new Label();

        final int rowSpan = 1;
        final int columnSpan = 1;
        final int column = 0;
        final int gameRow = 0;
        final int viewersRow = 1;
        final int uptimeRow = 2;
        final int descriptionRow = 3;

        this.descriptionGrid.add(this.channelGame, column, gameRow, columnSpan, rowSpan);
        this.descriptionGrid.add(this.channelViewers, column, viewersRow, columnSpan, rowSpan);
        this.descriptionGrid.add(this.channelUptime, column, uptimeRow, columnSpan, rowSpan);
        this.descriptionGrid.add(this.channelDescription, column, descriptionRow, columnSpan, rowSpan);

        this.startStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLAY);
        this.startStreamButton.setOnAction(event -> this.startStream());
        this.startStreamButton.setDisable(true);
        this.recordStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD);
        this.recordStreamButton.setOnAction(event -> this.recordStream());
        this.recordStreamButton.setDisable(true);
        this.openChatButton = GlyphsDude.createIconButton(FontAwesomeIcon.COMMENT);
        this.openChatButton.setOnAction(event -> this.openChat());
        this.openChatButton.setDisable(true);
        this.openInBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.EDGE);
        this.openInBrowserButton.setOnAction(event -> this.openBrowser());
        this.openInBrowserButton.setDisable(true);

        this.buttonBox.getItems().add(this.startStreamButton);
        this.buttonBox.getItems().add(this.recordStreamButton);
        this.buttonBox.getItems().add(this.openChatButton);
        this.buttonBox.getItems().add(this.openInBrowserButton);
    }

    private void bindToTwitchChannel(final TwitchChannel selectedChannel) {
        this.previewImageView.imageProperty().bind((selectedChannel).getPreviewImageLarge());
        this.channelDescription.textProperty().bind((selectedChannel).getTitle());
        this.channelUptime.textProperty().bind((selectedChannel).getUptimeString());
        this.channelUptime.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CLOCK_ALT));
        this.channelViewers.textProperty().bind((selectedChannel).getViewersString());
        this.channelViewers.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        this.channelGame.textProperty().bind((selectedChannel).getGame());
        this.channelGame.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        this.openChatButton.setDisable(false);
        this.openInBrowserButton.setDisable(false);
        this.startStreamButton.disableProperty()
                .bind(selectedChannel.isOnline().not().or(selectedChannel.getIsPlaylist()));
        this.recordStreamButton.disableProperty()
                .bind(selectedChannel.isOnline().not().or(selectedChannel.getIsPlaylist()));
    }

    private void bindToGenericChannel(final IChannel channel) {
        this.channelDescription.textProperty().bind(channel.getName());
        this.previewImageView.imageProperty().unbind();
        this.channelUptime.textProperty().unbind();
        this.channelUptime.setGraphic(null);
        this.channelViewers.textProperty().unbind();
        this.channelViewers.setGraphic(null);
        this.channelGame.textProperty().unbind();
        this.channelGame.setGraphic(null);
        this.openChatButton.setDisable(true);
        this.startStreamButton.disableProperty().unbind();
        this.recordStreamButton.disableProperty().unbind();
        this.startStreamButton.setDisable(false);
        this.recordStreamButton.setDisable(false);
        this.openInBrowserButton.setDisable(false);
    }

    private void startStream() {
        if (TwitchUtils.isChannelOnline(this.channelProperty.get())) {
            final String url = this.buildUrl();
            final String quality = this.getQuality();
            LivestreamerUtils.startLivestreamer(url, quality);
        }
    }

    private void recordStream() {
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        LsGuiUtils.recordStream((Stage) getScene().getWindow(), service, this.channelProperty.get());
    }

    private void openChat() {
        TwitchUtils.openTwitchChat(this.channelProperty.get());
    }

    private void openBrowser() {
        if (this.channelProperty.get() != null) {
            LsGuiUtils.openURLInBrowser(this.buildUrl());
        }
    }

    public final ObjectProperty<IChannel> getChannelProperty() {
        return this.channelProperty;
    }

    public final void setChannelProperty(ObjectProperty<IChannel> channelProperty) {
        this.channelProperty = channelProperty;
    }

    private String getQuality() {
        return this.qualityComboBox.getSelectionModel().getSelectedItem();
    }

    private String buildUrl() {
        final String serviceUrl = this.serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get();
        final String channel = this.channelProperty.get().getName().get();
        return LsGuiUtils.buildUrl(serviceUrl, channel);
    }
}
