package streamService;

import java.io.IOException;

import app.channel.Channel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class StreamService extends AnchorPane {

    private String name;
    private String url;
    private ListProperty<Channel> channelList;
    private FXMLLoader loader;

    @FXML
    private Label serviceLabel;

    public StreamService(String name, String url) {
        this.name = name;
        this.url = url;
        channelList = new SimpleListProperty<Channel>(FXCollections.observableArrayList());

        loader = new FXMLLoader(getClass().getResource("StreamService.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serviceLabel.setTextFill(Color.BLACK);
        serviceLabel.setText(name);
        serviceLabel.setTooltip(new Tooltip(url));
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the channels
     */
    public ListProperty<Channel> getChannels() {
        return channelList;
    }

    public ObservableList<Channel> getChannelsObservable() {
        return channelList.get();
    }

}
