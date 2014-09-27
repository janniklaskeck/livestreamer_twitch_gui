package twitchlsgui;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class ConfigUtil {

    File f = new File("./config.ini");
    Preferences myPrefs;

    private final String QUALITY = "quality";
    private final String TIMER = "timer";
    private final String STREAMLIST = "streamlist";

    /**
     * 
     */
    public ConfigUtil() {
	myPrefs = Preferences.userNodeForPackage(twitchlsgui.Main_GUI.class);
	readConfig();
    }

    public void saveStream(String stream) {
	Functions.streamList.add(new TwitchStream(stream));
	writeConfig();
	readConfig();
	Main_GUI.updateList();
    }

    /**
     * 
     */
    public void readConfig() {
	Functions.checkTimer = myPrefs.getInt(TIMER, 30);
	Main_GUI.currentQuality = myPrefs.get(QUALITY, "high");
	String buffer = myPrefs.get(STREAMLIST, "");

	String[] buffer2 = buffer.split(" ");
	Functions.streamList = new ArrayList<TwitchStream>();
	for (String s : buffer2) {
	    if (s.length() > 1)
		Functions.streamList.add(new TwitchStream(s));
	}
	
    }

    /**
     * 
     */
    public void writeConfig() {
	myPrefs.put(QUALITY, Main_GUI.currentQuality);
	myPrefs.putInt(TIMER, Functions.checkTimer);
	String buffer = "";

	for (TwitchStream s : Functions.streamList) {
	    if (buffer == "") {
		buffer = buffer + s.channel;
	    } else {
		buffer = buffer + " " + s.channel;
	    }
	}
	myPrefs.put(STREAMLIST, buffer);
    }
}
