package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridView;

import app.lsgui.model.twitch.ITwitchItem;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

public final class BrowserTab extends Tab {

    private ListProperty<ITwitchItem> items = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<ITwitchItem> activeItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private GridView<ITwitchItem> contentGridView;

    public BrowserTab(final String name) {
        super(name);
        this.contentGridView = this.buildGridView();
        setContent(this.contentGridView);
    }

    public GridView<ITwitchItem> getGridView() {
        return this.contentGridView;
    }

    private GridView<ITwitchItem> buildGridView() {
        final GridView<ITwitchItem> gridView = new GridView<>();
        gridView.setCellFactory(param -> new TwitchItemPane());
        gridView.setCellWidth(TwitchItemPane.WIDTH);
        gridView.cellHeightProperty().bind(TwitchItemPane.HEIGHT_PROPERTY);
        gridView.itemsProperty().bind(this.activeItemsProperty());
        return gridView;
    }

    public void refresh() {
        final ObservableList<ITwitchItem> gridViewItems = this.getGridView().getItems();
        this.getGridView().itemsProperty().unbind();
        this.getGridView().setItems(FXCollections.observableArrayList());
        this.getGridView().setItems(gridViewItems);
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
