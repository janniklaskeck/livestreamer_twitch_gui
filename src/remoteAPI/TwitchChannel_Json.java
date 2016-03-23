package remoteAPI;

import com.google.gson.JsonObject;

import util.JsonUtil;

/**
 * Sets Variables from Twitch.tv JsonObject
 *
 * @author Niklas 27.09.2014
 *
 */
public class TwitchChannel_Json {
    private boolean online = false;
    private String category = "";
    private String title = "";
    private String created_at = "";
    private String updated_at = "";
    private String meta_game = "";
    private String name = "";
    private String username = "";
    private String status = "";
    private String subcategory_title = "";
    private String screen_cap_url_large = "";
    private String screen_cap_url_medium = "";
    private String category_title = "";
    private int current_viewers = 0;
    private int views_count = 0;

    public TwitchChannel_Json() {
    }

    public TwitchChannel_Json(final JsonObject streamData) {
        JsonObject channelData = streamData.get("channel").getAsJsonObject();
        JsonObject previewData = streamData.get("preview").getAsJsonObject();
        setTitle(JsonUtil.getStringIfNotNull("status", channelData));
        setMeta_game(JsonUtil.getStringIfNotNull("game", streamData));
        setCurrent_viewers(JsonUtil.getIntegerIfNotNull("viewers", streamData));
        setScreen_cap_url_large(JsonUtil.getStringIfNotNull("large", previewData));
        setScreen_cap_url_medium(JsonUtil.getStringIfNotNull("medium", previewData));
        setCreated_At(JsonUtil.getStringIfNotNull("created_at", streamData));
        setUpdated_At(JsonUtil.getStringIfNotNull("updated_at", channelData));
        setOnline(true);
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_At() {
        return this.created_at;
    }

    public void setCreated_At(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_At() {
        return this.updated_at;
    }

    public void setUpdated_At(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getMeta_game() {
        return this.meta_game;
    }

    public void setMeta_game(String meta_game) {
        this.meta_game = meta_game;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubcategory_title() {
        return this.subcategory_title;
    }

    public void setSubcategory_title(String subcategory_title) {
        this.subcategory_title = subcategory_title;
    }

    public String getScreen_cap_url_large() {
        return this.screen_cap_url_large;
    }

    public void setScreen_cap_url_large(String screen_cap_url_large) {
        this.screen_cap_url_large = screen_cap_url_large;
    }

    public String getScreen_cap_url_medium() {
        return this.screen_cap_url_medium;
    }

    public void setScreen_cap_url_medium(String screen_cap_url_medium) {
        this.screen_cap_url_medium = screen_cap_url_medium;
    }

    public String getCategory_title() {
        return this.category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public int getViews_count() {
        return this.views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    /**
     * @return the current_viewers
     */
    public int getCurrent_viewers() {
        return current_viewers;
    }

    /**
     * @param current_viewers
     *            the current_viewers to set
     */
    public void setCurrent_viewers(int current_viewers) {
        this.current_viewers = current_viewers;
    }
}
