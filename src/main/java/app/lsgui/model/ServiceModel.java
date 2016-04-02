package app.lsgui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.TwitchStreamModel;
import app.lsgui.service.twitch.TwitchChannelUpdateService;
import app.lsgui.service.twitch.TwitchProcessor;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class ServiceModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceModel.class);

    private StringProperty name;
    private StringProperty url;
    private ListProperty<StreamModel> channels;
    private ObservableList<StreamModel> streams;

    public final static ObservableMap<StreamModel, TwitchChannelUpdateService> UPDATESERVICES = FXCollections
            .observableHashMap();

    public ServiceModel(String name, String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        this.channels = new SimpleListProperty<StreamModel>();
        this.streams = FXCollections.observableArrayList();
    }

    public void addStream(final String name) {
        LOGGER.debug("Add Stream to List");
        List<StreamModel> streams = new ArrayList<StreamModel>(getChannels().subList(0, getChannels().getSize()));
        StreamModel sm = new TwitchStreamModel(name);
        streams.add(sm);
        getChannels().setValue(FXCollections.observableArrayList(streams));

        final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(sm);
        tcus.start();
        UPDATESERVICES.put(sm, tcus);
    }

    public void removeSelectedStream(final StreamModel selectedStream) {
        if (selectedStream != null) {
            LOGGER.debug("Remove stream {} from list", selectedStream.getName());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    List<StreamModel> streams = getChannels().subList(0, getChannels().getSize());
                    streams.remove(selectedStream);
                    getChannels().setValue(FXCollections.observableArrayList(streams));
                }
            });

            final TwitchChannelUpdateService tcus = UPDATESERVICES.remove(selectedStream);
            tcus.cancel();
        }
    }

    public void addFollowedStreams(final String username) {
        LOGGER.debug("Import followed Streams for user {}", username);

        Set<String> set = TwitchProcessor.instance().getListOfFollowedStreams(username);
        List<StreamModel> list = new ArrayList<StreamModel>();
        for (String s : set) {
            list.add(new TwitchStreamModel(s));
        }
        streams = FXCollections.observableArrayList(list);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getChannels().setValue(streams);
            }
        });

        for (StreamModel sm : streams) {
            final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(sm);
            tcus.start();
            UPDATESERVICES.put(sm, tcus);
        }
    }

    public void forceRefresh() {
        for (StreamModel sm : channels) {
            channels.set(channels.indexOf(sm), sm);
        }
    }

    public ListProperty<StreamModel> getChannels() {
        return channels;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getUrl() {
        return url;
    }

}
