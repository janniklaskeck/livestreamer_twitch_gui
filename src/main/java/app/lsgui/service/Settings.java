package app.lsgui.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import app.lsgui.model.Channel;
import app.lsgui.model.Service;
import app.lsgui.utils.JSONUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static final String FILEPATH = System.getProperty("user.home") + "/.lsgui/settings.json";

    private static Settings instance;

    private static final String VERSION = "";
    private static final long TIMEOUT = 5000L;
    private List<Service> services = new ArrayList<>();
    private BooleanProperty sortTwitch = new SimpleBooleanProperty();
    private boolean minimizeToTray = true;
    private String windowStyle = "LightStyle";
    private String currentService = "twitch.tv";
    private String twitchUser = "";
    private String twitchOAuth = "";
    private int maxGamesLoad = 20;
    private int maxChannelsLoad = 20;
    private String liveStreamerExePath;

    private boolean isLoading = false;

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

    private Settings() {
        File settings = new File(FILEPATH);
        if (settings.exists() && settings.isFile() && !isLoading) {
            loadSettings(settings);
        }
    }

    public static Settings instance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public void saveSettings() {
        File settings = null;
        try {
            settings = new File(FILEPATH);

            settings.getParentFile().mkdirs();
            settings.createNewFile();
        } catch (IOException e) {
            LOGGER.error("ERROR while creaing Settings file", e);
        }
        createSettingsJson(settings);
    }

    public void loadSettings(File file) {
        isLoading = true;
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            Gson g = new Gson();
            JsonArray jArray = g.fromJson(sb.toString(), JsonArray.class);
            JsonObject settings = jArray.get(0).getAsJsonObject();

            JsonArray servicesArray = jArray.get(1).getAsJsonArray();
            for (int i = 0; i < servicesArray.size(); i++) {
                JsonObject service = servicesArray.get(i).getAsJsonObject();
                Service ss = new Service(service.get(SERVICENAME).getAsString(), service.get(SERVICEURL).getAsString());
                ss.bindSortProperty(sortTwitch);
                JsonArray channels = service.get("channels").getAsJsonArray();
                for (int e = 0; e < channels.size(); e++) {
                    ss.addChannel(channels.get(e).getAsString());
                }
                services.add(ss);
            }
            sortTwitch.setValue(JSONUtils.getBooleanSafe(settings.get(TWITCHSORT), false));
            minimizeToTray = JSONUtils.getBooleanSafe(settings.get(MINIMIZETOTRAYSTRING), false);
            twitchUser = JSONUtils.getStringSafe(settings.get(TWITCHUSERSTRING), "");
            twitchOAuth = JSONUtils.getStringSafe(settings.get(TWITCHOAUTHSTRING), "");
            windowStyle = JSONUtils.getStringSafe(settings.get(WINDOWSTYLESTRING), "LightStyle");
            liveStreamerExePath = JSONUtils.getStringSafe(settings.get(EXEPATHSTRING), "");
            maxChannelsLoad = JSONUtils.getIntSafe(settings.get(CHANNELSLOAD), 20);
            maxGamesLoad = JSONUtils.getIntSafe(settings.get(GAMESSLOAD), 20);

        } catch (IOException e) {
            LOGGER.error("ERROR while reading Settings file", e);
        }
    }

    private void createSettingsJson(File file) {
        JsonWriter w;
        try {
            w = new JsonWriter(new FileWriter(file));
            w.setIndent("  ");
            w.beginArray();
            w.beginObject();
            w.name(TWITCHUSERSTRING).value(twitchUser);
            w.name(TWITCHOAUTHSTRING).value(twitchOAuth);
            w.name(TWITCHSORT).value(sortTwitch.get());
            w.name(PATH).value("");
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

    private void writeServices(JsonWriter w) throws IOException {
        for (Service s : services) {
            w.beginObject();
            w.name(SERVICENAME).value(s.getName().get());
            w.name(SERVICEURL).value(s.getUrl().get());
            w.name("channels");
            w.beginArray();
            for (Channel channel : s.getChannels()) {
                if (channel.getName().get() != null) {
                    w.value(channel.getName().get());
                }
            }
            w.endArray();
            w.endObject();
        }
    }

    public List<Service> getStreamServices() {
        return services;
    }

    public ObservableList<Service> getStreamServicesObservable() {
        return FXCollections.observableArrayList(services);
    }

    public BooleanProperty getSortTwitch() {
        return sortTwitch;
    }

    public String getCurrentStreamService() {
        return currentService;
    }

    public void setCurrentStreamService(String currentStreamService) {
        this.currentService = currentStreamService;
    }

    public String getTwitchUser() {
        return twitchUser;
    }

    public void setTwitchUser(String twitchUser) {
        this.twitchUser = twitchUser;
    }

    public String getTwitchOAuth() {
        return twitchOAuth;
    }

    public void setTwitchOAuth(String twitchOAuth) {
        this.twitchOAuth = twitchOAuth;
    }

    public int getMaxGamesLoad() {
        return maxGamesLoad;
    }

    public void setMaxGamesLoad(int maxGamesLoad) {
        this.maxGamesLoad = maxGamesLoad;
    }

    public int getMaxChannelsLoad() {
        return maxChannelsLoad;
    }

    public void setMaxChannelsLoad(int maxChannelsLoad) {
        this.maxChannelsLoad = maxChannelsLoad;
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

    public void setMinimizeToTray(boolean minimizeToTray) {
        this.minimizeToTray = minimizeToTray;
    }

    public String getWindowStyle() {
        return windowStyle;
    }

    public void setWindowStyle(String windowStyle) {
        this.windowStyle = windowStyle;
    }

    public void setLivestreamerExePath(final String absolutePath) {
        this.liveStreamerExePath = absolutePath;
    }

    public String getLivestreamerExePath() {
        return this.liveStreamerExePath;
    }
}
