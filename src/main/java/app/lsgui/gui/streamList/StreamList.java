package app.lsgui.gui.streamList;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.StreamModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class StreamList extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamList.class);
    private static FXMLLoader loader;

    private final ListProperty<StreamModel> streams = new SimpleListProperty<StreamModel>(
            FXCollections.observableArrayList());

    private ListProperty<StreamModel> streamProperty;
    private ObjectProperty<StreamModel> modelProperty;

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
        streamList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        modelProperty = new SimpleObjectProperty<StreamModel>();
        streamProperty = new SimpleListProperty<StreamModel>();
        modelProperty.bind(streamList.getSelectionModel().selectedItemProperty());
        streamList.itemsProperty().bind(streamProperty);

        streamList.setCellFactory(new Callback<ListView<StreamModel>, ListCell<StreamModel>>() {
            @Override
            public ListCell<StreamModel> call(ListView<StreamModel> param) {
                return new StreamCell();
            }
        });
        streams.bind(getListView().itemsProperty());

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

    /**
     * @return the modelProperty
     */
    public ObjectProperty<StreamModel> getModelProperty() {
        return modelProperty;
    }

    /**
     * @param modelProperty
     *            the modelProperty to set
     */
    public void setModelProperty(ObjectProperty<StreamModel> modelProperty) {
        this.modelProperty = modelProperty;
    }
}