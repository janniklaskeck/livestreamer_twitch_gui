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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

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
        this.createSettingsJson(settings);
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

    private void createSettingsJson(final File file) {
        try (final FileOutputStream outputStream = new FileOutputStream(file);) {
            final BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            final JsonWriter jsonWriter = new JsonWriter(bufferedWriter);
            jsonWriter.setIndent("  ");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            jsonWriter.name(TWITCH_USER_STRING).value(this.twitchUserProperty().get());
            jsonWriter.name(TWITCH_OAUTH_STRING).value(this.twitchOAuthProperty().get());
            jsonWriter.name(TWITCH_SORT).value(this.sortTwitchProperty().get());
            jsonWriter.name(QUALITY_STRING).value(this.qualityProperty().get());
            jsonWriter.name(PATH).value(this.recordingPathProperty().get());
            jsonWriter.name(CHANNELS_LOAD).value(this.maxChannelsProperty().get());
            jsonWriter.name(GAMES_LOAD).value(this.maxGamesProperty().get());
            jsonWriter.name(MINIMIZE_TO_TRAY_STRING).value(this.minimizeToTrayProperty().get());
            jsonWriter.name(WINDOWSTYLE_STRING).value(this.windowStyleProperty().get());
            jsonWriter.name(EXEPATH_STRING).value(this.livestreamerPathProperty().get());
            this.writeFavouriteGames(jsonWriter);
            jsonWriter.endObject();
            this.writeServices(jsonWriter);
            jsonWriter.endArray();
            jsonWriter.close();
            bufferedWriter.close();
        } catch (IOException e) {
            LOGGER.error("ERROR while writing to Settings file", e);
        }
    }

    private void writeServices(final JsonWriter writer) throws IOException {
        writer.beginArray();
        for (final IService service : this.services) {
            LOGGER.debug("Creating JSON for Service {}", service.getName().get());
            writer.beginObject();
            writer.name(SERVICE_NAME).value(service.getName().get());
            writer.name(SERVICE_URL).value(service.getUrl().get());
            writer.name("channels");
            writer.beginArray();
            for (final IChannel channel : service.getChannelProperty().get()) {
                if (channel.getName().get() != null) {
                    writer.value(channel.getName().get());
                }
            }
            writer.endArray();
            writer.endObject();
        }
        writer.endArray();
    }

    private void writeFavouriteGames(final JsonWriter writer) throws IOException {
        LOGGER.debug("Writing Favourites to Settings file");
        writer.name(FAVOURITE_GAMES);
        writer.beginArray();
        for (final String favourite : this.favouriteGames) {
            LOGGER.trace("Writing Favourite '{}' to file", favourite);
            writer.value(favourite);
        }
        writer.endArray();
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
