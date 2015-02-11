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

    public TwitchUpdateWorker(int i, ArrayList<GenericStreamInterface> streamList) {
	index = i;
	this.streamList = streamList;
    }

    @Override
    public void run() {
	((TwitchStream) streamList.get(index)).refresh();
	TwitchUpdateThread.finishedUpdates.getAndIncrement();
    }
}
