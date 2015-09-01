package gamesPanel;

import java.util.ArrayList;

public class GameThread {

    private ArrayList<GameRunnable> threads;
    private ArrayList<Thread> t;
    private GamesPane parent;

    public GameThread(GamesPane parent) {
	threads = new ArrayList<GameRunnable>();
	t = new ArrayList<Thread>();
	this.parent = parent;
    }

    public void load() {
	for (int i = 0; i < parent.tDir.channels.size(); i++) {
	    threads.add(new GameRunnable(parent, i));
	}

	for (int i = 0; i < parent.tDir.channels.size(); i++) {
	    t.add(new Thread(threads.get(i)));
	    t.get(i).start();
	}

	for (int i = 0; i < parent.tDir.channels.size(); i++) {
	    try {
		t.get(i).join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	for (int i = 0; i < parent.tDir.channels.size(); i++) {
	    threads.get(i).addButton();
	}
	System.out.println("finished loading channels");
	t.clear();
	parent.revalidate();
    }
}
