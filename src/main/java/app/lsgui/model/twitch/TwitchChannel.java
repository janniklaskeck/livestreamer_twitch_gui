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

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannel.class);

	private LongProperty id = new SimpleLongProperty();
	private StringProperty name = new SimpleStringProperty();
	private StringProperty displayName = new SimpleStringProperty();
	private StringProperty logoURL = new SimpleStringProperty();
	private StringProperty previewUrl = new SimpleStringProperty();
	private StringProperty gameId = new SimpleStringProperty();
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
	private BooleanProperty isPartnered = new SimpleBooleanProperty();

	private boolean isBrowser;
	private boolean cameOnline;

	public TwitchChannel() {
		// Empty Constructor
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
		LOGGER.trace("set {} offline", name);
		TwitchUtils.setOfflineData(this, name);
	}

	private void setOnline(final TwitchChannel data) {
		LOGGER.debug("Set {} to online {}", data.getName().get(), data.isOnline().get());
		this.id.setValue(data.getId().get());
		this.name.setValue(data.getName().get());
		this.displayName.setValue(data.displayNameProperty().get());
		this.logoURL.setValue(data.getLogoURL().get());
		this.previewUrl.setValue(data.getPreviewUrl().get());
		this.game.setValue(data.getGame().get());
		this.title.setValue(data.getTitle().get());
		this.uptime.setValue(data.getUptime().get());
		this.uptimeString.setValue(TwitchUtils.buildUptimeString(this.uptime.get()));
		this.viewers.setValue(data.getViewers().get());
		this.viewersString.setValue(Integer.toString(this.getViewers().get()));
		if (data.isOnline().get() && !this.isOnline.get()) {
			this.isOnline.setValue(Boolean.TRUE);
			this.cameOnline = true;
		} else if (!data.isOnline().get()) {
			this.isOnline.setValue(Boolean.FALSE);
		}
		this.isPlaylist.setValue(data.getIsPlaylist().get());
		this.previewImageLarge.setValue(data.getPreviewImageLarge().get());
		this.previewImageMedium.setValue(data.getPreviewImageMedium().get());
		this.availableQualities.clear();
		this.availableQualities.addAll(data.getAvailableQualities());

	}

	public static Callback<IChannel, Observable[]> extractor() {
		return (IChannel sm) -> new Observable[] { ((TwitchChannel) sm).getName(),
				((TwitchChannel) sm).displayNameProperty(), ((TwitchChannel) sm).getGame(),
				((TwitchChannel) sm).isOnline(), ((TwitchChannel) sm).getTitle(), ((TwitchChannel) sm).getLogoURL(),
				((TwitchChannel) sm).getPreviewImageLarge(), ((TwitchChannel) sm).getPreviewUrl(),
				((TwitchChannel) sm).getUptime(), ((TwitchChannel) sm).getViewers(), };
	}

	public StringProperty displayNameProperty() {
		return this.displayName;
	}

	@Override
	public StringProperty getName() {
		return this.name;
	}

	public StringProperty getLogoURL() {
		return this.logoURL;
	}

	public StringProperty getPreviewUrl() {
		return this.previewUrl;
	}

	public StringProperty getGameId() {
		return this.gameId;
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

	public BooleanProperty isPartneredProperty() {
		return this.isPartnered;
	}

	public boolean isBrowser() {
		return this.isBrowser;
	}

	public void setBrowser(final boolean isBrowser) {
		this.isBrowser = isBrowser;
	}

	@Override
	public boolean isTwitchGame() {
		return false;
	}

	@Override
	public boolean isTwitchChannel() {
		return true;
	}

	@Override
	public StringProperty getDisplayName() {
		return this.displayName;
	}

	public LongProperty getId() {
		return this.id;
	}
}
