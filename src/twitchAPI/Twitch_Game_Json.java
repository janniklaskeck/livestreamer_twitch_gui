package twitchAPI;

import com.google.gson.JsonObject;

public class Twitch_Game_Json {

    private String name;
    private String channel;
    private int viewers;
    private String preview_image_small;
    private String preview_image_medium;

    public void load(JsonObject job) {
	setName(job.get("channel").getAsJsonObject().get("display_name")
		.getAsString());
	setChannel(job.get("channel").getAsJsonObject().get("name")
		.getAsString());
	setViewers(job.get("viewers").getAsInt());
	setPreview_image_medium(job.get("preview").getAsJsonObject()
		.get("medium").getAsString());
	setPreview_image_small(job.get("preview").getAsJsonObject()
		.get("small").getAsString());
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
     * @return the preview_image_small
     */
    public String getPreview_image_small() {
	return preview_image_small;
    }

    /**
     * @param preview_image_small
     *            the preview_image_small to set
     */
    public void setPreview_image_small(String preview_image_small) {
	this.preview_image_small = preview_image_small;
    }

    /**
     * @return the preview_image_medium
     */
    public String getPreview_image_medium() {
	return preview_image_medium;
    }

    /**
     * @param preview_image_medium
     *            the preview_image_medium to set
     */
    public void setPreview_image_medium(String preview_image_medium) {
	this.preview_image_medium = preview_image_medium;
    }

    /**
     * @return the channel
     */
    public String getChannel() {
	return channel;
    }

    /**
     * @param channel
     *            the channel to set
     */
    public void setChannel(String channel) {
	this.channel = channel;
    }
}
