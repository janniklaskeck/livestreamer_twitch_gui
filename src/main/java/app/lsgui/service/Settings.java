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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import app.lsgui.model.ServiceModel;
import app.lsgui.model.StreamModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.misc.Version;

public class Settings {

    private final static String FILEPATH = System.getProperty("user.home") + "/.lsgui/settings.json";

    private static Settings instance;

    private List<ServiceModel> streamServices = new ArrayList<ServiceModel>();
    private boolean sortTwitch = true;
    private boolean showPreview = true;
    private boolean autoUpdate = true;
    private boolean minimizeToTray = true;
    private String currentStreamService = "twitch.tv";
    private String currentQuality = "High";
    private String currentStreamName = "";
    private String twitchUser = "";
    private String twitchOAuth = "";
    private String path = "";
    private String chatFont = "Tahoma";
    private final Version VERSION = null;
    private final long timeout = 5000L;
    private int checkTimer = 30;
    private int downloadedBytes = 0;
    private int maxGamesLoad = 20;
    private int maxChannelsLoad = 20;
    private int chatFontSize = 3;

    private PrintStream logPrintStream;

    private final String QUALITY = "quality";
    private final String TIMER = "timer";
    private final String SHOWPREVIEW = "showpreview";
    private final String AUTOUPDATE = "autoupdate";
    private final String TWITCHUSER = "twitchusername";
    private final String TWITCHOAuth = "twitchoauth";
    private final String TWITCHSORT = "twitchsorting";
    private final String PATH = "recordingpath";
    private final String CHANNELSLOAD = "load_max_channels";
    private final String GAMESSLOAD = "load_max_games";
    private final String SERVICENAME = "serviceName";
    private final String SERVICEURL = "serviceURL";
    private final String MINIMIZETOTRAY = "minimizetotray";

    private Settings() {
        File settings;
        try {
            settings = new File(FILEPATH);
            if (settings.exists() && settings.isFile()) {
                loadSettings(settings);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        createSettingsJson(settings);
    }

    public void loadSettings(File file) {
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
            currentQuality = settings.get(QUALITY).getAsString();
            checkTimer = settings.get(TIMER).getAsInt();
            showPreview = settings.get(SHOWPREVIEW).getAsBoolean();
            autoUpdate = settings.get(AUTOUPDATE).getAsBoolean();
            twitchUser = settings.get(TWITCHUSER).getAsString();
            twitchOAuth = settings.get(TWITCHOAuth).getAsString();
            sortTwitch = settings.get(TWITCHSORT).getAsBoolean();
            maxChannelsLoad = settings.get(CHANNELSLOAD).getAsInt();
            maxGamesLoad = settings.get(GAMESSLOAD).getAsInt();
            minimizeToTray = settings.get(MINIMIZETOTRAY).getAsBoolean();

            JsonArray services = jArray.get(1).getAsJsonArray();
            for (int i = 0; i < services.size(); i++) {
                JsonObject service = services.get(i).getAsJsonObject();
                ServiceModel ss = new ServiceModel(service.get(SERVICENAME).getAsString(),
                        service.get(SERVICEURL).getAsString());

                JsonArray channels = service.get("channels").getAsJsonArray();
                if (channels.size() > 0) {
                    for (int e = 0; e < channels.size(); e++) {
                        // TODO include other services
                        ss.addStream(channels.get(e).getAsString());
                    }
                }
                streamServices.add(ss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSettingsJson(File file) {
        JsonWriter w;
        try {
            w = new JsonWriter(new FileWriter(file));
            w.setIndent("  ");
            w.beginArray();
            w.beginObject();
            w.name(QUALITY).value(currentQuality);
            w.name(TIMER).value(checkTimer);
            w.name(SHOWPREVIEW).value(showPreview);
            w.name(AUTOUPDATE).value(autoUpdate);
            w.name(TWITCHUSER).value(twitchUser);
            w.name(TWITCHOAuth).value(twitchOAuth);
            w.name(TWITCHSORT).value(sortTwitch);
            w.name(PATH).value("");
            w.name(CHANNELSLOAD).value(maxChannelsLoad);
            w.name(GAMESSLOAD).value(maxGamesLoad);
            w.name(MINIMIZETOTRAY).value(minimizeToTray);
            w.endObject();
            w.beginArray();
            for (ServiceModel s : streamServices) {
                w.beginObject();
                w.name(SERVICENAME).value(s.getName().get());
                w.name(SERVICEURL).value(s.getUrl().get());
                w.name("channels");
                w.beginArray();
                for (StreamModel channel : s.getChannels()) {
                    w.value(channel.getName().get());
                }
                w.endArray();
                w.endObject();
            }
            w.endArray();

            w.endArray();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ServiceModel> getStreamServices() {
        return streamServices;
    }

    public ObservableList<ServiceModel> getStreamServicesObservable() {
        return FXCollections.observableArrayList(streamServices);
    }

    public boolean isSortTwitch() {
        return sortTwitch;
    }

    public void setSortTwitch(boolean sortTwitch) {
        this.sortTwitch = sortTwitch;
    }

    public String getCurrentStreamService() {
        return currentStreamService;
    }

    public void setCurrentStreamService(String currentStreamService) {
        this.currentStreamService = currentStreamService;
    }

    public String getCurrentQuality() {
        return currentQuality;
    }

    public void setCurrentQuality(String currentQuality) {
        this.currentQuality = currentQuality;
    }

    public String getCurrentStreamName() {
        return currentStreamName;
    }

    public void setCurrentStreamName(String currentStreamName) {
        this.currentStreamName = currentStreamName;
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public int getCheckTimer() {
        return checkTimer;
    }

    public void setCheckTimer(int checkTimer) {
        this.checkTimer = checkTimer;
    }

    public int getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(int downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
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

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public Integer getChatFontSize() {
        return chatFontSize;
    }

    public void setChatFontSize(Integer chatFontSize) {
        this.chatFontSize = chatFontSize;
    }

    public String getChatFont() {
        return chatFont;
    }

    public void setChatFont(String chatFont) {
        this.chatFont = chatFont;
    }

    public Version getVERSION() {
        return VERSION;
    }

    public long getTimeout() {
        return timeout;
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
