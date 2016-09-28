package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridView;

import app.lsgui.model.twitch.ITwitchItem;
import javafx.scene.control.Tab;

public class BrowserTab extends Tab {

    private String name;

    public BrowserTab(final String name) {
        this.name = name;
        setText(this.name);
        setContent(buildGridView());
    }

    public String getName() {
        return this.name;
    }

    public GridView<ITwitchItem> getGridView() {
        return (GridView<ITwitchItem>) getContent();
    }

    private static GridView<ITwitchItem> buildGridView() {
        final GridView<ITwitchItem> gridView = new GridView<>();
        gridView.setCellFactory(param -> new TwitchItemPane());
        gridView.setCellWidth(TwitchItemPane.WIDTH);
        gridView.cellHeightProperty().bind(TwitchItemPane.HEIGHT_PROPERTY);
        return gridView;
    }

}
