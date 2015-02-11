package twitchUpdate;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import settings.SettingsPanel;
import stream.GenericStreamInterface;
import stream.TwitchStream;
import twitchlsgui.Main_GUI;

/**
 * Constant running thread which checks regulary for stream updates
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class TwitchUpdateThread extends Thread {

    private static ArrayList<Thread> threads = new ArrayList<Thread>();
    private static ArrayList<GenericStreamInterface> streamList;
    public static AtomicInteger finishedUpdates = new AtomicInteger(0);
    private SettingsPanel settingsPane;
    private Main_GUI parent;

    public TwitchUpdateThread(SettingsPanel settingsPane, Main_GUI parent) {
	this.settingsPane = settingsPane;
	this.parent = parent;
    }

    @Override
    public void run() {
	while (true) {
	    if (Main_GUI.autoUpdate) {
		update();
	    }
	    try {
		Thread.sleep(Main_GUI.checkTimer * 1000);
	    } catch (InterruptedException e) {
		if (Main_GUI._DEBUG)
		    e.printStackTrace();
	    }
	}
    }

    /**
     * Starts a thread for every Twitch.tv stream to update it
     */
    public void update() {
	parent.canUpdate = false;
	streamList = parent.selectStreamService("twitch.tv").getStreamList();

	if (streamList.size() > 0) {
	    if (parent.currentStreamService.equals(parent.selectStreamService(
		    "twitch.tv").getUrl())) {
		parent.updateStatus.setText("Updating " + "(" + finishedUpdates
			+ "/" + parent.streamListModel.size() + ")");
	    } else {
		parent.updateStatus.setText("");
	    }

	    for (int i = 0; i < streamList.size(); i++) {
		threads.add(new Thread(new TwitchUpdateWorker(i, streamList)));
	    }
	    if (Main_GUI._DEBUG)
		System.out.println("added " + streamList.size()
			+ " new threads");
	    for (int i = 0; i < streamList.size(); i++) {
		threads.get(i).start();
	    }
	    if (Main_GUI._DEBUG)
		System.out.println("started " + streamList.size() + " threads");
	    for (int i = 0; i < streamList.size(); i++) {
		try {
		    threads.get(i).join();
		    parent.updateStatus.setText("Updating. " + "("
			    + finishedUpdates + "/"
			    + parent.streamListModel.size() + ")");
		} catch (InterruptedException e) {
		    if (Main_GUI._DEBUG)
			e.printStackTrace();
		}
	    }
	    if (Main_GUI._DEBUG)
		System.out.println(streamList.size() + " threads were joined");
	    for (int i = 0; i < parent.streamListModel.size(); i++) {
		parent.streamListModel.setElementAt(
			parent.streamListModel.get(i), i);
	    }
	}

	if (parent.currentStreamService.equals(parent.selectStreamService(
		"twitch.tv").getUrl())) {
	    parent.updateStatus.setText("Finished updating");
	    finishedUpdates.set(0);
	    ;
	} else {
	    parent.updateStatus.setText("");
	}
	if (!Main_GUI.currentStreamName.equals("")) {
	    if (parent.currentStreamService.equals(parent.selectStreamService(
		    "twitch.tv").getUrl())) {
		for (int i = 0; i < streamList.size(); i++) {
		    TwitchStream ts = (TwitchStream) streamList.get(i);
		    if (ts.getChannel().equals(Main_GUI.currentStreamName)) {
			if (ts.isOnline()) {
			    parent.onlineStatus.setText(ts.getOnlineString());
			    Main_GUI.setPreviewImage(ts.getPreview());
			} else {
			    parent.onlineStatus.setText("Stream is Offline");
			}
		    }
		}
	    } else {
		parent.onlineStatus.setText("");
	    }
	}
	threads.clear();
	settingsPane.setKBLabel(Main_GUI.downloadedBytes / 1000 + "");
	Main_GUI.downloadedBytes = 0;
	parent.canUpdate = true;
    }
}
