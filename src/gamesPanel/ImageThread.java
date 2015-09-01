package gamesPanel;

import java.util.ArrayList;

public class ImageThread {

    private ArrayList<ImageRunnable> threads;
    private ArrayList<Thread> t;
    private GamesPane parent;

    public ImageThread(GamesPane parent) {
	threads = new ArrayList<ImageRunnable>();
	t = new ArrayList<Thread>();
	this.parent = parent;
    }

    public void loadImages() {
	parent.progressBar.setVisible(true);
	for (int i = 0; i < parent.size; i++) {
	    threads.add(new ImageRunnable(parent, i));
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
