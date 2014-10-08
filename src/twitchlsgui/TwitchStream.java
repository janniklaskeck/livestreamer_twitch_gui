package twitchlsgui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import twitchAPI.Twitch_API;
import twitchAPI.Twitch_Stream;

public class TwitchStream {
    public String channel;
    public String game;
    public String title;
    public BufferedImage preview;
    public String created_at;
    public String updated_at;
    public long created_at_Long;
    public long updated_at_Long;
    public long upTimeHour;
    public long upTimeMinute;
    private boolean online = false;
    private String onlineString;

    public TwitchStream(String channel) {
	this.channel = channel;
    }

    public void refresh() {
	Twitch_Stream ts = Twitch_API.getStream(channel);
	game = ts.getMeta_game();
	title = ts.getTitle();
	online = ts.isOnline();
	created_at = ts.getCreated_At();
	updated_at = ts.getUpdated_At();
	upTimeHour = 0L;
	upTimeMinute = 0L;

	if (online) {
	    created_at_Long = convertDate(created_at);
	    // updated_at_Long = convertDate(updated_at);

	    upTimeHour = ((System.currentTimeMillis() - created_at_Long) / (1000 * 60 * 60)) % 24;
	    upTimeMinute = ((System.currentTimeMillis() - created_at_Long) / (1000 * 60)) % 60;
	    setOnlineString("<html>Playing " + getGame() + " (Online for "
		    + getUpTimeHours() + ":" + getUpTimeMinutes() + " hours)"
		    + "<br>" + getTitle() + "</html>");
	}

	if (ts.getScreen_cap_url_medium() != null) {
	    for (int i = 0; i < 5; i++) {
		try {
		    preview = ImageIO.read(new URL(ts
			    .getScreen_cap_url_medium()));
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

    }

    /**
     * 
     * @return true if channel is online
     */
    public boolean isOnline() {
	return online;
    }

    /**
     * @return the game
     */
    public String getGame() {
	return game;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
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
	    e.printStackTrace();
	}
	return d.getTime();
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
}
