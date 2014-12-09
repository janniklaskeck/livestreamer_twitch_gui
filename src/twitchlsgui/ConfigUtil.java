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
    // private final String STREAMLIST = "streamlist";
    private final String SHOWPREVIEW = "showpreview";
    private final String STREAMSERVICES = "streamservices";

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
	writeStreams(Main_GUI.currentStreamService);
	readStreamList(Main_GUI.currentStreamService);
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
	writeStreams(Main_GUI.currentStreamService);
	readStreamList(Main_GUI.currentStreamService);
	Main_GUI.updateList();
    }

    /**
     * Reads config from registry
     */
    public void readConfig() {
	Functions.checkTimer = myPrefs.getInt(TIMER, 30);
	Main_GUI.currentQuality = myPrefs.get(QUALITY, "High");
	Main_GUI.showPreview = myPrefs.getBoolean(SHOWPREVIEW, true);

	String buffer = myPrefs.get(STREAMSERVICES, "twitch.tv");
	String[] buffer2 = buffer.split(" ");
	Functions.streamServicesList = new ArrayList<String>();
	for (String s : buffer2) {
	    if (s.length() > 1)
		Functions.streamServicesList.add(s);
	}
    }

    public void readStreamList(String streamService) {
	String buffer = myPrefs.get(streamService, "");
	String[] buffer2 = buffer.split(" ");
	Functions.streamList = new ArrayList<TwitchStream>();
	for (String s : buffer2) {
	    if (s.length() >= 1)
		Functions.streamList.add(new TwitchStream(s));
	}
    }

    /**
     * Writes config to registry
     */
    public void writeConfig() {
	myPrefs.put(QUALITY, Main_GUI.currentQuality);
	myPrefs.putInt(TIMER, Functions.checkTimer);
	myPrefs.putBoolean(SHOWPREVIEW, Main_GUI.showPreview);
	String buffer = "";
	for (int i = 0; i < Main_GUI.streamServicesBox.getItemCount(); i++) {
	    if (buffer == "") {
		buffer = buffer + Main_GUI.streamServicesBox.getItemAt(i);
	    } else {
		buffer = buffer + " " + Main_GUI.streamServicesBox.getItemAt(i);
	    }
	}
	myPrefs.put(STREAMSERVICES, buffer);
    }

    /**
     * Writes config to registry
     */
    public void writeStreams(String service) {
	String buffer = "";
	for (TwitchStream ts : Functions.streamList) {
	    if (buffer == "") {
		buffer = ts.getChannel();
	    } else {
		buffer += " " + ts.getChannel();
	    }
	}
	myPrefs.put(service, buffer);

    }
}
