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
package app.lsgui.model.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IChannel;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.TwitchUtils;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.util.Callback;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class TwitchChannel implements IChannel, ITwitchItem {

    private static final String CHANNEL_IS_OFFLINE = "Channel is offline";
    private static final String NO_QUALITIES = "Error fetching Quality Options!";
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannel.class);

    private StringProperty name = new SimpleStringProperty();
    private StringProperty logoURL = new SimpleStringProperty();
    private StringProperty previewUrlLarge = new SimpleStringProperty();
    private StringProperty previewUrlMedium = new SimpleStringProperty();
    private StringProperty game = new SimpleStringProperty();
    private StringProperty title = new SimpleStringProperty();
    private LongProperty uptime = new SimpleLongProperty();
    private StringProperty uptimeString = new SimpleStringProperty();
    private IntegerProperty viewers = new SimpleIntegerProperty();
    private StringProperty viewersString = new SimpleStringProperty();
    private BooleanProperty isOnline = new SimpleBooleanProperty();
    private BooleanProperty isPlaylist = new SimpleBooleanProperty();
    private ObjectProperty<Image> previewImageLarge = new SimpleObjectProperty<>();
    private ObjectProperty<Image> previewImageMedium = new SimpleObjectProperty<>();
    private ListProperty<String> availableQualities = new SimpleListProperty<>(FXCollections.observableArrayList());
    private BooleanProperty hasReminder = new SimpleBooleanProperty();

    private boolean isBrowser;
    private boolean cameOnline;

    public TwitchChannel() {
        // Empty Constructor
    }

    public void updateData(final TwitchChannel data, final boolean notify) {
        if (data != null && data.isOnline().get()) {
            this.setOnline(data);
        } else if (data != null && !data.isOnline().get()) {
            this.setOffline(data.getName().get());
        }
        this.displayNotification(notify);
    }

    private void displayNotification(final boolean notify) {
        if (this.cameOnline && notify && !this.hasReminder.get()) {
            TwitchUtils.showOnlineNotification(this);
            this.cameOnline = false;
        } else if (this.cameOnline && notify && this.hasReminder.get()) {
            TwitchUtils.showReminderNotification(this);
            this.cameOnline = false;
        }
    }

    private void setOffline(final String name) {
        this.name.set(name);
        this.logoURL.set("");
        this.previewUrlLarge.set("");
        this.game.set("");
        this.title.set(CHANNEL_IS_OFFLINE);
        this.uptime.set(0);
        this.viewers.set(0);
        this.isOnline.set(false);
        this.isPlaylist.set(false);
        this.previewImageLarge.setValue(TwitchUtils.DEFAULT_LOGO);
        this.availableQualities.clear();
    }

    private void setOnline(final TwitchChannel data) {
        LOGGER.trace("update {} with data {}", data.getName(), data.isOnline());
        this.name.setValue(data.getName().get());
        this.logoURL.setValue(data.getLogoURL().get());
        this.previewUrlLarge.setValue(data.getPreviewUrlLarge().get());
        this.previewUrlMedium.setValue(data.getPreviewUrlMedium().get());
        this.game.setValue(data.getGame().get());
        this.title.setValue(data.getTitle().get());
        this.uptime.setValue(data.getUptime().get());
        this.uptimeString.setValue(TwitchUtils.buildUptimeString(this.uptime.get()));
        this.viewers.setValue(data.getViewers().get());
        this.viewersString.setValue(Integer.toString(this.getViewers().get()));
        if (data.isOnline().get() && !this.isOnline.get()) {
            this.isOnline.set(true);
            this.cameOnline = true;
        } else if (!data.isOnline().get()) {
            this.isOnline.set(false);
        }
        this.isPlaylist.setValue(data.getIsPlaylist().get());
        this.previewImageLarge.setValue(data.getPreviewImageLarge().get());
        this.previewImageMedium.setValue(data.getPreviewImageMedium().get());
        this.availableQualities.clear();
        this.availableQualities.addAll(data.getAvailableQualities());
    }

    public static Callback<IChannel, Observable[]> extractor() {
        return (IChannel sm) -> new Observable[] { ((TwitchChannel) sm).getName(), ((TwitchChannel) sm).getGame(),
                ((TwitchChannel) sm).isOnline(), ((TwitchChannel) sm).getTitle(), ((TwitchChannel) sm).getLogoURL(),
                ((TwitchChannel) sm).getPreviewImageLarge(), ((TwitchChannel) sm).getPreviewUrlLarge(),
                ((TwitchChannel) sm).getPreviewUrlMedium(), ((TwitchChannel) sm).getUptime(),
                ((TwitchChannel) sm).getViewers(), };
    }

    @Override
    public StringProperty getName() {
        return this.name;
    }

    public StringProperty getLogoURL() {
        return this.logoURL;
    }

    public StringProperty getPreviewUrlLarge() {
        return this.previewUrlLarge;
    }

    public StringProperty getPreviewUrlMedium() {
        return this.previewUrlMedium;
    }

    public StringProperty getGame() {
        return this.game;
    }

    public StringProperty getTitle() {
        return this.title;
    }

    public LongProperty getUptime() {
        return this.uptime;
    }

    public IntegerProperty getViewers() {
        return this.viewers;
    }

    @Override
    public BooleanProperty isOnline() {
        return this.isOnline;
    }

    public ObjectProperty<Image> getPreviewImageLarge() {
        return this.previewImageLarge;
    }

    public ObjectProperty<Image> getPreviewImageMedium() {
        return this.previewImageMedium;
    }

    @Override
    public ListProperty<String> getAvailableQualities() {
        if (this.availableQualities.isEmpty()) {
            if (!this.isOnline.get()) {
                this.availableQualities.add(CHANNEL_IS_OFFLINE);
            } else if (!this.isBrowser) {
                this.availableQualities.add(NO_QUALITIES);
                LsGuiUtils.showWarningNotification(NO_QUALITIES, "Check your Twitch OAuth Key in the Settings!");
            }
        }
        return this.availableQualities;
    }

    public StringProperty getUptimeString() {
        return this.uptimeString;
    }

    public StringProperty getViewersString() {
        return this.viewersString;
    }

    public BooleanProperty getIsPlaylist() {
        return this.isPlaylist;
    }

    @Override
    public BooleanProperty hasReminder() {
        return this.hasReminder;
    }

    @Override
    public void setReminder(final boolean hasReminder) {
        this.hasReminder.set(hasReminder);
    }

    public BooleanProperty getIsOnline() {
        return this.isOnline;
    }

    public boolean isBrowser() {
        return this.isBrowser;
    }

    public void setBrowser(final boolean isBrowser) {
        this.isBrowser = isBrowser;
    }
}
