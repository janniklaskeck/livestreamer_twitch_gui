package gamesPanel.channel;

import gamesPanel.GamesPane;

import java.util.ArrayList;

public class ChannelThread {

    private ArrayList<ChannelRunnable> threads;
    private ArrayList<Thread> t;
    private GamesPane parent;

    public ChannelThread(GamesPane parent) {
	threads = new ArrayList<ChannelRunnable>();
	t = new ArrayList<Thread>();
	this.parent = parent;
    }

    public void load(int size) {
	parent.setProgressBar("channel");
	parent.progressBar.setValue(0);
	parent.progressBar.setVisible(true);

	for (int i = 0; i < size; i++) {
	    threads.add(new ChannelRunnable(parent, i));
	}

	for (int i = 0; i < size; i++) {
	    t.add(new Thread(threads.get(i)));
	    t.get(i).start();
	}

	for (int i = 0; i < size; i++) {
	    try {
		t.get(i).join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	for (int i = 0; i < size; i++) {
	    threads.get(i).addButton();
	}
	t.clear();
	parent.progress.set(0);
	parent.progressBar.setVisible(false);
	parent.revalidate();
    }
}
