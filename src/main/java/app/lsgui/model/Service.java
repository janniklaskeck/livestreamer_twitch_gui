package app.lsgui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.service.twitch.TwitchAPIClient;
import app.lsgui.service.twitch.TwitchChannelUpdateService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Service {
	private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

	private StringProperty name;
	private StringProperty url;
	private ListProperty<Channel> channelProperty;
	private ObservableList<Channel> observableChannels;

	public final static ObservableMap<Channel, TwitchChannelUpdateService> UPDATESERVICES = FXCollections
			.observableHashMap();

	public Service(String name, String url) {
		this.name = new SimpleStringProperty(name);
		this.url = new SimpleStringProperty(url);
		this.channelProperty = new SimpleListProperty<Channel>();
		this.observableChannels = FXCollections.observableArrayList();
	}

	public void addChannel(final String name) {
		LOGGER.debug("Add Channel to List");
		List<Channel> channels = new ArrayList<Channel>(getChannels().subList(0, getChannels().getSize()));
		Channel sm = new TwitchChannel(name);
		channels.add(sm);
		ObservableList<Channel> obsStreams = FXCollections.observableArrayList(TwitchChannel.extractor());
		obsStreams.addAll(channels);
		getChannels().setValue(obsStreams);

		final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(sm);
		tcus.start();
		UPDATESERVICES.put(sm, tcus);
	}

	public void removeSelectedChannel(final Channel selectedStream) {
		if (selectedStream != null) {
			LOGGER.debug("Remove Channel {} from list", selectedStream.getName());
			List<Channel> channels = getChannels().subList(0, getChannels().getSize());
			channels.remove(selectedStream);
			ObservableList<Channel> obsStreams = FXCollections.observableArrayList(TwitchChannel.extractor());
			obsStreams.addAll(channels);
			getChannels().setValue(obsStreams);

			final TwitchChannelUpdateService tcus = UPDATESERVICES.remove(selectedStream);
			tcus.cancel();
		}
	}

	public void addFollowedChannels(final String username) {
		LOGGER.debug("Import followed Streams for user {}", username);

		Set<String> set = TwitchAPIClient.instance().getListOfFollowedStreams(username);
		List<Channel> channels = new ArrayList<Channel>();
		for (String s : set) {
			channels.add(new TwitchChannel(s));
		}
		observableChannels = FXCollections.observableArrayList(channels);
		ObservableList<Channel> obsChannel = FXCollections.observableArrayList(TwitchChannel.extractor());
		obsChannel.addAll(observableChannels);
		getChannels().setValue(obsChannel);
		// TODO Find better Solution

		for (Channel c : observableChannels) {
			final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(c);
			tcus.start();
			UPDATESERVICES.put(c, tcus);
		}
	}

	public ListProperty<Channel> getChannels() {
		return channelProperty;
	}

	public StringProperty getName() {
		return name;
	}

	public StringProperty getUrl() {
		return url;
	}

}
