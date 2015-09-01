package gamesPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import twitchAPI.Twitch_Game_Json;

public class GameRunnable implements Runnable {

    private int index;
    private GamesPane parent;
    BufferedImage img = null;
    Twitch_Game_Json tgj;

    public GameRunnable(GamesPane parent, int index) {
	this.index = index;
	this.parent = parent;
	tgj = this.parent.tDir.channels.get(this.index);
    }

    public void addButton() {

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
	parent.tDir.jp.add(jb);
    }

    @Override
    public void run() {
	try {
	    img = ImageIO.read(new URL(tgj.getPreview_image_small()));
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

}
