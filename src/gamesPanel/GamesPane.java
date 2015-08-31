package gamesPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import twitchAPI.Twitch_List_Json;
import twitchlsgui.Main_GUI;

import com.google.gson.JsonObject;

public class GamesPane extends JPanel {

    private static final long serialVersionUID = 1L;

    Main_GUI parent;
    private JsonObject gamesJSON;
    JScrollPane scrollPane;
    ScrollableJPanel panel;
    public ArrayList<Twitch_List_Json> games;
    ImageThread it;
    public int size = 100;

    TwitchDirectory tDir;
    JPanel scrollView;

    public void activate() {
	tDir.home();
    }

    public GamesPane(Main_GUI parent) {
	this.parent = parent;
	it = new ImageThread(this);

	tDir = new TwitchDirectory(this);
	setLayout(new BorderLayout(0, 0));

	JToolBar twitchDirToolbar = new JToolBar();
	twitchDirToolbar.setFloatable(false);
	add(twitchDirToolbar, BorderLayout.NORTH);

	JButton refreshButton = new JButton("Refresh");
	refreshButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		tDir.refresh();
	    }
	});

	JButton homeButton = new JButton("Home");
	homeButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		tDir.home();
	    }
	});
	twitchDirToolbar.add(homeButton);
	twitchDirToolbar.add(refreshButton);

	JPanel twitchDirPanel = new JPanel();
	add(twitchDirPanel, BorderLayout.CENTER);
	twitchDirPanel.setLayout(new BorderLayout(0, 0));

	scrollView = new JPanel();
	scrollView.setLayout(new GridLayout(0, 4, 0, 0));
	scrollPane = new JScrollPane(scrollView);
	scrollPane
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scrollPane
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.getVerticalScrollBar().setUnitIncrement(30);
	scrollPane.setViewportBorder(new LineBorder(Color.MAGENTA));
	twitchDirPanel.add(scrollPane);

	scrollPane.revalidate();
	gamesJSON = this.parent.globals.twitchAPI.getGames();
	size = gamesJSON.get("top").getAsJsonArray().size();
	games = new ArrayList<Twitch_List_Json>();

	for (int i = 0; i < size; i++) {
	    Twitch_List_Json a = new Twitch_List_Json();
	    a.load(gamesJSON.get("top").getAsJsonArray().get(i)
		    .getAsJsonObject());
	    games.add(a);
	}
	games.sort(new GamesComparator());
    }

    void openStream(final String name) {
	parent.OpenStream(name, parent.globals.currentQuality);
    }
}
