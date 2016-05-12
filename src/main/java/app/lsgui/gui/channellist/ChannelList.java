package app.lsgui.gui.channellist;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IChannel;
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

public class ChannelList extends AnchorPane {// NOSONAR
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelList.class);
    private static final String CHANNELLISTFXML = "fxml/ChannelList.fxml";
    private static FXMLLoader loader;

    private ListProperty<IChannel> channelListProperty;
    private ObjectProperty<IChannel> channelProperty;

    @FXML
    private ListView<IChannel> channelListView;

    public ChannelList() {
        LOGGER.debug("Construct StreamList");

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
        channelProperty = new SimpleObjectProperty<>();
        channelListProperty = new SimpleListProperty<>();
        channelProperty.bind(channelListView.getSelectionModel().selectedItemProperty());
        channelListView.itemsProperty().bind(channelListProperty);
        channelListView.setCellFactory(listView -> new ChannelCell());
    }

    /**
     * @return the streams
     */
    public ListProperty<IChannel> getStreams() {
        return channelListProperty;
    }

    /**
     * @param streams
     *            the streams to set
     */
    public void setStreams(List<IChannel> streams) {
        channelListProperty.set(FXCollections.observableList(streams));
    }

    /**
     * @return the streamList
     */
    public ListView<IChannel> getListView() {
        return channelListView;
    }

    /**
     * @param streamList
     *            the streamList to set
     */
    public void setListView(ListView<IChannel> streamList) {
        this.channelListView = streamList;
    }

    /**
     * @return the modelProperty
     */
    public ObjectProperty<IChannel> getModelProperty() {
        return channelProperty;
    }

    /**
     * @param modelProperty
     *            the modelProperty to set
     */
    public void setModelProperty(ObjectProperty<IChannel> modelProperty) {
        this.channelProperty = modelProperty;
    }
}