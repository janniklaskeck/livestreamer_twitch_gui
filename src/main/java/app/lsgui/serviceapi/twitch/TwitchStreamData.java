package app.lsgui.serviceapi.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class TwitchStreamData {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchStreamData.class);

	private boolean online = false;
	private String name = "";
	private String title = "";
	private String viewers = "";
	private String createdAt = "";
	private String updatedAt = "";
	private String previewURL = "";
	private String logoURL = "";
	private String game = "";
	private long uptime = 0L;

	public TwitchStreamData(JsonObject streamObject) {
		LOGGER.debug("Load stream Json");

		calculateAndSetUptime();
	}

	private void calculateAndSetUptime() {
		setUptime(0L);
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getViewers() {
		return viewers;
	}

	public void setViewers(String viewers) {
		this.viewers = viewers;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getPreviewURL() {
		return previewURL;
	}

	public void setPreviewURL(String previewURL) {
		this.previewURL = previewURL;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

}
