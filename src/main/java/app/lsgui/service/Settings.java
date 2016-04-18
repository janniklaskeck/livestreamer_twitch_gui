package app.lsgui.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import junit.runner.Version;

public class Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static final String FILEPATH = System.getProperty("user.home") + "/.lsgui/settings.json";

    private static Settings instance;

    private static final Version VERSION = null;
    private static final long TIMEOUT = 5000L;
    private List<Service> services = new ArrayList<>();
    private BooleanProperty sortTwitch = new SimpleBooleanProperty();
    private boolean minimizeToTray = true;
    private String currentService = "twitch.tv";
    private String twitchUser = "";
    private String twitchOAuth = "";
    private int maxGamesLoad = 20;
    private int maxChannelsLoad = 20;

    private PrintStream logPrintStream;
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
            twitchUser = settings.get(TWITCHUSERSTRING).getAsString();
            twitchOAuth = settings.get(TWITCHOAUTHSTRING).getAsString();
            sortTwitch.setValue(settings.get(TWITCHSORT).getAsBoolean());
            maxChannelsLoad = settings.get(CHANNELSLOAD).getAsInt();
            maxGamesLoad = settings.get(GAMESSLOAD).getAsInt();
            minimizeToTray = settings.get(MINIMIZETOTRAYSTRING).getAsBoolean();

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
            w.endObject();
            w.beginArray();
            for (Service s : services) {
                w.beginObject();
                w.name(SERVICENAME).value(s.getName().get());
                w.name(SERVICEURL).value(s.getUrl().get());
                w.name("channels");
                w.beginArray();
                for (Channel channel : s.getChannels()) {
                    w.value(channel.getName().get());
                }
                w.endArray();
                w.endObject();
            }
            w.endArray();

            w.endArray();
            w.close();
        } catch (IOException e) {
            LOGGER.error("ERROR while writing to Settings file", e);
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

    public Version getVERSION() {
        return VERSION;
    }

    public long getTimeout() {
        return TIMEOUT;
    }

    /**
     * @return the logPrintStream
     */
    public PrintStream getLogPrintStream() {
        return logPrintStream;
    }

    /**
     * @param logPrintStream
     *            the logPrintStream to set
     */
    public void setLogPrintStream(PrintStream logPrintStream) {
        this.logPrintStream = logPrintStream;
    }

    /**
     * @return the minimizeToTray
     */
    public boolean isMinimizeToTray() {
        return minimizeToTray;
    }

    /**
     * @param minimizeToTray
     *            the minimizeToTray to set
     */
    public void setMinimizeToTray(boolean minimizeToTray) {
        this.minimizeToTray = minimizeToTray;
    }

}
