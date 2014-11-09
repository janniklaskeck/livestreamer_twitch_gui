package twitchlsgui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

public class MyListCellRenderer implements ListCellRenderer<JLabel> {

    private Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
    private Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    private Color bg = null;

    @Override
    public Component getListCellRendererComponent(JList<? extends JLabel> list,
	    JLabel value, int index, boolean isSelected, boolean cellHasFocus) {
	value.setOpaque(true);
	value.setFont(new Font("Dialog", Font.BOLD, 12));
	if (bg == null) {
	    bg = value.getBackground();
	}
	for (TwitchStream ts : Functions.streamList) {
	    if (ts.getChannel().equals(value.getText())) {
		if (ts.isOnline()) {
		    if (isSelected) {
			value.setBorder(lineBorder);
		    } else {
			value.setBorder(emptyBorder);
		    }
		    value.setBackground(Color.GREEN);
		} else {
		    value.setBorder(emptyBorder);
		    value.setBackground(bg);
		}
	    }
	}
	return value;
    }
}
