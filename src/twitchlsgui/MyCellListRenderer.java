package twitchlsgui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class MyCellListRenderer extends DefaultListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Color bg = null;

    @Override
    public Component getListCellRendererComponent(JList<?> paramlist,
	    Object value, int index, boolean isSelected, boolean cellHasFocus) {

	Component c = super.getListCellRendererComponent(paramlist, value,
		index, isSelected, cellHasFocus);
	if (bg == null) {
	    bg = c.getBackground();
	}
	for (TwitchStream ts : Functions.streamList) {
	    if (ts.channel.equals((String) value)) {
		if (ts.isOnline()) {
		    c.setBackground(Color.GREEN);
		} else {
		    c.setBackground(bg);
		}
	    }
	}
	
	return c;
    }

}
