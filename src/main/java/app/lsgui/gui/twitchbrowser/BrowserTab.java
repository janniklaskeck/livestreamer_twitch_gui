/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.gui.twitchbrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.ITwitchItem;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;

public final class BrowserTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserTab.class);
    private static final double ITEM_GAP = 15.0D;
    private static final int PREFERED_COLUMNS = 5;

    private ListProperty<ITwitchItem> items = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<ITwitchItem> activeItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ScrollPane content;

    public BrowserTab(final String name) {
        super(name);
        this.content = this.buildContent();
        setContent(this.content);
        LOGGER.trace("Created Browsertab for: {}", name);
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
        pane.setOnScroll(scrollEvent -> {
            final double deltaY = scrollEvent.getDeltaY() * 2.0D;
            final double height = BrowserTab.this.getCustomContent().getBoundsInLocal().getHeight();
            final double vValue = BrowserTab.this.getCustomContent().getVvalue();
            BrowserTab.this.getCustomContent().setVvalue(vValue - deltaY / height);
        });
        pane.setPrefColumns(PREFERED_COLUMNS);
        pane.setVgap(ITEM_GAP);
        pane.setHgap(ITEM_GAP);
        pane.setPadding(new Insets(ITEM_GAP));
        this.activeItemsProperty().addListener((ListChangeListener.Change<? extends ITwitchItem> c) -> pane
                .getChildren().setAll(convertToNodeList(this.activeItemsProperty().get())));
        scrollPane.setContent(pane);
        return scrollPane;
    }

    private static ObservableList<Node> convertToNodeList(final ObservableList<ITwitchItem> items) {
        final ObservableList<Node> list = FXCollections.observableArrayList();
        items.stream().forEach(item -> list.add(convertToBorderPane(item)));
        return list;
    }

    private static BorderPane convertToBorderPane(final ITwitchItem item) {
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
