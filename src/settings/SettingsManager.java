package settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import stream.GenericStreamInterface;
import stream.OtherStream;
import stream.StreamList;
import stream.TwitchStream;
import twitchAPI.Twitch_API;
import twitchlsgui.Main_GUI;

/**
 * Config class
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class SettingsManager {
    private Preferences myPrefs;

    // final strings for saving the different options
    private final String QUALITY = "quality";
    private final String TIMER = "timer";
    private final String SHOWPREVIEW = "showpreview";
    private final String STREAMSERVICES = "streamservices";
    private final String AUTOUPDATE = "autoupdate";
    private final String TWITCHUSER = "twitchusername";
    private final String TWITCHOAuth = "twitchoauth";
    private final String DEBUG = "enabledebugoutput";

    private Main_GUI parent;

    /**
     * Constructor Responsible for loading and saving the settings and streams
     */
    public SettingsManager(Main_GUI parent) {
	// set registry path for saving
	myPrefs = Preferences.userNodeForPackage(twitchlsgui.Main_GUI.class);
	// read config on creation
	readSettings();
	// reference need for import/export
	this.parent = parent;
    }

    /**
     * Checks for errors while retrieving information for the Twitch.tv stream
     * 
     * @param stream
     * @return true if there is no error, false otherwise
     */
    private boolean streamExists(String stream) {
	if (Twitch_API.checkTwitch(stream) != null) {
	    return true;
	}
	return false;
    }

    /**
     * Checks if the stream is a Twitchstream or not and adds it to the
     * Streamlist of the matching Streamservice. After that it saves it to the
     * registry
     * 
     * @param streamname
     *            to add
     * @param streamService
     *            to add the stream to
     */
    public void saveStream(String stream, String streamService) {
	if (!streamService.equals("twitch.tv")
		&& !streamService.equals("Twitch")) {
	    parent.selectStreamService(streamService).getStreamList()
		    .add(new OtherStream(stream));
	} else {
	    if (streamExists(stream)) {
		parent.selectStreamService(streamService).getStreamList()
			.add(new TwitchStream(stream));
	    } else {
		parent.displayMessage("This stream doesn't seem to exist."
			+ System.lineSeparator()
			+ "Check if you spelled it correct.");
	    }
	}
	writeStreams(streamService);
    }

    /**
     * Selects the streamservice and removes the stream from its streamlist
     * After that it saves to the registry
     * 
     * @param stream
     *            to remove
     * @param streamService
     *            to remove the stream from
     */
    public void removeStream(String stream, String streamService) {
	for (int i = 0; i < parent.selectStreamService(streamService)
		.getStreamList().size(); i++) {
	    if (parent.selectStreamService(streamService).getStreamList()
		    .get(i).getChannel().equals(stream)) {
		parent.selectStreamService(streamService).getStreamList()
			.remove(i);
		break;
	    }
	}
	writeStreams(streamService);
    }

    /**
     * Reads general settings and streamservice List from the registry
     */
    public void readSettings() {
	parent.currentQuality = myPrefs.get(QUALITY, "High");
	Main_GUI.showPreview = myPrefs.getBoolean(SHOWPREVIEW, true);
	Main_GUI.checkTimer = myPrefs.getInt(TIMER, 30);
	Main_GUI.autoUpdate = myPrefs.getBoolean(AUTOUPDATE, true);
	Main_GUI.twitchOAuth = myPrefs.get(TWITCHOAuth, "");
	Main_GUI.twitchUser = myPrefs.get(TWITCHUSER, "");
	Main_GUI._DEBUG = myPrefs.getBoolean(DEBUG, false);

	String buffer = myPrefs.get(STREAMSERVICES, "twitch.tv");
	String[] buffer2 = buffer.split(" ");
	parent.streamServicesList = new ArrayList<StreamList>();
	for (String s : buffer2) {
	    if (s.length() > 1)
		parent.streamServicesList.add(new StreamList(s, myPrefs.get(s
			+ "_displayname", s)));
	}
    }

    /**
     * Reads the streamlist for the streamservice from the registry, corrects it
     * if neccessary and creates the streamlist for the streamservice
     * 
     * @param streamService
     */
    public void readStreamList(String streamService) {
	String streams = myPrefs.get(streamService, "");
	streams = correctStreamList(streams);
	String[] streams_split = streams.split(" ");
	parent.selectStreamService(streamService).setStreamList(
		new ArrayList<GenericStreamInterface>());
	for (int i = 0; i < streams_split.length; i++) {

	    if (streamService.equals("twitch.tv")) {
		parent.selectStreamService(streamService).addStream(
			new TwitchStream(streams_split[i]));
	    } else {
		parent.selectStreamService(streamService).addStream(
			new OtherStream(streams_split[i]));
	    }
	}
    }

    /**
     * Removes spaces in front and at the end of streamList
     * 
     * @param streamList
     * @return a string without spaces in front and at the end
     */
    private String correctStreamList(String streamList) {
	String corrected = streamList;
	if (streamList.startsWith(" ")) {
	    corrected = corrected.substring(1);
	}
	if (streamList.endsWith(" ")) {
	    corrected = corrected.substring(0, corrected.length() - 1);
	}
	return corrected;
    }

    /**
     * Writes general settings and streamservices to the registry
     */
    public void writeSettings() {
	myPrefs.put(QUALITY, parent.currentQuality);
	myPrefs.putBoolean(SHOWPREVIEW, Main_GUI.showPreview);
	myPrefs.putInt(TIMER, Main_GUI.checkTimer);
	myPrefs.putBoolean(AUTOUPDATE, Main_GUI.autoUpdate);
	myPrefs.put(TWITCHUSER, Main_GUI.twitchUser);
	myPrefs.put(TWITCHOAuth, Main_GUI.twitchOAuth);
	myPrefs.putBoolean(DEBUG, Main_GUI._DEBUG);

	String buffer = "";
	for (int i = 0; i < parent.streamServicesList.size(); i++) {
	    myPrefs.put(parent.streamServicesList.get(i).getUrl()
		    + "_displayname", parent.streamServicesList.get(i)
		    .getDisplayName());
	    if (buffer == "") {
		buffer = buffer + parent.streamServicesList.get(i).getUrl();
	    } else {
		buffer = buffer + " "
			+ parent.streamServicesList.get(i).getUrl();
	    }
	}
	myPrefs.put(STREAMSERVICES, buffer);
    }

    /**
     * Writes the streams from a streamservice streamlist to the registry
     */
    public void writeStreams(String streamService) {
	String buffer = "";
	for (GenericStreamInterface ts : parent.selectStreamService(
		streamService).getStreamList()) {
	    if (buffer == "") {
		buffer = ts.getChannel();
	    } else {
		buffer += " " + ts.getChannel();
	    }
	}
	myPrefs.put(streamService, buffer);
    }

    /**
     * Opens a file dialog to select a file containing streamservice and streams
     */
    public void importStreams() {
	String path = "";
	JFileChooser jfc = new JFileChooser(path);
	jfc.setDialogType(JFileChooser.OPEN_DIALOG);
	FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(".txt",
		"txt");

	jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
	jfc.setFileFilter(txtFilter);
	jfc.setDialogTitle("Open File...");
	jfc.setVisible(true);
	int result = jfc.showOpenDialog(parent);

	if (result == JFileChooser.APPROVE_OPTION) {
	    path = jfc.getSelectedFile().toString();

	    try {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = "";
		String[] lineSplit;
		String lastService = "";
		int lineNumber = 0;
		while ((line = br.readLine()) != null) {

		    lineSplit = line.split(" ");

		    if (lineNumber == 0 || lineNumber % 2 == 0) {
			if (parent.selectStreamServiceD(lineSplit[0]) == null
				&& parent.selectStreamService(lineSplit[1]) == null) {
			    parent.streamServicesList.add(new StreamList(
				    lineSplit[1], lineSplit[0]));
			}
			lastService = lineSplit[1];
		    } else {
			boolean exists = false;
			for (String stream : lineSplit) {
			    if (!stream.equals(" ") || !stream.equals("")) {
				if (parent.selectStreamService(lastService)
					.getStreamList().size() > 0) {
				    for (GenericStreamInterface gs : parent
					    .selectStreamService(lastService)
					    .getStreamList()) {
					if (gs.getChannel().toLowerCase()
						.equals(stream.toLowerCase())) {
					    exists = true;
					}
				    }
				    if (!exists) {
					saveStream(stream, lastService);
				    }
				} else {
				    saveStream(stream, lastService);
				}
			    }
			}
		    }
		    lineNumber++;
		}
		br.close();
	    } catch (Exception e) {
		if (Main_GUI._DEBUG)
		    e.printStackTrace();
	    }
	}
	jfc.setVisible(false);
    }

    /**
     * Opens a file dialog to save a .txt file containing all streamservices and
     * their streamlists
     */
    public void exportStreams() {
	File file;
	String path = "";
	JFileChooser jfc = new JFileChooser(path);
	jfc.setDialogType(JFileChooser.SAVE_DIALOG);
	FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(".txt",
		"txt");

	jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
	jfc.setFileFilter(txtFilter);
	jfc.setDialogTitle("Save as...");
	jfc.setVisible(true);
	int result = jfc.showSaveDialog(parent);

	if (result == JFileChooser.APPROVE_OPTION) {
	    path = jfc.getSelectedFile().toString();
	    String[] pathSplit = path.split(".");
	    if (pathSplit.length > 0 && pathSplit[pathSplit.length] != "txt") {
		path = path + ".txt";
	    } else if (pathSplit.length == 0) {
		path = path + ".txt";
	    }
	    System.out.println(path);
	    file = new File(path);
	    try {
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		for (StreamList service : parent.streamServicesList) {
		    output.write(service.getDisplayName() + " "
			    + service.getUrl());
		    output.newLine();
		    for (int i = 0; i < service.getStreamList().size(); i++) {
			if (!service.getStreamList().get(i).getChannel()
				.equals(" ")) {
			    output.write(service.getStreamList().get(i)
				    .getChannel());
			    if (i < service.getStreamList().size() - 1) {
				output.write(" ");
			    }
			}
		    }
		    output.newLine();
		}
		output.close();
	    } catch (IOException e) {
		if (Main_GUI._DEBUG)
		    e.printStackTrace();
	    }
	}
	jfc.setVisible(false);
    }
}