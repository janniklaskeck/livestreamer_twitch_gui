package app.lsgui.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.service.GenericService;
import app.lsgui.model.service.IService;
import app.lsgui.model.service.TwitchService;
import app.lsgui.utils.JSONUtils;
import app.lsgui.utils.LsGuiUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static final String FILEPATH = System.getProperty("user.home") + "/.lsgui/settings.json";

    private static Settings instance;

    private static final String VERSION = "";
    private static final long TIMEOUT = 5000L;
    private static ListProperty<IService> services = new SimpleListProperty<>();
    private static BooleanProperty sortTwitch = new SimpleBooleanProperty();
    private static boolean minimizeToTray = true;
    private static String windowStyle = "LightStyle";
    private static String currentService = "twitch.tv";
    private static String twitchUser = "";
    private static String twitchOAuth = "";
    private static int maxGamesLoad = 100;
    private static int maxChannelsLoad = 100;
    private static String liveStreamerExePath = "";
    private static String quality = "Best";
    private static String recordingPath;

    private static boolean isLoading = false;

    private static final String TWITCHUSERSTRING = "twitchusername";
    private static final String TWITCHOAUTHSTRING = "twitchoauth";
    private static final String TWITCHSORT = "twitchsorting";
    private static final String PATH = "recordingpath";
    private static final String CHANNELSLOAD = "load_max_channels";
    private static final String GAMESSLOAD = "load_max_games";
    private static final String SERVICENAME = "serviceName";
    private static final String SERVICEURL = "serviceURL";
    private static final String MINIMIZETOTRAYSTRING = "minimizetotray";
    private static final String WINDOWSTYLESTRING = "windowstyle";
    private static final String EXEPATHSTRING = "livestreamerexe";
    private static final String QUALITYSTRING = "quality";

    public static synchronized Settings instance() {
        if (instance == null) {
            instance = new Settings();
            final File settings = new File(FILEPATH);
            if (!isLoading && settings.exists() && settings.isFile()) {
                loadSettingsFromFile(settings);
            }
        }
        return instance;
    }

    /**
     * Save Settings
     */
    public void saveSettings() {
        File settings = null;
        try {
            settings = new File(FILEPATH);
            final boolean createdDirs = settings.getParentFile().mkdirs();
            final boolean result = settings.createNewFile();
            LOGGER.debug("Settings Dir created? {}. Settings file was created? {}", createdDirs, result);
        } catch (IOException e) {
            LOGGER.error("ERROR while creaing Settings file", e);
        }
        createSettingsJson(settings);
    }

    private static void loadSettingsFromFile(final File file) {
        isLoading = true;
        try (final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            final Gson g = new Gson();
            final JsonArray jArray = g.fromJson(sb.toString(), JsonArray.class);
            loadSettings(jArray);
            loadServices(jArray);
        } catch (IOException e) {
            LOGGER.error("ERROR while reading Settings file", e);
        }
    }

    private static void loadSettings(final JsonArray jArray) {
        final JsonObject settings = jArray.get(0).getAsJsonObject();
        sortTwitch.setValue(JSONUtils.getBooleanSafe(settings.get(TWITCHSORT), false));
        minimizeToTray = JSONUtils.getBooleanSafe(settings.get(MINIMIZETOTRAYSTRING), false);
        twitchUser = JSONUtils.getStringSafe(settings.get(TWITCHUSERSTRING), "");
        twitchOAuth = JSONUtils.getStringSafe(settings.get(TWITCHOAUTHSTRING), "");
        windowStyle = JSONUtils.getStringSafe(settings.get(WINDOWSTYLESTRING), "LightStyle");
        liveStreamerExePath = JSONUtils.getStringSafe(settings.get(EXEPATHSTRING), "");
        maxChannelsLoad = JSONUtils.getIntSafe(settings.get(CHANNELSLOAD), 20);
        maxGamesLoad = JSONUtils.getIntSafe(settings.get(GAMESSLOAD), 20);
        quality = JSONUtils.getStringSafe(settings.get(QUALITYSTRING), "Best");
        recordingPath = JSONUtils.getStringSafe(settings.get(PATH), System.getProperty("user.home"));
    }

    private static void loadServices(final JsonArray jArray) {
        services.set(FXCollections.observableArrayList());
        final JsonArray servicesArray = jArray.get(1).getAsJsonArray();
        for (int i = 0; i < servicesArray.size(); i++) {
            final JsonObject serviceJson = servicesArray.get(i).getAsJsonObject();
            final String serviceName = serviceJson.get(SERVICENAME).getAsString();
            final String serviceUrl = serviceJson.get(SERVICEURL).getAsString();
            final IService service;
            if (serviceUrl.toLowerCase().contains("twitch")) {
                service = new TwitchService(serviceName, serviceUrl);
            } else {
                service = new GenericService(serviceName, serviceUrl);
            }

            final JsonArray channels = serviceJson.get("channels").getAsJsonArray();
            for (int e = 0; e < channels.size(); e++) {
                final String channel = channels.get(e).getAsString();
                service.addChannel(channel);
            }
            services.get().add(service);
        }
    }

    private void createSettingsJson(final File file) {
        JsonWriter w;
        try (final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            w = new JsonWriter(writer);
            w.setIndent("  ");
            w.beginArray();
            w.beginObject();
            w.name(TWITCHUSERSTRING).value(twitchUser);
            w.name(TWITCHOAUTHSTRING).value(twitchOAuth);
            w.name(TWITCHSORT).value(sortTwitch.get());
            w.name(QUALITYSTRING).value(quality);
            w.name(PATH).value(getRecordingPath());
            w.name(CHANNELSLOAD).value(maxChannelsLoad);
            w.name(GAMESSLOAD).value(maxGamesLoad);
            w.name(MINIMIZETOTRAYSTRING).value(minimizeToTray);
            w.name(WINDOWSTYLESTRING).value(windowStyle);
            w.name(EXEPATHSTRING).value(liveStreamerExePath);
            w.endObject();
            w.beginArray();
            writeServices(w);
            w.endArray();
            w.endArray();
            w.close();
        } catch (IOException e) {
            LOGGER.error("ERROR while writing to Settings file", e);
        }
    }

    private void writeServices(final JsonWriter w) throws IOException {
        for (final IService s : services) {
            LOGGER.debug("Creating JSON for Service {}", s.getName().get());
            w.beginObject();
            w.name(SERVICENAME).value(s.getName().get());
            w.name(SERVICEURL).value(s.getUrl().get());
            w.name("channels");
            w.beginArray();
            for (final IChannel channel : s.getChannelProperty().get()) {
                if (channel.getName().get() != null) {
                    w.value(channel.getName().get());
                }
            }
            w.endArray();
            w.endObject();
        }
    }

    public ListProperty<IService> getStreamServices() {
        return services;
    }

    public BooleanProperty getSortTwitch() {
        return sortTwitch;
    }

    public String getCurrentStreamService() {
        return currentService;
    }

    public void setCurrentStreamService(final String currentStreamService) {
        Settings.currentService = currentStreamService;
    }

    public String getTwitchUser() {
        return twitchUser;
    }

    public void setTwitchUser(final String twitchUser) {
        Settings.twitchUser = twitchUser;
    }

    public String getTwitchOAuth() {
        return twitchOAuth;
    }

    public void setTwitchOAuth(final String twitchOAuth) {
        Settings.twitchOAuth = twitchOAuth;
    }

    public int getMaxGamesLoad() {
        return maxGamesLoad;
    }

    public void setMaxGamesLoad(final int maxGamesLoad) {
        Settings.maxGamesLoad = maxGamesLoad;
    }

    public int getMaxChannelsLoad() {
        return maxChannelsLoad;
    }

    public void setMaxChannelsLoad(final int maxChannelsLoad) {
        Settings.maxChannelsLoad = maxChannelsLoad;
    }

    public String getVERSION() {
        return VERSION;
    }

    public long getTimeout() {
        return TIMEOUT;
    }

    public boolean isMinimizeToTray() {
        return minimizeToTray;
    }

    public void setMinimizeToTray(final boolean minimizeToTray) {
        Settings.minimizeToTray = minimizeToTray;
    }

    public String getWindowStyle() {
        return windowStyle;
    }

    public void setWindowStyle(final String windowStyle) {
        Settings.windowStyle = windowStyle;
    }

    public void setLivestreamerExePath(final String absolutePath) {
        Settings.liveStreamerExePath = absolutePath;
    }

    public String getLivestreamerExePath() {
        return Settings.liveStreamerExePath;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(final String quality) {
        Settings.quality = quality;
    }

    public String getRecordingPath() {
        return recordingPath;
    }

    public void setRecordingPath(final String recordingPath) {
        Settings.recordingPath = recordingPath;
    }

    public IService getTwitchService() {
        final List<IService> servicesAsList = getStreamServices().get();
        final Optional<IService> serviceOptional = servicesAsList.stream().filter(LsGuiUtils::isTwitchService)
                .findFirst();
        if (serviceOptional.isPresent()) {
            return serviceOptional.get();
        }
        return null;
    }
}
