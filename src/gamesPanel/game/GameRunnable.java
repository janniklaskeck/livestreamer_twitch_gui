package gamesPanel.game;

import gamesPanel.GamesPane;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class GameRunnable implements Runnable {

    int i;
    private GamesPane parent;
    private BufferedImage img;

    public GameRunnable(GamesPane parent, int i) {
	this.i = i;
	this.parent = parent;
    }

    public void addImage() {
	parent.tDir.games_list.setLogo(i, img);
	parent.scrollView.add(parent.tDir.games_list.getButton(i));
    }

    @Override
    public void run() {
	try {
	    img = ImageIO.read(new URL(parent.tDir.games_list.getJson(i)
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
