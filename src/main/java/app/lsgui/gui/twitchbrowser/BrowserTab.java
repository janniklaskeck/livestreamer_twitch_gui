package app.lsgui.gui.twitchbrowser;

import app.lsgui.model.twitch.ITwitchItem;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;

public final class BrowserTab extends Tab {

    private ListProperty<ITwitchItem> items = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<ITwitchItem> activeItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ScrollPane content;

    public BrowserTab(final String name) {
        super(name);
        this.content = this.buildContent();
        setContent(this.content);
    }

    public ScrollPane getCustomContent() {
        return this.content;
    }

    private ScrollPane buildContent() {
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        final TilePane pane = new TilePane();
        pane.setPrefColumns(5);
        pane.setVgap(10.0D);
        pane.setHgap(15.0D);
        this.activeItemsProperty().addListener((ListChangeListener.Change<? extends ITwitchItem> c) -> {
            pane.getChildren().setAll(this.convertToNodeList(this.activeItemsProperty().get()));
        });
        scrollPane.setContent(pane);
        return scrollPane;
    }

    private ObservableList<Node> convertToNodeList(final ObservableList<ITwitchItem> items) {
        final ObservableList<Node> list = FXCollections.observableArrayList();
        items.stream().forEach((item) -> {
            list.add(this.convertToBorderPane(item));
        });
        return list;
    }

    private BorderPane convertToBorderPane(final ITwitchItem item) {
        return new TwitchItemPane(item);
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
