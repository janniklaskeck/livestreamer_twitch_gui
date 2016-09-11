package app.lsgui.gui.channellist;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;

public class ChannelList extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelList.class);
    private static final String CHANNELLISTFXML = "fxml/ChannelList.fxml";

    private ListProperty<IChannel> channelListProperty;
    private ObjectProperty<IChannel> selectedChannelProperty;

    @FXML
    private ListView<IChannel> channelListView;

    /**
     * ChannelList
     */
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
        setupChannelList();
    }

    private void setupChannelList() {
        channelListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        selectedChannelProperty = new SimpleObjectProperty<>();
        channelListProperty = new SimpleListProperty<>();
        selectedChannelProperty.bind(channelListView.getSelectionModel().selectedItemProperty());
        channelListView.itemsProperty().bind(channelListProperty);
        channelListView.setCellFactory(listView -> new ChannelCell());
    }

    public ListProperty<IChannel> getStreams() {
        return channelListProperty;
    }

    public void setChannels(List<IChannel> channels) {
        channelListProperty.set(FXCollections.observableList(channels));
    }

    public ListView<IChannel> getListView() {
        return channelListView;
    }

    public void setListView(ListView<IChannel> channelList) {
        this.channelListView = channelList;
    }

    public ObjectProperty<IChannel> getSelectedChannelProperty() {
        return selectedChannelProperty;
    }

    public void setSelectedChannelProperty(ObjectProperty<IChannel> channelProperty) {
        this.selectedChannelProperty = channelProperty;
    }
}