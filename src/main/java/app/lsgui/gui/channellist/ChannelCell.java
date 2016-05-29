package app.lsgui.gui.channellist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.service.IService;
import app.lsgui.utils.LivestreamerUtils;
import app.lsgui.utils.Utils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;

public class ChannelCell extends ListCell<IChannel> {// NOSONAR

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelCell.class);
    private static final PseudoClass ONLINEPSEUDOCLASS = PseudoClass.getPseudoClass("online");

    private BooleanProperty online;
    private ContextMenu menu;

    public ChannelCell() {
        online = new BooleanPropertyBase() {
            @Override
            public void invalidated() {
                pseudoClassStateChanged(ONLINEPSEUDOCLASS, get());
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
    }

    @Override
    protected void updateItem(final IChannel item, final boolean isEmpty) {
        super.updateItem(item, isEmpty);

        if (isEmpty || item == null) {
            textProperty().unbind();
            setText(null);
            setContextMenu(null);
        } else {
            if (menu == null) {
                menu = createContextMenu();
            }
            setContextMenu(menu);
            textProperty().bind(item.getName());
            online.bind(item.isOnline());
        }
    }

    private ContextMenu createContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final IChannel channel = this.getItem();
        final MenuItem delete = new MenuItem();
        delete.textProperty().set("Delete " + channel.getName().get());
        delete.setOnAction(event -> {
            final IService service = (IService) this.getListView().getUserData();
            Utils.removeChannelFromService(channel, service);
            LOGGER.info("Deleting {}", channel.getName().get());
        });
        final MenuItem startStream = new MenuItem();
        startStream.textProperty().set("Start Stream ");
        startStream.setOnAction(event -> {
            final IService service = (IService) this.getListView().getUserData();
            final String url = Utils.buildUrl(service.getUrl().get(), channel.getName().get());
            LivestreamerUtils.startLivestreamer(url, "best");
            // TODO get right quality
            LOGGER.info("Starting Stream for {}", channel.getName().get());
        });
        contextMenu.getItems().add(delete);
        return contextMenu;
    }
}
