package twitchAPI;

import com.google.gson.JsonObject;

public class Twitch_Stream {
    boolean online;
    int broadcast_part;
    boolean featured;
    boolean channel_subscription;
    String id;
    String category;
    String title;
    int channel_count;
    int video_height;
    int site_count;
    boolean embed_enabled;
    String up_time;
    String meta_game;
    String format;
    int embed_count;
    String stream_type;
    boolean abuse_reported;
    int video_width;
    String geo;
    String name;
    String language;
    int stream_count;
    double video_bitrate;
    String broadcaster;
    int channel_view_count;
    String username;
    String status;
    String channel_url;
    boolean producer;
    String subcategory_title;
    String screen_cap_url_large;
    String screen_cap_url_small;
    String screen_cap_url_medium;
    String screen_cap_url_huge;
    String timezone;
    String category_title;
    int views_count;

    public void load(JsonObject job) {
	/*
	 * setBroadcast_part(job.get("broadcast_part").getAsInt());
	 * setFeatured(job.get("featured").getAsBoolean());
	 * setChannel_subscription
	 * (job.get("channel_subscription").getAsBoolean());
	 * setId(job.get("id").getAsString());
	 * setCategory(job.get("category").getAsString());
	 */
	setTitle(job.get("channel").getAsJsonObject().get("status")
		.getAsString());
	/*
	 * setChannel_count(job.get("channel_count").getAsInt());
	 * setVideo_height(job.get("video_height").getAsInt());
	 * setSite_count(job.get("site_count").getAsInt());
	 * setEmbed_enabled(job.get("embed_enabled").getAsBoolean());
	 * setUp_time(job.get("up_time").getAsString());
	 */
	setMeta_game(job.get("game").getAsString());
	/*
	 * setFormat(job.get("format").getAsString());
	 * setEmbed_count(job.get("embed_count").getAsInt());
	 * setStream_type(job.get("stream_type").getAsString());
	 * setAbuse_reported(job.get("abuse_reported").getAsBoolean());
	 * setVideo_width(job.get("video_width").getAsInt());
	 * setGeo(job.get("geo").getAsString());
	 * setName(job.get("name").getAsString());
	 * setLanguage(job.get("language").getAsString());
	 * setStream_count(job.get("stream_count").getAsInt());
	 * setVideo_bitrate(job.get("video_bitrate").getAsDouble());
	 * setBroadcaster(job.get("broadcaster").getAsString());
	 * setChannel_view_count(job.get("channel_view_count").getAsInt());
	 * 
	 * setUsername(job.get("channel").getAsJsonObject().get("login")
	 * .getAsString());
	 * setTitle(job.get("channel").getAsJsonObject().get("status")
	 * .getAsString());
	 * setChannel_url(job.get("channel").getAsJsonObject().get
	 * ("channel_url") .getAsString());
	 * setProducer(job.get("channel").getAsJsonObject().get("producer")
	 * .getAsBoolean());
	 * setSubcategory_title(job.get("channel").getAsJsonObject()
	 * .get("subcategory_title").getAsString());
	 */
	setScreen_cap_url_large(job.get("preview").getAsJsonObject()
		.get("large").getAsString());
	setScreen_cap_url_small(job.get("preview").getAsJsonObject()
		.get("small").getAsString());
	setScreen_cap_url_medium(job.get("preview").getAsJsonObject()
		.get("medium").getAsString());
	// setScreen_cap_url_huge(job.get("preview").getAsJsonObject()
	// .get("huge").getAsString());
	/*
	 * setTimezone(job.get ("channel"). getAsJsonObject ().get("timezone")
	 * .getAsString()); setCategory_title (job .get("channel")
	 * .getAsJsonObject() . get("category_title" ).getAsString());
	 * setViews_count (job.get("channel" ) .getAsJsonObject() .
	 * get("views_count") .getAsInt());
	 */
    }

    public boolean isOnline() {
	return this.online;
    }

    public void setOnline(boolean online) {
	this.online = online;
    }

    public int getBroadcast_part() {
	return this.broadcast_part;
    }

    public void setBroadcast_part(int broadcast_part) {
	this.broadcast_part = broadcast_part;
    }

    public boolean isFeatured() {
	return this.featured;
    }

    public void setFeatured(boolean featured) {
	this.featured = featured;
    }

    public boolean isChannel_subscription() {
	return this.channel_subscription;
    }

    public void setChannel_subscription(boolean channel_subscription) {
	this.channel_subscription = channel_subscription;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
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

    public int getChannel_count() {
	return this.channel_count;
    }

    public void setChannel_count(int channel_count) {
	this.channel_count = channel_count;
    }

    public int getVideo_height() {
	return this.video_height;
    }

    public void setVideo_height(int video_height) {
	this.video_height = video_height;
    }

    public int getSite_count() {
	return this.site_count;
    }

    public void setSite_count(int site_count) {
	this.site_count = site_count;
    }

    public boolean isEmbed_enabled() {
	return this.embed_enabled;
    }

    public void setEmbed_enabled(boolean embed_enabled) {
	this.embed_enabled = embed_enabled;
    }

    public String getUp_time() {
	return this.up_time;
    }

    public void setUp_time(String up_time) {
	this.up_time = up_time;
    }

    public String getMeta_game() {
	return this.meta_game;
    }

    public void setMeta_game(String meta_game) {
	this.meta_game = meta_game;
    }

    public String getFormat() {
	return this.format;
    }

    public void setFormat(String format) {
	this.format = format;
    }

    public int getEmbed_count() {
	return this.embed_count;
    }

    public void setEmbed_count(int embed_count) {
	this.embed_count = embed_count;
    }

    public String getStream_type() {
	return this.stream_type;
    }

    public void setStream_type(String stream_type) {
	this.stream_type = stream_type;
    }

    public boolean isAbuse_reported() {
	return this.abuse_reported;
    }

    public void setAbuse_reported(boolean abuse_reported) {
	this.abuse_reported = abuse_reported;
    }

    public int getVideo_width() {
	return this.video_width;
    }

    public void setVideo_width(int video_width) {
	this.video_width = video_width;
    }

    public String getGeo() {
	return this.geo;
    }

    public void setGeo(String geo) {
	this.geo = geo;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getLanguage() {
	return this.language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public int getStream_count() {
	return this.stream_count;
    }

    public void setStream_count(int stream_count) {
	this.stream_count = stream_count;
    }

    public double getVideo_bitrate() {
	return this.video_bitrate;
    }

    public void setVideo_bitrate(double video_bitrate) {
	this.video_bitrate = video_bitrate;
    }

    public String getBroadcaster() {
	return this.broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
	this.broadcaster = broadcaster;
    }

    public int getChannel_view_count() {
	return this.channel_view_count;
    }

    public void setChannel_view_count(int channel_view_count) {
	this.channel_view_count = channel_view_count;
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

    public String getChannel_url() {
	return this.channel_url;
    }

    public void setChannel_url(String channel_url) {
	this.channel_url = channel_url;
    }

    public boolean isProducer() {
	return this.producer;
    }

    public void setProducer(boolean producer) {
	this.producer = producer;
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

    public String getScreen_cap_url_small() {
	return this.screen_cap_url_small;
    }

    public void setScreen_cap_url_small(String screen_cap_url_small) {
	this.screen_cap_url_small = screen_cap_url_small;
    }

    public String getScreen_cap_url_medium() {
	return this.screen_cap_url_medium;
    }

    public void setScreen_cap_url_medium(String screen_cap_url_medium) {
	this.screen_cap_url_medium = screen_cap_url_medium;
    }

    public String getScreen_cap_url_huge() {
	return this.screen_cap_url_huge;
    }

    public void setScreen_cap_url_huge(String screen_cap_url_huge) {
	this.screen_cap_url_huge = screen_cap_url_huge;
    }

    public String getTimezone() {
	return this.timezone;
    }

    public void setTimezone(String timezone) {
	this.timezone = timezone;
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
}
