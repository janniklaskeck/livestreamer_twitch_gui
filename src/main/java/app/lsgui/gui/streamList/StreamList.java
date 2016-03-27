package app.lsgui.gui.streamList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.serviceapi.twitch.TwitchProcessor;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class StreamList extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamList.class);
    private static FXMLLoader loader;

    private ListProperty<String> streamProperty;

    @FXML
    private ListView<String> streamList;

    public StreamList() {
        LOGGER.debug("Construct StreamList");
        loader = new FXMLLoader(getClass().getResource("/StreamList.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        streamProperty = new SimpleListProperty<String>();
        streamList.itemsProperty().bind(streamProperty);
    }

    public void addStream(final String name) {
        LOGGER.debug("Add Stream to List");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<String> streams = new ArrayList<String>(getStreams().subList(0, getStreams().getSize()));
                streams.add(name);
                getStreams().setValue(FXCollections.observableArrayList(streams));
            }
        });
    }

    public void removeSelectedStream() {
        if (!getListView().getSelectionModel().getSelectedItem().equals("")
                || !getListView().getSelectionModel().getSelectedItem().equals(null)) {
            String selectedStream = getListView().getSelectionModel().getSelectedItem();
            LOGGER.debug("Remove stream {} from list", selectedStream);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    List<String> streams = getStreams().subList(0, getStreams().getSize());
                    streams.remove(selectedStream);
                    getStreams().setValue(FXCollections.observableArrayList(streams));
                }
            });
        }
    }

    public void addFollowedStreams(final String username) {
        LOGGER.debug("Import followed Streams for user {}", username);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Set<String> list = TwitchProcessor.instance().getListOfFollowedStreams(username);
                List<String> list2 = new ArrayList<String>();
                list2.addAll(list);
                getStreams().setValue(FXCollections.observableArrayList(list2));
            }
        });
    }

    /**
     * @return the streams
     */
    public ListProperty<String> getStreams() {
        return streamProperty;
    }

    /**
     * @param streams
     *            the streams to set
     */
    public void setStreams(List<String> streams) {
        streamProperty.set(FXCollections.observableList(streams));
    }

    /**
     * @return the streamList
     */
    public ListView<String> getListView() {
        return streamList;
    }

    /**
     * @param streamList
     *            the streamList to set
     */
    public void setListView(ListView<String> streamList) {
        this.streamList = streamList;
    }
}