package app.lsgui.gui.streamList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.StreamModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class StreamCell extends ListCell<StreamModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamCell.class);

    public StreamCell() {

        itemProperty().addListener(new ChangeListener<StreamModel>() {
            @Override
            public void changed(ObservableValue<? extends StreamModel> observable, StreamModel oldValue,
                    StreamModel newValue) {
                LOGGER.debug("StreamCell change");
                if (newValue.getOnline().get()) {
                    setStyle("-fx-background-color: lightgreen; -fx-border-color: black");
                    setTextFill(Color.GREEN);
                }
            }
        });
    }

    @Override
    protected void updateItem(StreamModel item, boolean isEmpty) {
        super.updateItem(item, isEmpty);
        if (isEmpty || item == null) {
            setText(null);
        } else {
            setText(item.getName().get());
            if (item.getOnline().get()) {
                setStyle("-fx-background-color: lightgreen; -fx-border-color: black");
                setTextFill(Color.GREEN);
            }
        }
    }
}
