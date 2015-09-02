package gamesPanel.channel;

import gamesPanel.GamesPane;

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

    public void loadImages() {
	parent.progressBar.setVisible(true);
	parent.setProgressBar("game");
	for (int i = 0; i < parent.size; i++) {
	    threads.add(new GameRunnable(parent, i));
	}

	for (int i = 0; i < parent.size; i++) {
	    t.add(new Thread(threads.get(i)));
	    t.get(i).start();
	}

	for (int i = 0; i < parent.size; i++) {
	    try {
		t.get(i).join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	for (int i = 0; i < parent.size; i++) {
	    threads.get(i).addImage();
	}
	t.clear();
	parent.progress.set(0);
	parent.progressBar.setVisible(false);
	parent.revalidate();
	parent.repaint();
    }

}
