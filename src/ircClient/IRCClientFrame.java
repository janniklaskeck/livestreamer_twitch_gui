package ircClient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.jibble.pircbot.IrcException;

import twitchlsgui.Main_GUI;

public class IRCClientFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField messageTextField;
    private IRCClient ircClient;
    private JTextPane chatTextPane;
    private HTMLEditorKit kit;
    private HTMLDocument doc;
    private String channel;

    public IRCClientFrame() {
	this.channel = Main_GUI.currentStreamName;
	setTitle("Twitch Chat - " + Main_GUI.currentStreamName);
	setBounds(50, 50, 500, 400);
	setResizable(true);
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	ircClient = new IRCClient(this);
	ircClient.setVerbose(false);

	JPanel inputPanel = new JPanel();
	getContentPane().add(inputPanel, BorderLayout.SOUTH);
	inputPanel.setLayout(new BorderLayout(0, 0));

	messageTextField = new JTextField();
	inputPanel.add(messageTextField, BorderLayout.CENTER);
	messageTextField.setColumns(10);

	JButton sendButton = new JButton("Send Message");
	sendButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		ircClient.sendMessage("#" + channel, messageTextField.getText());
		addMessage(Main_GUI.twitchUser, messageTextField.getText());
		messageTextField.setText("");

	    }
	});
	inputPanel.add(sendButton, BorderLayout.EAST);

	JButton connectButton = new JButton("Connect");
	inputPanel.add(connectButton, BorderLayout.WEST);
	connectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent event) {
		if (Main_GUI.twitchUser != "" && Main_GUI.twitchOAuth != ""
			&& Main_GUI.currentStreamName != "") {
		    ircClient.setUserName(Main_GUI.twitchUser);
		    try {
			ircClient.connect("irc.twitch.tv", 6667,
				Main_GUI.twitchOAuth);
		    } catch (IOException | IrcException e) {
			if (Main_GUI._DEBUG)
			    e.printStackTrace();
		    }
		    ircClient.joinChannel("#" + channel);
		    System.out.println("connected");
		} else {
		    // TODO display Error
		    System.out
			    .println("Error while connecting. Not enough Information");
		}
	    }
	});

	chatTextPane = new JTextPane();
	chatTextPane.setSize(500, 400);
	chatTextPane.setContentType("text/html");
	chatTextPane.setText("");
	kit = new HTMLEditorKit();
	doc = new HTMLDocument();
	chatTextPane.setEditorKit(kit);
	chatTextPane.setDocument(doc);
	JScrollPane scrollPane = new JScrollPane(chatTextPane);
	getContentPane().add(scrollPane, BorderLayout.CENTER);
	scrollPane.setBounds(0, 0, 500, 400);

    }

    int offset = 0;
    int length = 0;

    public void addMessage(String sender, String message) {
	SimpleAttributeSet sas = new SimpleAttributeSet();
	StyleConstants.setBold(sas, true);
	try {
	    kit.insertHTML(doc, doc.getLength(), "<b>[" + sender + "]:</b> "
		    + message + "<br>", 0, 0, HTML.Tag.B);

	} catch (IOException | BadLocationException e) {
	    e.printStackTrace();
	}
	chatTextPane.setCaretPosition(chatTextPane.getDocument().getLength());
    }

}
