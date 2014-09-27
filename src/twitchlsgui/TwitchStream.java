package twitchlsgui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import twitchAPI.Twitch_API;
import twitchAPI.Twitch_Stream;

public class TwitchStream {
    public String channel;
    public String game;
    public String title;
    public BufferedImage preview;
    private boolean online = false;

    public TwitchStream(String channel) {
	this.channel = channel;
    }

    public void refresh() {
	Twitch_Stream ts = Twitch_API.getStream(channel);
	game = ts.getMeta_game();
	title = ts.getTitle();
	online = ts.isOnline();
	if (ts.getScreen_cap_url_medium() != null) {
	    try {
		preview = ImageIO.read(new URL(ts.getScreen_cap_url_medium()));
	    } catch (IOException e) {
		e.printStackTrace();
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

}
