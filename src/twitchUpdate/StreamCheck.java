package twitchUpdate;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import stream.GenericStream;
import stream.TwitchStream;
import twitchlsgui.Main_GUI;
import twitchlsgui.OptionsPanel;

/**
 * Constant running thread which checks regulary for stream updates
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class StreamCheck implements Runnable {

    private static ArrayList<Thread> threads = new ArrayList<Thread>();
    private static ArrayList<GenericStream> streamList;
    public static AtomicInteger finishedUpdates = new AtomicInteger(0);

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
    public static void update() {
	Main_GUI.canUpdate = false;
	streamList = Main_GUI.selectStreamService("twitch.tv").getStreamList();

	if (streamList.size() > 0) {
	    if (Main_GUI.currentStreamService.equals(Main_GUI
		    .selectStreamService("twitch.tv").getUrl())) {
		Main_GUI.updateStatus.setText("Updating " + "("
			+ finishedUpdates + "/"
			+ Main_GUI.streamListModel.size() + ")");
	    } else {
		Main_GUI.updateStatus.setText("");
	    }

	    for (int i = 0; i < streamList.size(); i++) {
		threads.add(new Thread(new CheckThread(i, streamList)));
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
		    Main_GUI.updateStatus.setText("Updating. " + "("
			    + finishedUpdates + "/"
			    + Main_GUI.streamListModel.size() + ")");
		} catch (InterruptedException e) {
		    if (Main_GUI._DEBUG)
			e.printStackTrace();
		}
	    }
	    if (Main_GUI._DEBUG)
		System.out.println(streamList.size() + " threads were joined");
	    for (int i = 0; i < Main_GUI.streamListModel.size(); i++) {
		Main_GUI.streamListModel.setElementAt(
			Main_GUI.streamListModel.get(i), i);
	    }
	}

	if (Main_GUI.currentStreamService.equals(Main_GUI.selectStreamService(
		"twitch.tv").getUrl())) {
	    Main_GUI.updateStatus.setText("Finished updating");
	    finishedUpdates.set(0);;
	} else {
	    Main_GUI.updateStatus.setText("");
	}
	if (!Main_GUI.currentStreamName.equals("")) {
	    if (Main_GUI.currentStreamService.equals(Main_GUI
		    .selectStreamService("twitch.tv").getUrl())) {
		for (int i = 0; i < streamList.size(); i++) {
		    TwitchStream ts = (TwitchStream) streamList.get(i);
		    if (ts.getChannel().equals(Main_GUI.currentStreamName)) {
			if (ts.isOnline()) {
			    Main_GUI.onlineStatus.setText(ts.getOnlineString());
			    Main_GUI.setPreviewImage(ts.getPreview());
			} else {
			    Main_GUI.onlineStatus.setText("Stream is Offline");
			}
		    }
		}
	    } else {
		Main_GUI.onlineStatus.setText("");
	    }
	}
	threads.clear();
	OptionsPanel.KBLabel.setText(Main_GUI.downloadedBytes / 1000 + "");
	Main_GUI.downloadedBytes = 0;
	Main_GUI.canUpdate = true;
    }
}
