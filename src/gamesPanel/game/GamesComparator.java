package gamesPanel.game;

import java.util.Comparator;

public class GamesComparator implements Comparator<TwitchGame> {

    @Override
    public int compare(TwitchGame a, TwitchGame b) {
	if (a.getJson().getViewers() < b.getJson().getViewers()) {
	    return 1;
	}
	if (a.getJson().getViewers() > b.getJson().getViewers()) {
	    return -1;
	}
	return 0;
    }

}
