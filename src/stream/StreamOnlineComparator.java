package stream;

import java.util.Comparator;

public class StreamOnlineComparator implements
	Comparator<GenericStreamInterface> {

    @Override
    public int compare(GenericStreamInterface stream0,
	    GenericStreamInterface stream1) {
	TwitchStream ts1 = (TwitchStream) stream0;
	TwitchStream ts2 = (TwitchStream) stream1;
	if (ts1.isOnline() && !ts2.isOnline()) {
	    return -1;
	} else if (!ts1.isOnline() && ts2.isOnline()) {
	    return 1;
	} else {
	    return 0;
	}
    }

}
