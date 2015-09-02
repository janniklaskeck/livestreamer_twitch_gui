package gamesPanel;

import gamesPanel.channel.ChannelComparator;
import gamesPanel.channel.GameThread;
import gamesPanel.game.ChannelThread;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import twitchAPI.Twitch_Game_Json;

import com.google.gson.JsonObject;

public class TwitchDirectory {

    GamesPane parent;
    Component home;
    public JsonObject currentGameJSon;
    public ArrayList<Twitch_Game_Json> channels;
    private ChannelThread ct;
    public JPanel jp;
    GameThread gt;

    public TwitchDirectory(GamesPane parent) {
	this.parent = parent;
	channels = new ArrayList<Twitch_Game_Json>();
	ct = new ChannelThread(this.parent);
	gt = new GameThread(this.parent);
    }

    public void refresh() {
	parent.gamesJSON = parent.parent.globals.twitchAPI.getGames();
	parent.size = parent.gamesJSON.get("top").getAsJsonArray().size();
	parent.scrollView.removeAll();
	parent.scrollView.setLayout(new GridLayout(0, 4, 0, 0));
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		gt.loadImages();
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
		ct.load();
	    }
	}).start();

	parent.scrollPane.setViewportView(jp);
    }
}
