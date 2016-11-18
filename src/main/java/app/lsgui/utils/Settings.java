/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.generic.GenericService;
import app.lsgui.model.twitch.TwitchService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static final String FILEPATH = System.getProperty("user.home") + "/.lsgui/settings.json";
    private static final long TIMEOUT = 5000L;
    private static final int DEFAULT_GAMES_TO_LOAD = 20;
    private static final int DEFAULT_CHANNELS_TO_LOAD = 20;
    private static final String TWITCH_USER_STRING = "twitchusername";
    private static final String TWITCH_OAUTH_STRING = "twitchoauth";
    private static final String TWITCH_SORT = "twitchsorting";
    private static final String PATH = "recordingpath";
    private static final String CHANNELS_LOAD = "load_max_channels";
    private static final String GAMES_LOAD = "load_max_games";
    private static final String SERVICE_NAME = "serviceName";
    private static final String SERVICE_URL = "serviceURL";
    private static final String MINIMIZE_TO_TRAY_STRING = "minimizetotray";
    private static final String WINDOWSTYLE_STRING = "windowstyle";
    private static final String EXEPATH_STRING = "livestreamerexe";
    private static final String QUALITY_STRING = "quality";
    private static final String FAVOURITE_GAMES = "favouriteGames";
    private static final String DEFAULT_TOKEN = "vkwhrtlhzcz3o91nu386ub62p5j6sk";

    public static final String DEFAULT_QUALITY = "Source";

    private static Settings instance;

    private ListProperty<IService> services = new SimpleListProperty<>();
    private ListProperty<String> favouriteGames = new SimpleListProperty<>();
    private BooleanProperty sortTwitch = new SimpleBooleanProperty();
    private BooleanProperty minimizeToTray = new SimpleBooleanProperty();
    private StringProperty windowStyle = new SimpleStringProperty("LightStyle");
    private StringProperty currentService = new SimpleStringProperty("twitch.tv");
    private StringProperty twitchUser = new SimpleStringProperty();
    private StringProperty twitchOAuth = new SimpleStringProperty();
    private IntegerProperty maxGamesLoad = new SimpleIntegerProperty();
    private IntegerProperty maxChannelsLoad = new SimpleIntegerProperty();
    private StringProperty liveStreamerExePath = new SimpleStringProperty();
    private StringProperty quality = new SimpleStringProperty(DEFAULT_QUALITY);
    private StringProperty recordingPath = new SimpleStringProperty();
    private StringProperty updateLink = new SimpleStringProperty();

    private boolean isLoading;

    private Settings() {
    }

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
            final File settings = new File(FILEPATH);
            if (!instance.isLoading && settings.exists() && settings.isFile() && !LsGuiUtils.isFileEmpty(settings)) {
                LOGGER.info("Loading Settings from File");
                instance.loadSettingsFromFile(settings);
            } else {
                LOGGER.info("Settings file does not exists. Creating default File.");
                instance.saveSettings();
            }
        }
        return instance;
    }

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
        final JsonElement data = this.createSettingsJson();
        JsonUtils.writeJsonToFile(settings, data);
    }

    private void loadSettingsFromFile(final File file) {
        this.isLoading = true;
        final JsonArray jsonArray = JsonUtils.getJsonArrayFromFile(file);
        this.loadSettings(jsonArray);
        this.loadServices(jsonArray);
    }

    private void loadSettings(final JsonArray jArray) {
        final JsonObject settings = jArray.get(0).getAsJsonObject();
        this.sortTwitchProperty().setValue(JsonUtils.getBooleanSafe(settings.get(TWITCH_SORT), false));
        this.minimizeToTrayProperty().setValue(JsonUtils.getBooleanSafe(settings.get(MINIMIZE_TO_TRAY_STRING), false));
        this.twitchUserProperty().setValue(JsonUtils.getStringSafe(settings.get(TWITCH_USER_STRING), ""));
        this.twitchOAuthProperty().setValue(JsonUtils.getStringSafe(settings.get(TWITCH_OAUTH_STRING), DEFAULT_TOKEN));
        this.windowStyleProperty().setValue(JsonUtils.getStringSafe(settings.get(WINDOWSTYLE_STRING), "LightStyle"));
        this.livestreamerPathProperty().setValue(JsonUtils.getStringSafe(settings.get(EXEPATH_STRING), ""));
        this.maxChannelsProperty()
                .setValue(JsonUtils.getIntSafe(settings.get(CHANNELS_LOAD), DEFAULT_CHANNELS_TO_LOAD));
        this.maxGamesProperty().setValue(JsonUtils.getIntSafe(settings.get(GAMES_LOAD), DEFAULT_GAMES_TO_LOAD));
        this.qualityProperty().setValue(JsonUtils.getStringSafe(settings.get(QUALITY_STRING), DEFAULT_QUALITY));
        this.recordingPathProperty()
                .setValue(JsonUtils.getStringSafe(settings.get(PATH), System.getProperty("user.home")));
        final JsonArray favouritesArray = JsonUtils.getJsonArraySafe(FAVOURITE_GAMES, settings);
        for (int i = 0; i < favouritesArray.size(); i++) {
            final String favourite = favouritesArray.get(i).getAsString();
            this.addFavouriteGame(favourite);
        }
    }

    private void loadServices(final JsonArray jArray) {
        this.services.setValue(FXCollections.observableArrayList());
        final JsonArray servicesArray = jArray.get(1).getAsJsonArray();
        for (int i = 0; i < servicesArray.size(); i++) {
            final JsonObject serviceJson = servicesArray.get(i).getAsJsonObject();
            final String serviceName = serviceJson.get(SERVICE_NAME).getAsString();
            final String serviceUrl = serviceJson.get(SERVICE_URL).getAsString();
            final IService service;
            if (serviceUrl.toLowerCase(Locale.ENGLISH).contains("twitch")) {
                service = new TwitchService(serviceName, serviceUrl);
            } else {
                service = new GenericService(serviceName, serviceUrl);
            }

            final JsonArray channels = serviceJson.get("channels").getAsJsonArray();
            for (int e = 0; e < channels.size(); e++) {
                final String channel = channels.get(e).getAsString();
                service.addChannel(channel);
            }
            this.services.get().add(service);
        }
    }

    private JsonElement createSettingsJson() {
        final JsonObject generalSettings = new JsonObject();
        generalSettings.addProperty(TWITCH_USER_STRING, this.twitchUserProperty().get());
        generalSettings.addProperty(TWITCH_OAUTH_STRING, this.twitchOAuthProperty().get());
        generalSettings.addProperty(TWITCH_SORT, this.sortTwitchProperty().get());
        generalSettings.addProperty(QUALITY_STRING, this.qualityProperty().get());
        generalSettings.addProperty(PATH, this.recordingPathProperty().get());
        generalSettings.addProperty(CHANNELS_LOAD, this.maxChannelsProperty().get());
        generalSettings.addProperty(GAMES_LOAD, this.maxGamesProperty().get());
        generalSettings.addProperty(MINIMIZE_TO_TRAY_STRING, this.minimizeToTrayProperty().get());
        generalSettings.addProperty(WINDOWSTYLE_STRING, this.windowStyleProperty().get());
        generalSettings.addProperty(EXEPATH_STRING, this.livestreamerPathProperty().get());
        final JsonArray favouriteGamesArray = new JsonArray();
        for (final String favourite : this.favouriteGames.get()) {
            favouriteGamesArray.add(favourite);
        }
        generalSettings.add(FAVOURITE_GAMES, favouriteGamesArray);
        final JsonArray servicesArray = new JsonArray();
        for (final IService service : this.services.get()) {
            final JsonObject serviceObject = new JsonObject();
            serviceObject.addProperty(SERVICE_NAME, service.getName().get());
            serviceObject.addProperty(SERVICE_URL, service.getUrl().get());
            final JsonArray channelArray = new JsonArray();
            for (final IChannel channel : service.getChannelProperty().get()) {
                if (channel.getName().get() != null) {
                    channelArray.add(channel.getName().get());
                }
            }
            serviceObject.add("channels", channelArray);
            servicesArray.add(serviceObject);
        }
        final JsonArray settingsArray = new JsonArray();
        settingsArray.add(generalSettings);
        settingsArray.add(servicesArray);
        return settingsArray;
    }

    public IService getTwitchService() {
        final List<IService> servicesAsList = this.servicesProperty().get();
        final Optional<IService> serviceOptional = servicesAsList.stream().filter(TwitchUtils::isTwitchService)
                .findFirst();
        if (serviceOptional.isPresent()) {
            return serviceOptional.get();
        }
        return null;
    }

    public void addFavouriteGame(final String game) {
        final ObservableList<String> favourites = FXCollections.observableArrayList(this.favouriteGames);
        favourites.add(game);
        this.favouriteGamesProperty().set(favourites);
    }

    public void removeFavouriteGame(final String game) {
        final ObservableList<String> favourites = FXCollections.observableArrayList(this.favouriteGames);
        favourites.remove(game);
        this.favouriteGamesProperty().set(favourites);
    }

    public String getCurrentStyleSheet() {
        return Settings.class.getResource("/styles/" + Settings.getInstance().windowStyleProperty().get() + ".css")
                .toExternalForm();
    }

    public long getTimeout() {
        return TIMEOUT;
    }

    public ListProperty<IService> servicesProperty() {
        return this.services;
    }

    public BooleanProperty sortTwitchProperty() {
        return this.sortTwitch;
    }

    public StringProperty currentServiceProperty() {
        return this.currentService;
    }

    public StringProperty twitchUserProperty() {
        return this.twitchUser;
    }

    public StringProperty twitchOAuthProperty() {
        return this.twitchOAuth;
    }

    public IntegerProperty maxGamesProperty() {
        return this.maxGamesLoad;
    }

    public IntegerProperty maxChannelsProperty() {
        return this.maxChannelsLoad;
    }

    public BooleanProperty minimizeToTrayProperty() {
        return this.minimizeToTray;
    }

    public StringProperty windowStyleProperty() {
        return this.windowStyle;
    }

    public StringProperty livestreamerPathProperty() {
        return this.liveStreamerExePath;
    }

    public StringProperty qualityProperty() {
        return this.quality;
    }

    public StringProperty recordingPathProperty() {
        return this.recordingPath;
    }

    public StringProperty updateLinkProperty() {
        return this.updateLink;
    }

    public ListProperty<String> favouriteGamesProperty() {
        return this.favouriteGames;
    }

}
