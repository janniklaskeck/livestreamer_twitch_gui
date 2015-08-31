package gamesPanel;

import java.util.Comparator;

import twitchAPI.Twitch_List_Json;

public class GamesComparator implements Comparator<Twitch_List_Json> {

    @Override
    public int compare(Twitch_List_Json a, Twitch_List_Json b) {
	if (a.getViewers() < b.getViewers()) {
	    return 1;
	}
	if (a.getViewers() > b.getViewers()) {
	    return -1;
	}
	return 0;
    }

}
