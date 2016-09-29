package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridView;

import app.lsgui.model.twitch.ITwitchItem;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;

public final class BrowserTab extends Tab {

    private ListProperty<ITwitchItem> items = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<ITwitchItem> activeItems = new SimpleListProperty<>(FXCollections.observableArrayList());

    public BrowserTab(final String name) {
        super(name);
        setContent(this.buildGridView());
    }

    public GridView<ITwitchItem> getGridView() {
        return (GridView<ITwitchItem>) getContent();
    }

    private GridView<ITwitchItem> buildGridView() {
        final GridView<ITwitchItem> gridView = new GridView<>();
        gridView.setCellFactory(param -> new TwitchItemPane());
        gridView.setCellWidth(TwitchItemPane.WIDTH);
        gridView.cellHeightProperty().bind(TwitchItemPane.HEIGHT_PROPERTY);
        gridView.itemsProperty().bind(this.activeItemsProperty());
        return gridView;
    }

    public ListProperty<ITwitchItem> itemsProperty() {
        return this.items;
    }

    public ListProperty<ITwitchItem> activeItemsProperty() {
        return this.activeItems;
    }

    public void resetActiveItems() {
        this.activeItemsProperty().set(this.itemsProperty().get());
    }
}
