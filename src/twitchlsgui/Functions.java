package twitchlsgui;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class Functions {

    public static ArrayList<TwitchStream> streamList;
    //public static TwitchStream stream;
    public static boolean online = false;
    public static int checkTimer = 20;

    /**
     * 
     * @param name
     * @param quality
     */
    public static void OpenStream(String name, String quality) {
	String cmd = "livestreamer twitch.tv/" + name + " " + quality;
	try {
	    if (Functions.checkStream(name)) {
		@SuppressWarnings("unused")
		Process prc = Runtime.getRuntime().exec(cmd);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     * @param name
     * @return
     */
    public static boolean checkStream(String name) {
	for (TwitchStream ts : streamList) {
	    if (ts.getChannel().equalsIgnoreCase(name)) {
		online = ts.isOnline();
	    }
	}
	return online;
    }

    /**
     * 
     * @param so
     */
    public static void addStream(TwitchStream so) {
	streamList.add(so);
    }

    /**
     * 
     */
    public void removeStream() {

    }

}
