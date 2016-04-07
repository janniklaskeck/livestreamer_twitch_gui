package app.lsgui.gui.streamlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.Channel;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class StreamCell extends ListCell<Channel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamCell.class);



    @Override
    protected void updateItem(Channel item, boolean isEmpty) {
        super.updateItem(item, isEmpty);
        if (isEmpty || item == null) {
            setText(null);
            LOGGER.debug("empty streamCell");
        } else {
            setText(item.getName().get());
            if (item.isOnline().get()) {
                setStyle("-fx-background-color: lightgreen; -fx-border-color: black");
                setTextFill(Color.GREEN);
            }
        }
    }
}
