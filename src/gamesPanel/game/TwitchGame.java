package gamesPanel.game;

import gamesPanel.TwitchDirectory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import twitchAPI.Twitch_API;
import twitchAPI.Twitch_List_Json;

public class TwitchGame {

    private BufferedImage logo;
    private Twitch_List_Json json;
    private String name;
    private TwitchDirectory parent;

    public TwitchGame(BufferedImage logo, Twitch_List_Json json,
	    TwitchDirectory parent) {
	this.logo = logo;
	this.json = json;
	this.name = json.getName();
	this.parent = parent;
    }

    public TwitchGame(Twitch_List_Json json, TwitchDirectory parent) {
	this.logo = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	this.json = json;
	this.name = json.getName();
	this.parent = parent;
    }

    public JButton getButton() {
	JButton button = new JButton();
	String text = json.getName();
	String viewers = json.getViewers() + "";
	setLogo(logo);
	if (text.length() > 14) {
	    button.setText("<html>" + text.substring(0, 14)
		    + ".. <br>Viewers: " + viewers + "</html>");
	} else {
	    button.setText("<html>" + text + "<br>Viewers: " + viewers
		    + "</html>");
	}
	button.setToolTipText(text);
	button.setIcon(new ImageIcon(getLogo()));
	button.setHorizontalTextPosition(JLabel.CENTER);
	button.setVerticalTextPosition(JLabel.BOTTOM);
	button.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		// special characers needed for url
		String game = name.replace(" ", "+").replace(":", "%3A");
		parent.currentGameJSon = Twitch_API.getChannels(game);
		parent.switchToGame();
	    }
	});
	return button;
    }

    /**
     * @return the logo
     */
    public BufferedImage getLogo() {
	return logo;
    }

    /**
     * @param logo
     *            the logo to set
     */
    public void setLogo(BufferedImage logo) {
	this.logo = logo;
    }

    /**
     * @return the json
     */
    public Twitch_List_Json getJson() {
	return json;
    }

    /**
     * @param json
     *            the json to set
     */
    public void setJson(Twitch_List_Json json) {
	this.json = json;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

}
