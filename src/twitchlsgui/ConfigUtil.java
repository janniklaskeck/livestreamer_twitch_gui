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

    public void saveStream(String stream, String streamService) {
	if (!streamService.equals("twitch.tv")) {
	    Main_GUI.selectStreamService(streamService).getStreamList()
		    .add(new OtherStream(stream));
	} else {
	    Main_GUI.selectStreamService(streamService).getStreamList()
		    .add(new TwitchStream(stream));
	}
	writeStreams(streamService);
    }

    public void removeStream(String stream, String streamService) {
	for (int i = 0; i < Main_GUI.selectStreamService(streamService)
		.getStreamList().size(); i++) {
	    if (Main_GUI.selectStreamService(streamService).getStreamList()
		    .get(i).getChannel().equals(stream)) {
		Main_GUI.selectStreamService(streamService).getStreamList()
			.remove(i);
		break;
	    }
	}
	writeStreams(streamService);
    }

    /**
     * Reads config from registry
     */
    public void readConfig() {
	Main_GUI.currentQuality = myPrefs.get(QUALITY, "High");
	Main_GUI.showPreview = myPrefs.getBoolean(SHOWPREVIEW, true);
	Main_GUI.checkTimer = myPrefs.getInt(TIMER, 30);

	String buffer = myPrefs.get(STREAMSERVICES, "twitch.tv");
	String[] buffer2 = buffer.split(" ");
	Main_GUI.streamServicesList = new ArrayList<StreamList>();
	for (String s : buffer2) {
	    if (s.length() > 1)
		Main_GUI.streamServicesList.add(new StreamList(s, myPrefs.get(s
			+ "_displayname", s)));
	}

    }

    public void readStreamList(String streamService) {
	String streams = myPrefs.get(streamService, "");

	String[] streams_split = streams.split(" ");
	Main_GUI.selectStreamService(streamService).setStreamList(
		new ArrayList<GenericStream>());
	for (int i = 0; i < streams_split.length; i++) {

	    if (streamService.equals("twitch.tv")) {
		Main_GUI.selectStreamService(streamService).addStream(
			new TwitchStream(streams_split[i]));
	    } else {
		Main_GUI.selectStreamService(streamService).addStream(
			new OtherStream(streams_split[i]));
	    }
	}

    }

    /**
     * Writes config to registry
     */
    public void writeConfig() {
	myPrefs.put(QUALITY, Main_GUI.currentQuality);
	myPrefs.putBoolean(SHOWPREVIEW, Main_GUI.showPreview);
	myPrefs.putInt(TIMER, Main_GUI.checkTimer);

	String buffer = "";
	for (int i = 0; i < Main_GUI.streamServicesList.size(); i++) {
	    myPrefs.put(Main_GUI.streamServicesList.get(i).getUrl()
		    + "_displayname", Main_GUI.streamServicesList.get(i)
		    .getDisplayName());
	    if (buffer == "") {
		buffer = buffer + Main_GUI.streamServicesList.get(i).getUrl();
	    } else {
		buffer = buffer + " "
			+ Main_GUI.streamServicesList.get(i).getUrl();
	    }
	}
	myPrefs.put(STREAMSERVICES, buffer);
    }

    /**
     * Writes config to registry
     */
    public void writeStreams(String streamService) {
	String buffer = "";
	for (GenericStream ts : Main_GUI.selectStreamService(streamService)
		.getStreamList()) {
	    if (buffer == "") {
		buffer = ts.getChannel();
	    } else {
		buffer += " " + ts.getChannel();
	    }
	}
	myPrefs.put(streamService, buffer);

    }
}