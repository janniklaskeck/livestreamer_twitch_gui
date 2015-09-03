package twitchUpdate;

import java.util.ArrayList;

import stream.GenericStreamInterface;
import stream.TwitchStream;

/**
 * Simple runnable to refresh all streams from a streamList
 * 
 * @author Niklas 21.01.2015
 * 
 */
class TwitchUpdateWorker implements Runnable {
    private int index = -1;
    private ArrayList<GenericStreamInterface> streamList;
    private TwitchUpdateThread parent;

    public TwitchUpdateWorker(int i, ArrayList<GenericStreamInterface> streamList, TwitchUpdateThread parent) {
	index = i;
	this.streamList = streamList;
	this.parent = parent;
    }

    @Override
    public void run() {
	((TwitchStream) streamList.get(index)).refresh();
	parent.parent.incProgress();
    }
}
