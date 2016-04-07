package app.lsgui.gui.streamlist;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.Channel;
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

public class StreamList extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamList.class);
    private static FXMLLoader loader;

    private final ListProperty<Channel> streams = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<Channel> streamProperty;
    private ObjectProperty<Channel> modelProperty;

    @FXML
    private ListView<Channel> streamListView;

    public StreamList() {
        LOGGER.debug("Construct StreamList");
        loader = new FXMLLoader(getClass().getResource("/StreamList.fxml"));
        getStylesheets().add(getClass().getResource("/ListView.css").toExternalForm());
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            LOGGER.error("ERROR while loading streamlist fxml", e);
        }
        streamListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        modelProperty = new SimpleObjectProperty<>();
        streamProperty = new SimpleListProperty<>();
        modelProperty.bind(streamListView.getSelectionModel().selectedItemProperty());
        streamListView.itemsProperty().bind(streamProperty);

        streamListView.setCellFactory(listView -> new StreamCell());

        streams.bind(getListView().itemsProperty());

    }

    /**
     * @return the streams
     */
    public ListProperty<Channel> getStreams() {
        return streamProperty;
    }

    /**
     * @param streams
     *            the streams to set
     */
    public void setStreams(List<Channel> streams) {
        streamProperty.set(FXCollections.observableList(streams));
    }

    /**
     * @return the streamList
     */
    public ListView<Channel> getListView() {
        return streamListView;
    }

    /**
     * @param streamList
     *            the streamList to set
     */
    public void setListView(ListView<Channel> streamList) {
        this.streamListView = streamList;
    }

    /**
     * @return the modelProperty
     */
    public ObjectProperty<Channel> getModelProperty() {
        return modelProperty;
    }

    /**
     * @param modelProperty
     *            the modelProperty to set
     */
    public void setModelProperty(ObjectProperty<Channel> modelProperty) {
        this.modelProperty = modelProperty;
    }
}