package app.lsgui.gui.streamList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.TwitchStreamModel;
import app.lsgui.service.twitch.TwitchProcessor;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class StreamList extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamList.class);
    private static FXMLLoader loader;

    private ListProperty<TwitchStreamModel> streamProperty;

    @FXML
    private ListView<TwitchStreamModel> streamList;

    public StreamList() {
        LOGGER.debug("Construct StreamList");
        loader = new FXMLLoader(getClass().getResource("/StreamList.fxml"));
        getStylesheets().add(getClass().getResource("/ListView.css").toExternalForm());
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        streamProperty = new SimpleListProperty<TwitchStreamModel>();
        streamList.itemsProperty().bind(streamProperty);
        streamList.setCellFactory(new Callback<ListView<TwitchStreamModel>, ListCell<TwitchStreamModel>>() {
            @Override
            public ListCell<TwitchStreamModel> call(ListView<TwitchStreamModel> param) {
                return new StreamCell();
            }
        });
    }

    public void addStream(final String name) {
        LOGGER.debug("Add Stream to List");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<TwitchStreamModel> streams = new ArrayList<TwitchStreamModel>(
                        getStreams().subList(0, getStreams().getSize()));
                streams.add(new TwitchStreamModel(name));
                getStreams().setValue(FXCollections.observableArrayList(streams));
            }
        });
    }

    public void removeSelectedStream() {
        if (!getListView().getSelectionModel().getSelectedItem().equals("")
                || !getListView().getSelectionModel().getSelectedItem().equals(null)) {
            TwitchStreamModel selectedStream = getListView().getSelectionModel().getSelectedItem();
            LOGGER.debug("Remove stream {} from list", selectedStream.getName());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    List<TwitchStreamModel> streams = getStreams().subList(0, getStreams().getSize());
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
                Set<String> set = TwitchProcessor.instance().getListOfFollowedStreams(username);
                List<TwitchStreamModel> list = new ArrayList<TwitchStreamModel>();
                for (String s : set) {
                    list.add(new TwitchStreamModel(s));
                }
                getStreams().setValue(FXCollections.observableArrayList(list));
            }
        });
    }

    /**
     * @return the streams
     */
    public ListProperty<TwitchStreamModel> getStreams() {
        return streamProperty;
    }

    /**
     * @param streams
     *            the streams to set
     */
    public void setStreams(List<TwitchStreamModel> streams) {
        streamProperty.set(FXCollections.observableList(streams));
    }

    /**
     * @return the streamList
     */
    public ListView<TwitchStreamModel> getListView() {
        return streamList;
    }

    /**
     * @param streamList
     *            the streamList to set
     */
    public void setListView(ListView<TwitchStreamModel> streamList) {
        this.streamList = streamList;
    }
}