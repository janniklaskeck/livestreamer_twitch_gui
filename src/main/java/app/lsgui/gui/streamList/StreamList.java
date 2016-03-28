package app.lsgui.gui.streamList;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.StreamModel;
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

    private ListProperty<StreamModel> streamProperty;

    @FXML
    private ListView<StreamModel> streamList;

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
        streamProperty = new SimpleListProperty<StreamModel>();
        streamList.itemsProperty().bind(streamProperty);
        streamList.setCellFactory(new Callback<ListView<StreamModel>, ListCell<StreamModel>>() {
            @Override
            public ListCell<StreamModel> call(ListView<StreamModel> param) {
                return new StreamCell();
            }
        });
    }

    /**
     * @return the streams
     */
    public ListProperty<StreamModel> getStreams() {
        return streamProperty;
    }

    /**
     * @param streams
     *            the streams to set
     */
    public void setStreams(List<StreamModel> streams) {
        streamProperty.set(FXCollections.observableList(streams));
    }

    /**
     * @return the streamList
     */
    public ListView<StreamModel> getListView() {
        return streamList;
    }

    /**
     * @param streamList
     *            the streamList to set
     */
    public void setListView(ListView<StreamModel> streamList) {
        this.streamList = streamList;
    }
}