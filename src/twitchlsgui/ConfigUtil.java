package twitchlsgui;

import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Config class
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class ConfigUtil {
    Preferences myPrefs;

    private final String QUALITY = "quality";
    private final String TIMER = "timer";
    private final String STREAMLIST = "streamlist";

    /**
     * Constructor
     */
    public ConfigUtil() {
	myPrefs = Preferences.userNodeForPackage(twitchlsgui.Main_GUI.class);
	readConfig();
    }

    /**
     * Adds stream to streamList
     * 
     * @param stream
     */
    public void saveStream(String stream) {
	Functions.streamList.add(new TwitchStream(stream));
	writeConfig();
	readConfig();
	Main_GUI.updateList();
    }

    /**
     * Removes stream from streamList
     * 
     * @param stream
     */
    public void removeStream(String stream) {
	for (int i = 0; i < Functions.streamList.size(); i++) {
	    if (Functions.streamList.get(i).getChannel().equals(stream)) {
		Functions.streamList.remove(i);
	    }
	}
	writeConfig();
	readConfig();
	Main_GUI.updateList();
    }

    /**
     * Reads config from registry
     */
    public void readConfig() {
	Functions.checkTimer = myPrefs.getInt(TIMER, 30);
	Main_GUI.currentQuality = myPrefs.get(QUALITY, "High");
	String buffer = myPrefs.get(STREAMLIST, "");

	String[] buffer2 = buffer.split(" ");
	Functions.streamList = new ArrayList<TwitchStream>();
	for (String s : buffer2) {
	    if (s.length() > 1)
		Functions.streamList.add(new TwitchStream(s));
	}

    }

    /**
     * Writes config to registry
     */
    public void writeConfig() {
	myPrefs.put(QUALITY, Main_GUI.currentQuality);
	myPrefs.putInt(TIMER, Functions.checkTimer);
	String buffer = "";

	for (TwitchStream s : Functions.streamList) {
	    if (buffer == "") {
		buffer = buffer + s.getChannel();
	    } else {
		buffer = buffer + " " + s.getChannel();
	    }
	}
	myPrefs.put(STREAMLIST, buffer);
    }
}
