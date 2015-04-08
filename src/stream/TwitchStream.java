package stream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import twitchAPI.Twitch_Json;
import twitchlsgui.Main_GUI;

public class TwitchStream implements GenericStreamInterface {

    private String channel;
    private String game;
    private String title;
    private int current_viewers;
    private BufferedImage preview;
    private String created_at;
    private String updated_at;
    private String onlineString;
    private long created_at_Long;
    private long updated_at_Long;
    private long upTimeHour;
    private long upTimeMinute;
    private boolean online = false;
    private Twitch_Json ts;
    private Main_GUI parent;

    private static ByteArrayOutputStream bos = new ByteArrayOutputStream();

    public TwitchStream(String channel, Main_GUI parent) {
	this.setChannel(channel);
	this.parent = parent;
    }

    /**
     * Refreshes all Information for the stream and downloads a preview image if
     * the stream is online
     */
    public void refresh() {
	ts = parent.globals.twitchAPI.getStream(this.getChannel());
	if (ts != null) {
	    game = ts.getMeta_game();
	    title = ts.getTitle();
	    online = ts.isOnline();
	    current_viewers = ts.getCurrent_viewers();
	    created_at = ts.getCreated_At();
	    updated_at = ts.getUpdated_At();
	    upTimeHour = 0L;
	    upTimeMinute = 0L;

	    if (online) {
		created_at_Long = convertDate(created_at);
		calcUpTime(created_at_Long);
		setOnlineString("<html>Currently playing: " + getGame()
			+ "<br>(Online for: " + getUpTimeHours() + ":"
			+ getUpTimeMinutes() + " hours) |"
			+ " Current Viewers: " + getCurrent_viewers() + "<br>"
			+ getTitle() + "</html>");
	    }
	    preview = null;
	    if (ts.getScreen_cap_url_medium() != null
		    && parent.globals.showPreview) {
		for (int i = 0; i < 5; i++) {
		    try {
			preview = ImageIO.read(new URL(ts
				.getScreen_cap_url_medium()));
			@SuppressWarnings("unused")
			boolean rw = ImageIO.write(preview, "PNG", bos);
			parent.globals.downloadedBytes += bos.toByteArray().length;
		    } catch (IOException e) {
			if (parent.globals._DEBUG)
			    e.printStackTrace();
		    }
		    if (preview != null) {
			break;
		    }
		}
	    }
	}
    }

    /**
     * @return the game
     */
    public String getGame() {
	return game;
    }

    /**
     * @param game
     *            the game to set
     */
    public void setGame(String game) {
	this.game = game;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * @return the preview
     */
    public BufferedImage getPreview() {
	return preview;
    }

    /**
     * @param preview
     *            the preview to set
     */
    public void setPreview(BufferedImage preview) {
	this.preview = preview;
    }

    /**
     * @return the online
     */
    public boolean isOnline() {
	return online;
    }

    /**
     * @param online
     *            the online to set
     */
    public void setOnline(boolean online) {
	this.online = online;
    }

    /**
     * Converts Twitch date to long
     * 
     * @param date
     * @return long value of date
     */
    private long convertDate(String date) {
	DateFormat fm = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	String dateS = date;
	String[] dateArray = dateS.split("T");
	dateS = "";
	for (String s : dateArray) {
	    if (dateS != "") {
		dateS += " " + s;
	    } else {
		dateS += s;
	    }
	}
	dateArray = dateS.split("Z");
	dateS = "";
	for (String s : dateArray) {
	    dateS += s;
	}
	Date d = null;
	try {
	    d = fm.parse(dateS);
	} catch (ParseException e) {
	    if (parent.globals._DEBUG)
		e.printStackTrace();
	}
	return d.getTime();
    }

    /**
     * Calculates the time from created_at to now and sets the hours and minutes
     * 
     * @param created_at
     */
    private void calcUpTime(Long created_at) {
	upTimeHour = (((System.currentTimeMillis() - created_at) / (1000 * 60 * 60)) % 24) - 1;
	upTimeMinute = ((System.currentTimeMillis() - created_at) / (1000 * 60)) % 60;
    }

    /**
     * 
     * @return amount of hours from created_at to now
     */
    public long getUpTimeHours() {
	return upTimeHour;
    }

    /**
     * 
     * @return String with a 0 in front if value lower than 10
     */
    public String getUpTimeMinutes() {
	if (upTimeMinute < 10) {
	    return 0 + "" + upTimeMinute;
	} else {
	    return "" + upTimeMinute;
	}
    }

    /**
     * @return the onlineString
     */
    public String getOnlineString() {
	return onlineString;
    }

    /**
     * @param onlineString
     *            the onlineString to set
     */
    public void setOnlineString(String onlineString) {
	this.onlineString = onlineString;
    }

    /**
     * @return the updated_at
     */
    public String getUpdated_at() {
	return updated_at;
    }

    /**
     * @param updated_at
     *            the updated_at to set
     */
    public void setUpdated_at(String updated_at) {
	this.updated_at = updated_at;
    }

    /**
     * @return the updated_at_Long
     */
    public long getUpdated_at_Long() {
	return updated_at_Long;
    }

    /**
     * @param updated_at_Long
     *            the updated_at_Long to set
     */
    public void setUpdated_at_Long(long updated_at_Long) {
	this.updated_at_Long = updated_at_Long;
    }

    @Override
    public String getChannel() {
	return channel;
    }

    /**
     * @return the current_viewers
     */
    public int getCurrent_viewers() {
	return current_viewers;
    }

    @Override
    public void setChannel(String channel) {
	this.channel = channel;

    }

}
