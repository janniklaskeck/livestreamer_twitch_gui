package app.lsgui.gui.streamList;

import app.lsgui.model.twitch.TwitchStreamModel;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class StreamCell extends ListCell<TwitchStreamModel> {

    @Override
    protected void updateItem(TwitchStreamModel item, boolean isEmpty) {
        super.updateItem(item, isEmpty);

        if (isEmpty || item == null) {
            setText(null);
        } else {
            setText(item.getName().get());
            if (item.getOnline().get()) {
                setStyle("-fx-background-color: lightgreen; -fx-border-color: black");
                setTextFill(Color.BLACK);
            }
        }

    }
}
