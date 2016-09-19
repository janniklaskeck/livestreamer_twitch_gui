/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
