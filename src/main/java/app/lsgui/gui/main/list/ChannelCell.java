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
package app.lsgui.gui.main.list;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.utils.LivestreamerUtils;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.Settings;
import app.lsgui.utils.TwitchUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class ChannelCell extends ListCell<IChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelCell.class);
    private static final PseudoClass ONLINE_PSEUDOCLASS = PseudoClass.getPseudoClass("online");
    private static final PseudoClass PLAYLIST_PSEUDOCLASS = PseudoClass.getPseudoClass("isPlaylist");

    private BooleanProperty isOnline;
    private BooleanProperty isPlaylist;
    private BooleanProperty hasReminder;

    /**
     * Channelist ChannelCell
     */
    public ChannelCell() {
        this.isOnline = new BooleanPropertyBase() {
            @Override
            public void invalidated() {
                pseudoClassStateChanged(ONLINE_PSEUDOCLASS, get());
            }

            @Override
            public String getName() {
                return "online";
            }

            @Override
            public Object getBean() {
                return ChannelCell.this;
            }
        };
        getStyleClass().add("channel-cell");
        this.isPlaylist = new BooleanPropertyBase() {
            @Override
            public void invalidated() {
                pseudoClassStateChanged(PLAYLIST_PSEUDOCLASS, get());
            }

            @Override
            public String getName() {
                return "isPlaylist";
            }

            @Override
            public Object getBean() {
                return ChannelCell.this;
            }
        };
        getStyleClass().add("channel-cell");
        this.hasReminder = new SimpleBooleanProperty();
    }

    @Override
    protected final void updateItem(final IChannel channel, final boolean isEmpty) {
        super.updateItem(channel, isEmpty);
        if (isEmpty || channel == null) {
            textProperty().unbind();
            setText(null);
            setContextMenu(null);
        } else {
            setGraphic(createReminderCheckBox(channel));
            setContentDisplay(ContentDisplay.LEFT);
            setContextMenu(this.createContextMenu(channel));
            textProperty().bind(channel.getDisplayName());
            setOnMouseClicked(mouseEvent -> {
                final int doubleClickAmount = 2;
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == doubleClickAmount) {
                    this.startLivestreamerStream(channel);
                }
            });

            this.isOnline.bind(channel.isOnline());
            if (TwitchUtils.isTwitchChannel(channel)) {
                final TwitchChannel twitchChannel = (TwitchChannel) channel;
                this.isPlaylist.bind(twitchChannel.getIsPlaylist());
            }
            this.hasReminder.bind(channel.hasReminder());
        }
    }

    private static CheckBox createReminderCheckBox(final IChannel channel) {
        final CheckBox checkBox = new CheckBox();
        final BooleanProperty selectedProperty = checkBox.selectedProperty();
        selectedProperty.set(channel.hasReminder().get());
        selectedProperty.addListener((obs, oldValue, newValue) -> channel.setReminder(newValue));
        return checkBox;
    }

    private ContextMenu createContextMenu(final IChannel channel) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem delete = new MenuItem();
        delete.textProperty().set("Delete " + channel.getDisplayName().get());
        delete.setOnAction(event -> {
            final IService service = (IService) this.getListView().getUserData();
            LsGuiUtils.removeChannelFromService(channel, service);
            LOGGER.info("Deleting {}", channel.getName().get());
        });
        final MenuItem startStream = new MenuItem();
        startStream.textProperty().set("Start Stream ");
        startStream.setOnAction(event -> this.startLivestreamerStream(channel));
        startStream.disableProperty().bind(channel.isOnline().not());

        final MenuItem recordStream = new MenuItem();
        recordStream.textProperty().set("Record Stream");
        recordStream.setOnAction(event -> {
            final IService service = (IService) this.getListView().getUserData();
            LsGuiUtils.recordStream((Stage) getScene().getWindow(), service, channel);
        });
        recordStream.disableProperty().bind(channel.isOnline().not());

        final MenuItem openBrowser = new MenuItem();
        openBrowser.textProperty().set("Open in Browser");
        openBrowser.setOnAction(event -> {
            final IService service = (IService) this.getListView().getUserData();
            final String url = LsGuiUtils.buildUrl(service.getUrl().get(), channel.getName().get());
            LsGuiUtils.openURLInBrowser(url);
        });
        contextMenu.getItems().add(startStream);
        contextMenu.getItems().add(recordStream);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(openBrowser);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(delete);
        return contextMenu;
    }

    private void startLivestreamerStream(final IChannel channel) {
        final IService service = (IService) this.getListView().getUserData();
        final String url = LsGuiUtils.buildUrl(service.getUrl().get(), channel.getName().get());
        final String quality = Settings.getInstance().qualityProperty().get();
        LivestreamerUtils.startLivestreamer(url, quality);
        LOGGER.info("Starting Stream for {}", channel.getName().get());
    }
}
