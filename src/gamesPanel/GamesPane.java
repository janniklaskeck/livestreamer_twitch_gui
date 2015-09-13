package gamesPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import twitchlsgui.Main_GUI;

public class GamesPane extends JPanel {

    private static final long serialVersionUID = 1L;

    public Main_GUI parent;

    public JScrollPane scrollPane;

    public TwitchDirectory tDir;
    public JPanel scrollView;
    public JProgressBar progressBar;
    public AtomicInteger progress;

    public synchronized void inc() {
	progressBar.setValue(progress.incrementAndGet());
    }

    public void activate() {
	tDir.games_list.loadJson();
	tDir.home();
    }

    public void setProgressBar(String type) {
	int maxChannel = parent.globals.maxChannelsLoad <= tDir.channel_list.getSize() ? parent.globals.maxChannelsLoad
		: tDir.channel_list.getSize();
	int maxGame = parent.globals.maxGamesLoad <= tDir.games_list.getSize() ? parent.globals.maxGamesLoad
		: tDir.games_list.getSize();
	if (type.equals("channel")) {
	    progressBar.setMaximum(maxChannel);
	} else if (type.equals("game")) {
	    progressBar.setMaximum(maxGame);
	}
    }

    public GamesPane(Main_GUI parent) {
	this.parent = parent;

	progress = new AtomicInteger(0);

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

	progressBar = new JProgressBar(0, 20);
	twitchDirToolbar.add(progressBar);

	JPanel twitchDirPanel = new JPanel();
	add(twitchDirPanel, BorderLayout.CENTER);
	twitchDirPanel.setLayout(new BorderLayout(0, 0));

	scrollView = new JPanel();
	scrollView.setLayout(new GridLayout(0, 4, 0, 0));
	scrollPane = new JScrollPane(scrollView);
	scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.getVerticalScrollBar().setUnitIncrement(30);
	scrollPane.setViewportBorder(new LineBorder(Color.MAGENTA));
	twitchDirPanel.add(scrollPane);

	scrollPane.revalidate();

    }

    public void openStream(final String name) {
	parent.OpenStream(name, parent.globals.currentQuality);
    }
}
