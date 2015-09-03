package gamesPanel.channel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import gamesPanel.GamesPane;

public class ChannelRunnable implements Runnable {

    private int index;
    private GamesPane parent;
    BufferedImage img = null;

    public ChannelRunnable(GamesPane parent, int index) {
	this.index = index;
	this.parent = parent;

    }

    public void addButton() {
	parent.tDir.channel_list.setPreview(index, img);
	parent.tDir.channelPanel.add(parent.tDir.channel_list.getButton(index));
    }

    @Override
    public void run() {
	try {
	    img = ImageIO.read(new URL(parent.tDir.channel_list.getJson(index).getPreview_image_small()));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	parent.inc();
    }

}
