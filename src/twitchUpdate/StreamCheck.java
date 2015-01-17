package twitchlsgui;

import java.util.ArrayList;

/**
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class StreamCheck implements Runnable {

    private ArrayList<Thread> threads = new ArrayList<Thread>();
    private ArrayList<GenericStream> streamList;

    @Override
    public void run() {
	while (true) {
	    streamList = Main_GUI.selectStreamService("twitch.tv")
		    .getStreamList();

	    if (streamList.size() > 0) {
		if (Main_GUI.currentStreamService.equals(Main_GUI
			.selectStreamService("twitch.tv").getUrl())) {
		    Main_GUI.updateStatus.setText("Updating");
		} else {
		    Main_GUI.updateStatus.setText("");
		}

		for (int i = 0; i < streamList.size(); i++) {
		    threads.add(new Thread(new CheckThread(i)));
		}
		for (int i = 0; i < streamList.size(); i++) {
		    threads.get(i).start();
		}
		for (int i = 0; i < streamList.size(); i++) {
		    try {
			threads.get(i).join();
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		}
		for (int i = 0; i < Main_GUI.streamListModel.getSize(); i++) {
		    Main_GUI.streamListModel.setElementAt(
			    Main_GUI.streamListModel.getElementAt(i), i);
		}
	    }

	    if (Main_GUI.currentStreamService.equals(Main_GUI
		    .selectStreamService("twitch.tv").getUrl())) {
		Main_GUI.updateStatus.setText("Finished updating");
	    } else {
		Main_GUI.updateStatus.setText("");
	    }
	    if (Main_GUI.currentStreamName != "") {
		if (Main_GUI.currentStreamService.equals(Main_GUI
			.selectStreamService("twitch.tv").getUrl())) {
		    for (int i = 0; i < streamList.size(); i++) {
			TwitchStream ts = (TwitchStream) streamList.get(i);
			if (ts.getChannel().equals(Main_GUI.currentStreamName)) {
			    if (ts.isOnline()) {
				Main_GUI.onlineStatus.setText(ts
					.getOnlineString());
				Main_GUI.setPreviewImage(ts.getPreview());
			    } else {
				Main_GUI.onlineStatus
					.setText("Stream is Offline");
			    }
			}
		    }
		} else {
		    Main_GUI.onlineStatus.setText("");
		}

	    }
	    threads = new ArrayList<Thread>();

	    try {
		Thread.sleep(Main_GUI.checkTimer * 1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    private class CheckThread implements Runnable {
	private int index = -1;

	public CheckThread(int i) {
	    index = i;
	}

	@Override
	public void run() {
	    ((TwitchStream) streamList.get(index)).refresh();
	}
    }
}
