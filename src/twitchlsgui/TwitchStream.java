package twitchlsgui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import twitchAPI.Twitch_API;
import twitchAPI.Twitch_Stream;

public class TwitchStream {
    public String channel;
    public String game;
    public String title;
    public BufferedImage preview;
    public String upTimeString;
    public long upTimeLong;
    public int upTimeHour;
    public int upTimeMinute;
    private boolean online = false;

    public TwitchStream(String channel) {
	this.channel = channel;
    }

    public void refresh() {
	Twitch_Stream ts = Twitch_API.getStream(channel);
	game = ts.getMeta_game();
	title = ts.getTitle();
	online = ts.isOnline();
	upTimeString = ts.getUp_time();
	if (upTimeString != null) {
	    upTimeLong = convertDate(upTimeString);
	    GregorianCalendar c = new GregorianCalendar();
	    c.setTimeInMillis(System.currentTimeMillis() - upTimeLong);
	    upTimeHour = c.get(Calendar.HOUR_OF_DAY);
	    upTimeMinute = c.get(Calendar.MINUTE);
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
	String a = date;
	String[] b = a.split("T");
	a = "";
	for (String s : b) {
	    if (a != "") {
		a = a + " " + s;
	    } else {
		a = a + s;
	    }
	}
	b = a.split("Z");
	a = "";
	for (String s : b) {
	    a = a + s;
	}
	Date d = null;
	try {
	    d = fm.parse(a);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return d.getTime();
    }

    public int getUpTimeHours() {
	return upTimeHour;
    }

    public String getUpTimeMinutes() {
	if (upTimeMinute < 10) {
	    return 0 + "" + upTimeMinute;
	} else {
	    return "" + upTimeMinute;
	}
    }
}
