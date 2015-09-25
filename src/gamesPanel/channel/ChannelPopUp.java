package gamesPanel.channel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ChannelPopUp extends JPopupMenu {

    private static final long serialVersionUID = 1L;

    public ChannelPopUp(final TwitchChannel parent, final String channel) {
	add(new JMenuItem(new AbstractAction("Add to List") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		parent.parent.parent.parent.addStream(channel, "twitch.tv");
	    }
	}));
    }
}
