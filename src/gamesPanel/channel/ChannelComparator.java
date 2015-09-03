package gamesPanel.channel;

import java.util.Comparator;

public class ChannelComparator implements Comparator<TwitchChannel> {

    @Override
    public int compare(TwitchChannel a, TwitchChannel b) {
	if (a.getJson().getViewers() > b.getJson().getViewers()) {
	    return -1;
	} else if (a.getJson().getViewers() < b.getJson().getViewers()) {
	    return 1;
	}
	return 0;
    }
}
