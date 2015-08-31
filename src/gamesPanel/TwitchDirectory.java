package gamesPanel;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import twitchAPI.Twitch_Game_Json;

import com.google.gson.JsonObject;

public class TwitchDirectory {

    GamesPane parent;
    Component home;
    JsonObject currentGameJSon;
    ArrayList<Twitch_Game_Json> channels;

    public TwitchDirectory(GamesPane parent) {
	this.parent = parent;
	channels = new ArrayList<Twitch_Game_Json>();
    }

    public void refresh() {
	System.out.println("refresh");
	new Thread(new Runnable() {

	    @Override
	    public void run() {
		parent.it.loadImages();
	    }

	}).start();
	home = parent.scrollView;
    }

    public void home() {
	System.out.println("home");
	channels.clear();
	if (home == null) {
	    refresh();
	} else {
	    parent.scrollPane.setViewportView(home);
	}
    }

    public void switchToGame() {
	JPanel jp = new JPanel();
	jp.setLayout(new GridLayout(0, 4, 0, 0));
	int size = currentGameJSon.get("streams").getAsJsonArray().size();
	for (int i = 0; i < size; i++) {
	    Twitch_Game_Json a = new Twitch_Game_Json();
	    a.load(currentGameJSon.get("streams").getAsJsonArray().get(i)
		    .getAsJsonObject());
	    channels.add(a);
	}

	for (Twitch_Game_Json tgj : channels) {
	    BufferedImage img = null;
	    try {
		img = ImageIO.read(new URL(tgj.getPreview_image_small()));
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    JButton jb = new JButton();
	    String text = tgj.getName();
	    String viewers = tgj.getViewers() + "";
	    jb.setIcon(new ImageIcon(img));
	    jb.setHorizontalTextPosition(JLabel.CENTER);
	    jb.setVerticalTextPosition(JLabel.BOTTOM);
	    jb.setText("<html>" + text + "<br>Viewers: " + viewers + "</html>");
	    jb.setToolTipText(tgj.getChannel());
	    jb.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    String name = ((JButton) e.getSource()).getToolTipText();
		    parent.openStream(name);
		}
	    });
	    jp.add(jb);
	}
	parent.scrollPane.setViewportView(jp);
    }
}
