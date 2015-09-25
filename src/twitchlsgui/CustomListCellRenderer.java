package twitchlsgui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import stream.TwitchStream;

/**
 * Custom CellRenderer to color and frame streams
 * 
 * @author Niklas 21.01.2015
 * 
 */
public class CustomListCellRenderer implements ListCellRenderer<JLabel> {

    private Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
    private Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    private Color bg = null;
    private Main_GUI parent;

    public CustomListCellRenderer(Main_GUI parent) {
	this.parent = parent;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends JLabel> list, JLabel value, int index,
	    boolean isSelected, boolean cellHasFocus) {
	value.setOpaque(true);
	value.setFont(new Font("Dialog", Font.BOLD, 12));
	if (bg == null) {
	    bg = value.getBackground();
	}
	if (parent.globals.currentStreamService.equals("twitch.tv")) {
	    for (int i = 0; i < parent.selectStreamService(parent.globals.currentStreamService).getStreamList()
		    .size(); i++) {
		TwitchStream ts = (TwitchStream) parent.selectStreamService(parent.globals.currentStreamService)
			.getStreamList().get(i);
		if (ts.getChannel().equals(value.getText())) {
		    if (isSelected) {
			value.setBorder(lineBorder);
		    } else {
			value.setBorder(emptyBorder);
		    }
		    if (ts.isOnline()) {
			value.setBackground(Color.GREEN);
		    } else {
			value.setBackground(bg);
		    }
		}
	    }
	} else {
	    if (isSelected) {
		value.setBorder(lineBorder);
	    } else {
		value.setBorder(emptyBorder);
	    }
	}

	return value;
    }
}
