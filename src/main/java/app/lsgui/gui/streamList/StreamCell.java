package app.lsgui.gui.streamList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.StreamModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class StreamCell extends ListCell<StreamModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamCell.class);

    private BooleanProperty online = new SimpleBooleanProperty();

    public StreamCell() {
        //LOGGER.debug("StreamCell created");
    }

    @Override
    protected void updateItem(StreamModel item, boolean isEmpty) {
        super.updateItem(item, isEmpty);
        if (isEmpty || item == null) {
            setText(null);
        } else {
            online.bind(item.getOnline());
            setText(item.getName().get());
            if (online.get()) {
                setStyle("-fx-background-color: lightgreen; -fx-border-color: black");
                setTextFill(Color.GREEN);
            }
        }
    }
}
