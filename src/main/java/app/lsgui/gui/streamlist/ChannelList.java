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

public class ChannelList extends AnchorPane {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelList.class);
	private static final String CHANNELLISTFXML = "fxml/channellist.fxml";
	private static FXMLLoader loader;

	private final ListProperty<Channel> streams = new SimpleListProperty<>(FXCollections.observableArrayList());

	private ListProperty<Channel> channelProperty;
	private ObjectProperty<Channel> modelProperty;

	@FXML
	private ListView<Channel> channelListView;

	public ChannelList() {
		LOGGER.debug("Construct StreamList");

		loader = new FXMLLoader();
		getStylesheets().add(getClass().getResource("/ListView.css").toExternalForm());
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load(getClass().getClassLoader().getResourceAsStream(CHANNELLISTFXML));
		} catch (IOException e) {
			LOGGER.error("ERROR while loading streamlist fxml", e);
		}
		if (channelListView == null) {
			LOGGER.error("asdasdasdasdasdasd");
		}
		channelListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		modelProperty = new SimpleObjectProperty<>();
		channelProperty = new SimpleListProperty<>();
		modelProperty.bind(channelListView.getSelectionModel().selectedItemProperty());
		channelListView.itemsProperty().bind(channelProperty);

		channelListView.setCellFactory(listView -> new StreamCell());

		streams.bind(getListView().itemsProperty());

	}

	/**
	 * @return the streams
	 */
	public ListProperty<Channel> getStreams() {
		return channelProperty;
	}

	/**
	 * @param streams
	 *            the streams to set
	 */
	public void setStreams(List<Channel> streams) {
		channelProperty.set(FXCollections.observableList(streams));
	}

	/**
	 * @return the streamList
	 */
	public ListView<Channel> getListView() {
		return channelListView;
	}

	/**
	 * @param streamList
	 *            the streamList to set
	 */
	public void setListView(ListView<Channel> streamList) {
		this.channelListView = streamList;
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