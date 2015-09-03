package gamesPanel.channel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;

import gamesPanel.TwitchDirectory;
import twitchAPI.Twitch_Game_Json;

public class TwitchChannel_List {

    public ArrayList<TwitchChannel> channels;
    private TwitchDirectory parent;

    public TwitchChannel_List(TwitchDirectory parent) {
	channels = new ArrayList<TwitchChannel>();
	this.parent = parent;
    }

    public void sort() {
	channels.sort(new ChannelComparator());
    }

    public void update() {
	int size = parent.currentGameJSon.get("streams").getAsJsonArray().size();
	for (int i = 0; i < size; i++) {
	    Twitch_Game_Json a = new Twitch_Game_Json();
	    a.load(parent.currentGameJSon.get("streams").getAsJsonArray().get(i).getAsJsonObject());
	    channels.add(new TwitchChannel(a, parent));
	}

	sort();

	new Thread(new Runnable() {
	    @Override
	    public void run() {
		parent.ct.load(parent.currentGameJSon.get("streams").getAsJsonArray().size());
	    }
	}).start();
    }

    public void setPreview(int index, BufferedImage logo) {
	channels.get(index).setPreview(logo);
    }

    public JButton getButton(int index) {
	return channels.get(index).getButton();
    }

    public Twitch_Game_Json getJson(int index) {
	return channels.get(index).getJson();
    }

    public int getSize() {
	return channels.size();
    }
}
