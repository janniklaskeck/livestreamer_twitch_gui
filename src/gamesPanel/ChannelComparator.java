package gamesPanel;

import java.util.Comparator;

import twitchAPI.Twitch_Game_Json;

public class ChannelComparator implements Comparator<Twitch_Game_Json> {

    @Override
    public int compare(Twitch_Game_Json a, Twitch_Game_Json b) {
	if (a.getViewers() > b.getViewers()) {
	    return -1;
	} else if (a.getViewers() < b.getViewers()) {
	    return 1;
	}
	return 0;
    }
}
