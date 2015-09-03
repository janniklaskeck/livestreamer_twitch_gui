package gamesPanel;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;

import com.google.gson.JsonObject;

import gamesPanel.channel.ChannelThread;
import gamesPanel.channel.TwitchChannel_List;
import gamesPanel.game.GameThread;
import gamesPanel.game.TwitchGame_List;

public class TwitchDirectory {

    // JsonObject gamesJSON;
    public GamesPane parent;
    private Component home;
    public JsonObject currentGameJSon;
    public ChannelThread ct;
    public JPanel channelPanel;
    public GameThread gt;
    public TwitchGame_List games_list;
    public TwitchChannel_List channel_list;

    // public ArrayList<TwitchGame> games;

    public TwitchDirectory(GamesPane parent) {
	this.parent = parent;
	ct = new ChannelThread(this.parent);
	gt = new GameThread(this.parent);
	games_list = new TwitchGame_List(this);
	channel_list = new TwitchChannel_List(this);
    }

    public void refresh() {
	games_list.update();
	home = parent.scrollView;
    }

    public void home() {
	channel_list.channels.clear();
	if (home == null) {
	    refresh();
	} else {
	    parent.scrollPane.setViewportView(home);
	}
	parent.progress.set(0);
    }

    public void switchToGame() {
	channelPanel = new JPanel();
	channelPanel.setLayout(new GridLayout(0, 4, 0, 0));
	channel_list.update();
	parent.scrollPane.setViewportView(channelPanel);
    }
}
