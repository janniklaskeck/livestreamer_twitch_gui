package app.lsgui.gui.channellist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.ListCell;

public class ChannelCell extends ListCell<IChannel> {// NOSONAR

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelCell.class); // NOSONAR
    private static final PseudoClass ONLINEPSEUDOCLASS = PseudoClass.getPseudoClass("online");

    private BooleanProperty online;

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
    protected void updateItem(IChannel item, boolean isEmpty) {
        super.updateItem(item, isEmpty);
        if (isEmpty || item == null) {
            setText(null);
        } else {
            setText(item.getName().get());
            online.bind(item.isOnline());
        }
    }
}
