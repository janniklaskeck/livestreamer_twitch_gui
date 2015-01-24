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

import org.jibble.pircbot.IrcException;

import twitchlsgui.Main_GUI;

public class IRCClientFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField messageTextField;
    private IRCClient ircClient;
    private JTextPane chatTextPane;

    public IRCClientFrame() {
	setTitle("irc client test");
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
		    ircClient.joinChannel("#" + Main_GUI.currentStreamName);
		} else {
		    // TODO display Error
		    System.out
			    .println("Error while connecting. Not enough Information");
		}
	    }
	});

	chatTextPane = new JTextPane();
	chatTextPane.setSize(500, 400);

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
	    chatTextPane.getDocument().insertString(
		    chatTextPane.getDocument().getLength(),
		    "<" + sender + ">: ", null);

	    offset = chatTextPane.getDocument().getLength() - 3
		    - sender.length();
	    length = 3 + sender.length();

	    chatTextPane.getStyledDocument().setCharacterAttributes(offset,
		    length, sas, false);

	    chatTextPane.getDocument().insertString(
		    chatTextPane.getDocument().getLength(), message + "\n",
		    null);
	} catch (BadLocationException e) {
	    e.printStackTrace();
	}
	chatTextPane.setCaretPosition(chatTextPane.getDocument().getLength());
    }
}
