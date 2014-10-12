package twitchlsgui;

import java.util.ArrayList;

/**
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class StreamCheck implements Runnable {

    ArrayList<Thread> threads = new ArrayList<Thread>();
    int count = 0;

    @Override
    public void run() {
	while (true) {
	    if (Functions.streamList.size() > 0) {
		Main_GUI.onlineStatus.setText("Updating");
		for (int i = 0; i < Functions.streamList.size(); i++) {
		    threads.add(new Thread(new CheckThread(i)));
		}
		for (int i = 0; i < Functions.streamList.size(); i++) {
		    threads.get(i).start();
		    count++;
		}
		for (int i = 0; i < Functions.streamList.size(); i++) {
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
	    // System.out.println(count);
	    if (Main_GUI.currentStreamName == "") {
		Main_GUI.onlineStatus.setText("Finished updating");
	    } else {
		for (TwitchStream ts : Functions.streamList) {
		    if (ts.getChannel().equals(Main_GUI.currentStreamName)) {
			if (ts.isOnline()) {
			    Main_GUI.onlineStatus.setText(ts.getOnlineString());

			} else {
			    Main_GUI.onlineStatus.setText("Stream is Offline");
			}
		    }
		}
	    }
	    threads = new ArrayList<Thread>();

	    try {
		Thread.sleep(Functions.checkTimer * 1000);
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
	    Functions.streamList.get(index).refresh();
	}
    }
}
