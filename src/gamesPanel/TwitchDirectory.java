package gamesPanel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import twitchAPI.Twitch_Game_Json;

import com.google.gson.JsonObject;

public class TwitchDirectory {

    GamesPane parent;
    Component home;
    JsonObject currentGameJSon;
    ArrayList<Twitch_Game_Json> channels;
    private GameThread gt;
    JPanel jp;

    public TwitchDirectory(GamesPane parent) {
	this.parent = parent;
	channels = new ArrayList<Twitch_Game_Json>();
	gt = new GameThread(this.parent);

    }

    public void refresh() {
	parent.scrollView.removeAll();
	parent.scrollView.setLayout(new GridLayout(0, 4, 0, 0));
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		parent.it.loadImages();
	    }
	}).start();
	home = parent.scrollView;
    }

    public void home() {
	channels.clear();
	if (home == null) {
	    refresh();
	} else {
	    parent.scrollPane.setViewportView(home);
	}
	parent.progress.set(0);
    }

    public void switchToGame() {
	jp = new JPanel();
	jp.setLayout(new GridLayout(0, 4, 0, 0));
	int size = currentGameJSon.get("streams").getAsJsonArray().size();
	for (int i = 0; i < size; i++) {
	    Twitch_Game_Json a = new Twitch_Game_Json();
	    a.load(currentGameJSon.get("streams").getAsJsonArray().get(i)
		    .getAsJsonObject());
	    channels.add(a);
	}

	channels.sort(new ChannelComparator());

	new Thread(new Runnable() {
	    @Override
	    public void run() {
		gt.load();
	    }
	}).start();

	parent.scrollPane.setViewportView(jp);
    }
}
