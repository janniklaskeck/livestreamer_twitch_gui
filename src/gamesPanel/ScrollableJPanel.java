package gamesPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class ScrollableJPanel extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;
    private GridBagLayout gbLayout;

    public ScrollableJPanel() {
	gbLayout = new GridBagLayout();
	gbLayout.columnWidths = new int[] { 70, 70, 70, 70, 70, 70, 70, 70 };
	gbLayout.rowHeights = new int[] { 75, 75, 75, 75, 75, 75, 75, 75, 75,
		75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75, 75,
		75, 75, 75, 75, 75, 75, 75 };

	setLayout(gbLayout);
    }

    /**
     * 
     * @param label
     * @param c
     */
    public void addToPanel(JLabel label, GridBagConstraints c) {
	gbLayout.setConstraints(label, c);
	add(label);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
	return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
	    int orientation, int direction) {
	return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
	    int orientation, int direction) {
	return ((orientation == SwingConstants.VERTICAL) ? visibleRect.height
		: visibleRect.width) - 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
	return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
	return false;
    }

}
