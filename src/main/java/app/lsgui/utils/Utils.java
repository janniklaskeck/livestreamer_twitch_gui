package app.lsgui.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.gui.LsGUIWindow;
import app.lsgui.gui.chat.ChatWindow;
import app.lsgui.model.channel.IChannel;
import app.lsgui.model.service.GenericService;
import app.lsgui.model.service.IService;
import app.lsgui.model.service.TwitchService;
import app.lsgui.model.twitch.channel.TwitchChannel;
import app.lsgui.rest.twitch.TwitchAPIClient;
import app.lsgui.settings.Settings;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    /**
     *
     * @param url
     */
    public static void openURLInBrowser(final String url) {
	LOGGER.info("Open Browser URL {}", url);
	try {
	    URI uri = new URI(url);
	    Desktop.getDesktop().browse(uri);
	} catch (IOException | URISyntaxException e) {
	    LOGGER.error("ERROR while opening URL in Browser", e);
	}
    }

    /**
     *
     * @param url
     * @return
     */
    public static List<String> getAvailableQuality(final String url) {
	final List<String> qualities = new ArrayList<>();
	final JsonObject qualitiesJson = LivestreamerUtils.getQualityJsonFromLivestreamer(url);
	if (!qualitiesJson.toString().contains("error")) {
	    final JsonObject jsonQualitiyList = qualitiesJson.get("streams").getAsJsonObject();
	    jsonQualitiyList.entrySet().forEach(entry -> qualities.add(entry.getKey()));
	    return sortQualities(qualities);
	}
	return qualities;
    }

    private static List<String> sortQualities(final List<String> qualities) {
	final List<String> sortedQualities = new ArrayList<>();
	qualities.forEach(s -> s = s.toLowerCase());
	if (qualities.contains("audio")) {
	    sortedQualities.add("Audio");
	}
	if (qualities.contains("mobile")) {
	    sortedQualities.add("Mobile");
	}
	if (qualities.contains("low")) {
	    sortedQualities.add("Low");
	}
	if (qualities.contains("medium")) {
	    sortedQualities.add("Medium");
	}
	if (qualities.contains("high")) {
	    sortedQualities.add("High");
	}
	if (qualities.contains("source")) {
	    sortedQualities.add("Source");
	}
	if (sortedQualities.isEmpty()) {
	    sortedQualities.add("Worst");
	    sortedQualities.add("Best");
	}
	return sortedQualities;
    }

    /**
     *
     * @param input
     * @return
     */
    public static String getColorFromString(final String input) {
	int r = 0;
	int g = 0;
	int b = 0;
	if (!"".equals(input)) {
	    int hash = input.hashCode();
	    r = (hash & 0xFF0000) >> 16;
	    if (r > 200) {
		r = 200;
	    }
	    g = (hash & 0x00FF00) >> 8;
	    if (g > 200) {
		g = 200;
	    }
	    b = hash & 0x0000FF;
	    if (b > 200) {
		b = 200;
	    }
	}
	return "rgb(" + r + "," + g + "," + b + ")";
    }

    /**
     *
     * @param stage
     * @param style
     */
    public static void addStyleSheetToStage(final Stage stage, final String style) {
	if (stage != null && !stage.getScene().getStylesheets().contains(style) && !"".equals(style)) {
	    stage.getScene().getStylesheets().add(style);
	}
    }

    /**
     *
     * @param stage
     */
    public static void clearStyleSheetsFromStage(final Stage stage) {
	if (stage != null) {
	    stage.getScene().getStylesheets().clear();
	}
    }

    /**
     *
     * @param channel
     * @return
     */
    public static boolean isTwitchChannel(final IChannel channel) {
	return channel.getClass().equals(TwitchChannel.class);
    }

    /**
     *
     * @param service
     * @return
     */
    public static boolean isTwitchService(final IService service) {
	return service.getClass().equals(TwitchService.class);
    }

    /**
     *
     * @param channel
     * @param service
     */
    public static void addChannelToService(final String channel, final IService service) {
	if (isTwitchService(service) && !"".equals(channel)) {
	    if (TwitchAPIClient.getInstance().channelExists(channel)) {
		service.addChannel(channel);
	    }
	} else {
	    service.addChannel(channel);
	}
    }

    /**
     *
     * @param username
     * @param service
     */
    public static void addFollowedChannelsToService(final String username, final TwitchService service) {
	if (!"".equals(username)) {
	    service.addFollowedChannels(username);
	}
    }

    /**
     *
     * @param channel
     * @param service
     */
    public static void removeChannelFromService(final IChannel channel, final IService service) {
	service.removeChannel(channel);
    }

    /**
     *
     * @param serviceName
     * @param serviceUrl
     */
    public static void addService(final String serviceName, final String serviceUrl) {
	LOGGER.debug("Add new Service {} with URL {}", serviceName, serviceUrl);
	if (!"".equals(serviceName) && !"".equals(serviceUrl)) {
	    String correctedUrl = correctUrl(serviceUrl);
	    Settings.instance().getStreamServices().add(new GenericService(serviceName, correctedUrl));
	}
    }

    private static String correctUrl(final String url) {
	if (!url.endsWith("/")) {
	    return url + "/";
	}
	return url;
    }

    /**
     *
     * @param serviceUrl
     * @param channelUrl
     * @return
     */
    public static String buildUrl(final String serviceUrl, final String channelUrl) {
	return serviceUrl + channelUrl;
    }

    /**
     *
     * @param channel
     * @return
     */
    public static boolean isChannelOnline(final IChannel channel) {
	if (channel != null) {
	    if (Utils.isTwitchChannel(channel)) {
		return channel.isOnline().get();
	    } else {
		return true;
	    }
	}
	return false;
    }

    /**
     *
     * @param service
     * @param channel
     */
    public static void recordStream(final IService service, final IChannel channel) {
	if (Utils.isChannelOnline(channel)) {
	    final String url = buildUrl(service.getUrl().get(), channel.getName().get());
	    final String quality = Settings.instance().getQuality();

	    final FileChooser recordFileChooser = new FileChooser();
	    recordFileChooser.setTitle("Choose Target file");
	    recordFileChooser.getExtensionFilters().add(new ExtensionFilter("MPEG4", ".mpeg4"));
	    final File recordFile = recordFileChooser.showSaveDialog(LsGUIWindow.getRootStage());
	    if (recordFile != null) {
		LivestreamerUtils.recordLivestreamer(url, quality, recordFile);
	    }
	}
    }

    /**
     *
     * @param channel
     */
    public static void openTwitchChat(final IChannel channel) {
	if (Utils.isChannelOnline(channel) && Utils.isTwitchChannel(channel)) {
	    final String channelName = channel.getName().get();
	    ChatWindow cw = new ChatWindow(channelName);
	    cw.connect();
	}
    }

    /**
     *
     * @param service
     */
    public static void removeService(final IService service) {
	LOGGER.debug("Removing Service {}", service.getName().get());
	Settings.instance().getStreamServices().remove(service);
    }
}
