package twitchAPI;

import com.google.gson.JsonObject;

/**
 * Sets Variables from Twitch.tv JsonObject
 * 
 * @author Niklas 27.09.2014
 * 
 */
public class Twitch_List_Json {
    private int viewers;
    private int channel_count;
    private String name;
    private String box_image_small;
    private String logo_image_small;
    private String box_image_medium;
    private String logo_image_medium;

    public void load(JsonObject job) {
	setViewers(job.get("viewers").getAsInt());
	setChannel_count(job.get("channels").getAsInt());
	setName(job.get("game").getAsJsonObject().get("name").getAsString());
	setBox_image_small(job.get("game").getAsJsonObject().get("box")
		.getAsJsonObject().get("small").getAsString());
	setLogo_image_small(job.get("game").getAsJsonObject().get("logo")
		.getAsJsonObject().get("small").getAsString());
	setBox_image_medium(job.get("game").getAsJsonObject().get("box")
		.getAsJsonObject().get("medium").getAsString());
	setLogo_image_medium(job.get("game").getAsJsonObject().get("logo")
		.getAsJsonObject().get("medium").getAsString());

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
     * @return the channel_count
     */
    public int getChannel_count() {
	return channel_count;
    }

    /**
     * @param channel_count
     *            the channel_count to set
     */
    public void setChannel_count(int channel_count) {
	this.channel_count = channel_count;
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
     * @return the box_image_medium
     */
    public String getBox_image_small() {
	return box_image_small;
    }

    /**
     * @param box_image_medium
     *            the box_image_medium to set
     */
    public void setBox_image_small(String box_image_small) {
	this.box_image_small = box_image_small;
    }

    /**
     * @return the logo_image_medium
     */
    public String getLogo_image_small() {
	return logo_image_small;
    }

    /**
     * @param logo_image_medium
     *            the logo_image_medium to set
     */
    public void setLogo_image_small(String logo_image_small) {
	this.logo_image_small = logo_image_small;
    }

    /**
     * @return the box_image_medium
     */
    public String getBox_image_medium() {
	return box_image_medium;
    }

    /**
     * @param box_image_medium
     *            the box_image_medium to set
     */
    public void setBox_image_medium(String box_image_medium) {
	this.box_image_medium = box_image_medium;
    }

    /**
     * @return the logo_image_medium
     */
    public String getLogo_image_medium() {
	return logo_image_medium;
    }

    /**
     * @param logo_image_medium
     *            the logo_image_medium to set
     */
    public void setLogo_image_medium(String logo_image_medium) {
	this.logo_image_medium = logo_image_medium;
    }
}
