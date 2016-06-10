package app.lsgui.model.channel.twitch;

import com.google.gson.JsonObject;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class TwitchGame {

    private String name;
    private String logoURL;
    private int viewers;
    private int channels;

    /**
     *
     * @param jo
     */
    public TwitchGame(JsonObject jo) {

    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the logoURL
     */
    public String getLogoURL() {
	return logoURL;
    }

    /**
     * @param logoURL
     *            the logoURL to set
     */
    public void setLogoURL(String logoURL) {
	this.logoURL = logoURL;
    }

    /**
     * @return the viewers
     */
    public int getViewers() {
	return viewers;
    }

    /**
     * @param viewers
     *            the viewers to set
     */
    public void setViewers(int viewers) {
	this.viewers = viewers;
    }

    /**
     * @return the channels
     */
    public int getChannels() {
	return channels;
    }

    /**
     * @param channels
     *            the channels to set
     */
    public void setChannels(int channels) {
	this.channels = channels;
    }

}
