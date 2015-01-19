package twitchlsgui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    private final String AUTOUPDATE = "autoupdate";

    private JFrame parent;

    /**
     * Constructor
     */
    public ConfigUtil(JFrame parent) {
	myPrefs = Preferences.userNodeForPackage(twitchlsgui.Main_GUI.class);
	readConfig();
	this.parent = parent;
    }

    public void saveStream(String stream, String streamService) {
	if (!streamService.equals("twitch.tv")
		&& !streamService.equals("Twitch")) {
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
	Main_GUI.autoUpdate = myPrefs.getBoolean(AUTOUPDATE, true);

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
	myPrefs.putBoolean(AUTOUPDATE, Main_GUI.autoUpdate);

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
			if (Main_GUI.selectStreamServiceD(lineSplit[0]) == null
				&& Main_GUI.selectStreamService(lineSplit[1]) == null) {
			    Main_GUI.streamServicesList.add(new StreamList(
				    lineSplit[1], lineSplit[0]));
			}
			lastService = lineSplit[1];
		    } else {
			boolean exists = false;
			for (String stream : lineSplit) {
			    if (!stream.equals(" ") || !stream.equals("")) {
				if (Main_GUI.selectStreamService(lastService)
					.getStreamList().size() > 0) {
				    for (GenericStream gs : Main_GUI
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
		e.printStackTrace();
	    }
	}
	jfc.setVisible(false);
    }

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
	    file = new File(path);
	    try {
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		for (StreamList service : Main_GUI.streamServicesList) {
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
		e.printStackTrace();
	    }
	}
	jfc.setVisible(false);
    }
}