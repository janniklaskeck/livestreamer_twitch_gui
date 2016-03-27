package app.lsgui.gui.streamList;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}