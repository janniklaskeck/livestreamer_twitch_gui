package app.lsgui.model.twitch;

import com.google.gson.JsonObject;

public class TwitchGameModel {

    private String name;
    private String logoURL;
    private int viewers;
    private int channels;

    public TwitchGameModel(JsonObject jo) {

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
