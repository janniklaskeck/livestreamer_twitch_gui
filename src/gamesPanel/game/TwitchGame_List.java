package gamesPanel.game;

import gamesPanel.TwitchDirectory;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;

import twitchAPI.Twitch_API;
import twitchAPI.Twitch_List_Json;

import com.google.gson.JsonObject;

public class TwitchGame_List {

    private ArrayList<TwitchGame> games;
    private JsonObject gamesJSON;
    private TwitchDirectory parent;

    public TwitchGame_List(TwitchDirectory parent) {
	games = new ArrayList<TwitchGame>();
	this.parent = parent;
	load();
    }

    public void load() {
	games.clear();
	gamesJSON = Twitch_API.getGames();
	for (int i = 0; i < gamesJSON.get("top").getAsJsonArray().size(); i++) {
	    Twitch_List_Json a = new Twitch_List_Json();
	    a.load(gamesJSON.get("top").getAsJsonArray().get(i)
		    .getAsJsonObject());
	    games.add(new TwitchGame(a, parent));
	}
	sort();
    }

    public void sort() {
	games.sort(new GamesComparator());
    }

    public void update() {
	gamesJSON = Twitch_API.getGames();
	load();
	parent.parent.scrollView.removeAll();
	parent.parent.scrollView.setLayout(new GridLayout(0, 4, 0, 0));
	new Thread(new Runnable() {
	    @Override
	    public void run() {
		parent.gt.loadImages(gamesJSON.get("top").getAsJsonArray()
			.size());
	    }
	}).start();
    }

    public void setLogo(int index, BufferedImage logo) {
	games.get(index).setLogo(logo);
    }

    public JButton getButton(int index) {
	return games.get(index).getButton();
    }

    public Twitch_List_Json getJson(int index) {
	return games.get(index).getJson();
    }

    public int getSize() {
	return games.size();
    }
}
