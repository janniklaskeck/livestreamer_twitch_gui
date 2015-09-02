package gamesPanel.channel;

import gamesPanel.GamesPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import twitchAPI.Twitch_API;

public class GameRunnable implements Runnable {

    int i;
    private GamesPane parent;
    private BufferedImage img;

    public GameRunnable(GamesPane parent, int i) {
	this.i = i;
	this.parent = parent;
    }

    public void addImage() {
	JButton l = new JButton();
	String text = parent.games.get(i).getName();
	String viewers = parent.games.get(i).getViewers() + "";
	if (text.length() > 14) {
	    l.setText("<html>" + text.substring(0, 14) + ".. <br>Viewers: "
		    + viewers + "</html>");
	} else {
	    l.setText("<html>" + text + "<br>Viewers: " + viewers + "</html>");
	}
	l.setToolTipText(text);
	l.setIcon(new ImageIcon(img));
	l.setHorizontalTextPosition(JLabel.CENTER);
	l.setVerticalTextPosition(JLabel.BOTTOM);
	l.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		// get mouse over text
		String before = ((JButton) e.getSource()).getToolTipText();
		// special characers needed for url
		String game = before.replace(" ", "+").replace(":", "%3A");
		parent.tDir.currentGameJSon = Twitch_API.getChannels(game);
		parent.tDir.switchToGame();
	    }
	});
	parent.scrollView.add(l);
    }

    @Override
    public void run() {
	try {
	    img = ImageIO.read(new URL(parent.games.get(i)
		    .getBox_image_medium()));
	    int w = (int) (img.getWidth() * 0.7f);
	    int h = (int) (img.getHeight() * 0.7f);
	    BufferedImage after = new BufferedImage(w, h,
		    BufferedImage.TYPE_INT_ARGB);
	    AffineTransform at = new AffineTransform();
	    at.scale(0.7, 0.7);
	    AffineTransformOp scaleOp = new AffineTransformOp(at,
		    AffineTransformOp.TYPE_BILINEAR);
	    after = scaleOp.filter(img, after);
	    img = after;
	} catch (IOException e) {
	    e.printStackTrace();
	}
	parent.inc();
    }
}
