package twitchUpdate;

import java.awt.EventQueue;
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
    public AtomicInteger finishedUpdates = new AtomicInteger(0);
    private SettingsPanel settingsPane;
    Main_GUI parent;

    public TwitchUpdateThread(SettingsPanel settingsPane, Main_GUI parent) {
	this.settingsPane = settingsPane;
	this.parent = parent;
    }

    @Override
    public void run() {
	while (true) {
	    if (parent.globals.autoUpdate) {
		update();
	    }
	    try {
		Thread.sleep(parent.globals.checkTimer * 1000);
	    } catch (InterruptedException e) {
		if (parent.globals._DEBUG)
		    e.printStackTrace();
	    }
	}
    }

    /**
     * Starts a thread for every Twitch.tv stream to update it
     */
    public void update() {
	threads.clear();
	parent.canUpdate = false;
	streamList = parent.selectStreamService("twitch.tv").getStreamList();

	if (streamList.size() > 0) {
	    if (parent.currentStreamService.equals(parent.selectStreamService("twitch.tv").getUrl())) {
		parent.updateProgressBar.setValue(0);
		parent.updateToolBar.setVisible(true);
		int max = parent.selectStreamService("twitch.tv").getStreamList().size();
		parent.updateProgressBar.setMaximum(max);
	    }

	    for (int i = 0; i < streamList.size(); i++) {
		threads.add(new Thread(new TwitchUpdateWorker(i, streamList, this)));
	    }
	    if (parent.globals._DEBUG)
		System.out.println("added " + streamList.size() + " new threads");
	    for (int i = 0; i < streamList.size(); i++) {
		threads.get(i).start();
	    }
	    if (parent.globals._DEBUG)
		System.out.println("started " + streamList.size() + " threads");
	    for (int i = 0; i < streamList.size(); i++) {
		try {
		    threads.get(i).join();
		    parent.updateProgressBar.setValue(finishedUpdates.intValue());
		} catch (InterruptedException e) {
		    if (parent.globals._DEBUG)
			e.printStackTrace();
		}
	    }
	    if (parent.globals._DEBUG)
		System.out.println(streamList.size() + " threads were joined");
	    for (int i = 0; i < parent.streamListModel.size(); i++) {
		parent.streamListModel.setElementAt(parent.streamListModel.get(i), i);
	    }
	}

	if (parent.currentStreamService.equals(parent.selectStreamService("twitch.tv").getUrl())) {
	    finishedUpdates.set(0);
	    parent.updateProgressBar.setValue(0);
	    parent.updateToolBar.setVisible(false);
	    ;
	}
	if (!parent.globals.currentStreamName.equals("")) {
	    if (parent.currentStreamService.equals(parent.selectStreamService("twitch.tv").getUrl())) {
		for (int i = 0; i < streamList.size(); i++) {
		    TwitchStream ts = (TwitchStream) streamList.get(i);
		    if (ts.getChannel().equals(parent.globals.currentStreamName)) {
			if (ts.isOnline()) {
			    parent.onlineStatus.setText(ts.getOnlineString());
			    parent.setPreviewImage(ts.getPreview());
			} else {
			    parent.onlineStatus.setText("Stream is Offline");
			}
		    }
		}
	    } else {
		parent.onlineStatus.setText("");
	    }
	}

	settingsPane.setKBLabel(parent.globals.downloadedBytes / 1000 + "");
	parent.globals.downloadedBytes = 0;
	parent.canUpdate = true;
	if (parent.globals.sortTwitch) {
	    EventQueue.invokeLater(new Runnable() {
		@Override
		public void run() {
		    parent.updateList();
		}
	    });

	}
    }
}
