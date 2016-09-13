package app.lsgui.gui.channellist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.service.IService;
import app.lsgui.model.twitch.channel.TwitchChannel;
import app.lsgui.settings.Settings;
import app.lsgui.utils.LivestreamerUtils;
import app.lsgui.utils.LsGuiUtils;
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
        isOnline = new BooleanPropertyBase() {
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
        isPlaylist = new BooleanPropertyBase() {
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
        hasReminder = new SimpleBooleanProperty();
    }

    @Override
    protected void updateItem(final IChannel channel, final boolean isEmpty) {
        super.updateItem(channel, isEmpty);
        if (isEmpty || channel == null) {
            textProperty().unbind();
            setText(null);
            setContextMenu(null);
        } else {
            setGraphic(createReminderCheckBox(channel));
            setContentDisplay(ContentDisplay.LEFT);
            setContextMenu(createContextMenu(channel));
            textProperty().bind(channel.getName());
            setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    startLivestreamerStream(channel);
                }
            });

            isOnline.bind(channel.isOnline());
            if (LsGuiUtils.isTwitchChannel(channel)) {
                final TwitchChannel twitchChannel = (TwitchChannel) channel;
                isPlaylist.bind(twitchChannel.getIsPlaylist());
            }
            hasReminder.bind(channel.hasReminder());
        }
    }

    private CheckBox createReminderCheckBox(final IChannel channel) {
        final CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().set(channel.hasReminder().get());
        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            channel.setReminder(newValue);
        });
        return checkBox;
    }

    private ContextMenu createContextMenu(final IChannel channel) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem delete = new MenuItem();
        delete.textProperty().set("Delete " + channel.getName().get());
        delete.setOnAction(event -> {
            final IService service = (IService) this.getListView().getUserData();
            LsGuiUtils.removeChannelFromService(channel, service);
            LOGGER.info("Deleting {}", channel.getName().get());
        });
        final MenuItem startStream = new MenuItem();
        startStream.textProperty().set("Start Stream ");
        startStream.setOnAction(event -> startLivestreamerStream(channel));
        startStream.disableProperty().bind(channel.isOnline().not());

        final MenuItem recordStream = new MenuItem();
        recordStream.textProperty().set("Record Stream");
        recordStream.setOnAction(event -> {
            final IService service = (IService) this.getListView().getUserData();
            LsGuiUtils.recordStream(service, channel);
        });
        recordStream.disableProperty().bind(channel.isOnline().not());

        final MenuItem openChat = new MenuItem();
        openChat.textProperty().set("Open Twitch.tv Chat");
        openChat.setOnAction(event -> {
            LsGuiUtils.openTwitchChat(channel);
            LOGGER.debug("Opening Twitch Chat for {}", channel.getName().get());
        });
        openChat.setDisable(!LsGuiUtils.isTwitchChannel(channel));
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
        contextMenu.getItems().add(openChat);
        contextMenu.getItems().add(openBrowser);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(delete);
        return contextMenu;
    }

    private void startLivestreamerStream(final IChannel channel) {
        final IService service = (IService) this.getListView().getUserData();
        final String url = LsGuiUtils.buildUrl(service.getUrl().get(), channel.getName().get());
        final String quality = Settings.instance().getQuality();
        LivestreamerUtils.startLivestreamer(url, quality);
        LOGGER.info("Starting Stream for {}", channel.getName().get());
    }
}
