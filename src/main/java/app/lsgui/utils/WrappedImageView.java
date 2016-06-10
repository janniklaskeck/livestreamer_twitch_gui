package app.lsgui.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class WrappedImageView extends ImageView {

    /**
     *
     * @param img
     */
    public WrappedImageView(final Image img) {
	super(img);
	setPreserveRatio(true);
    }

    @Override
    public double minWidth(final double height) {
	return 40;
    }

    @Override
    public double maxWidth(final double height) {
	return 16384;
    }

    @Override
    public double prefWidth(final double height) {
	if (getImage() == null) {
	    return minWidth(height);
	}
	return getImage().getWidth();
    }

    @Override
    public double prefHeight(final double width) {
	if (getImage() == null) {
	    return minHeight(width);
	}
	return getImage().getHeight();
    }

    @Override
    public double minHeight(final double width) {
	return 40;
    }

    @Override
    public double maxHeight(final double width) {
	return 16384;
    }

    @Override
    public boolean isResizable() {
	return true;
    }

    @Override
    public void resize(final double width, final double height) {
	setFitWidth(width);
	setFitHeight(height);
    }
}
