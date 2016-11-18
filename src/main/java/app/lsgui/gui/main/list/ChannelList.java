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
package app.lsgui.gui.main.list;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IChannel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;

public final class ChannelList extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelList.class);
    private static final String CHANNELLISTFXML = "fxml/ChannelList.fxml";

    private ListProperty<IChannel> channelListProperty = new SimpleListProperty<>();
    private ObjectProperty<IChannel> selectedChannelProperty = new SimpleObjectProperty<>();

    @FXML
    private ListView<IChannel> channelListView;

    public ChannelList() {
        LOGGER.debug("Construct StreamList");
        final FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load(getClass().getClassLoader().getResourceAsStream(CHANNELLISTFXML));
        } catch (IOException e) {
            LOGGER.error("ERROR while loading streamlist fxml", e);
        }
        this.setupChannelList();
    }

    private void setupChannelList() {
        final MultipleSelectionModel<IChannel> selectionModel = this.channelListView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        this.selectedChannelProperty.bind(selectionModel.selectedItemProperty());
        this.channelListView.itemsProperty().bind(this.channelListProperty);
        this.channelListView.setCellFactory(listView -> new ChannelCell());
    }

    public ListProperty<IChannel> channelListProperty() {
        return this.channelListProperty;
    }

    public ListView<IChannel> getListView() {
        return this.channelListView;
    }

    public ObjectProperty<IChannel> selectedChannelProperty() {
        return this.selectedChannelProperty;
    }

}
