package stream;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 
 * @author Niklas 21.01.2015
 * 
 */
public class StreamList {

    private volatile ArrayList<GenericStreamInterface> streamList;
    private String url;
    private String displayName;

    public StreamList(String url, String displayName) {
	this.streamList = new ArrayList<GenericStreamInterface>();
	this.url = url;
	this.displayName = displayName;
    }

    public void sortList() {
	if (url.equals("twitch.tv")) {
	    streamList.sort(new Comparator<GenericStreamInterface>() {

		@Override
		public int compare(GenericStreamInterface o1,
			GenericStreamInterface o2) {
		    TwitchStream ts1 = (TwitchStream) o1;
		    TwitchStream ts2 = (TwitchStream) o2;
		    if (ts1.isOnline() && !ts2.isOnline()) {
			return -1;
		    } else if (!ts1.isOnline() && ts2.isOnline()) {
			return 1;
		    } else {
			return 0;
		    }
		}
	    });
	}
    }

    /**
     * @return the streamList
     */
    public ArrayList<GenericStreamInterface> getStreamList() {
	return streamList;
    }

    /**
     * @param streamList
     *            the streamList to set
     */
    public void setStreamList(ArrayList<GenericStreamInterface> streamList) {
	this.streamList = streamList;
    }

    /**
     * @return the url
     */
    public String getUrl() {
	return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
	this.url = url;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
	return displayName;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    public void addStream(GenericStreamInterface twitchStream) {
	streamList.add(twitchStream);
    }

}
