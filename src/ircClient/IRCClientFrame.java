package ircClient;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import org.jibble.pircbot.IrcException;

import twitchlsgui.Main_GUI;

public class IRCClientFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField messageTextField;
    private IRCClient ircClient;
    private JTextPane chatTextPane;
    private StyledEditorKit kit;
    private StyledDocument doc;
    private String channel;
    private JScrollPane scrollPane;
    private JButton connectButton;
    private Main_GUI parent;

    public IRCClientFrame(Main_GUI parentGUI) {
	this.parent = parentGUI;
	setIconImage(Toolkit.getDefaultToolkit().getImage(IRCClientFrame.class.getResource("/assets/icon.jpg")));
	this.channel = parent.globals.currentStreamName;
	setTitle("Twitch Chat - " + parent.globals.currentStreamName);
	setBounds(50, 50, 500, 400);
	setResizable(true);
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	ircClient = new IRCClient(this);
	ircClient.setVerbose(false);

	JPanel inputPanel = new JPanel();
	getContentPane().add(inputPanel, BorderLayout.SOUTH);
	inputPanel.setLayout(new BorderLayout(0, 0));
	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
	    @Override
	    public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED && ircClient != null
			&& ircClient.isConnected() && messageTextField != null
			&& !messageTextField.getText().equals("")) {
		    ircClient.sendMessage("#" + channel, messageTextField.getText());
		    addMessage(parent.globals.twitchUser, messageTextField.getText());
		    messageTextField.setText("");
		}
		return false;
	    }
	});

	messageTextField = new JTextField();
	inputPanel.add(messageTextField, BorderLayout.CENTER);
	messageTextField.setColumns(10);

	JButton sendButton = new JButton("Send Message");
	sendButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (ircClient != null && ircClient.isConnected() && messageTextField != null
			&& !messageTextField.getText().equals("")) {
		    ircClient.sendMessage("#" + channel, messageTextField.getText());
		    addMessage(parent.globals.twitchUser, messageTextField.getText());
		    messageTextField.setText("");
		}
	    }
	});

	inputPanel.add(sendButton, BorderLayout.EAST);

	connectButton = new JButton("Connect");
	inputPanel.add(connectButton, BorderLayout.WEST);
	connectButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent event) {
		if (!ircClient.isConnected()) {

		    if (parent.globals.twitchUser.equals("") && parent.globals.twitchOAuth.equals("")
			    && parent.globals.currentStreamName.equals("")) {
			ircClient.setUserName(parent.globals.twitchUser);
			try {
			    ircClient.connect("irc.twitch.tv", 6667, parent.globals.twitchOAuth);
			} catch (IOException | IrcException e) {
			    if (parent.globals._DEBUG)
				e.printStackTrace();
			}
			ircClient.joinChannel("#" + channel);
			System.out.println("connected");
		    } else {
			// TODO display Error
			System.out.println("Error while connecting. Not enough Information");
		    }
		    connectButton.setText("Disconnect");
		} else {
		    ircClient.disconnect();
		    System.out.println("disconnected");
		    connectButton.setText("Connect");
		}
	    }
	});

	chatTextPane = new JTextPane();
	chatTextPane.setSize(500, 400);
	chatTextPane.setContentType("text/plain;charset=UTF-8");
	chatTextPane.setText("");
	chatTextPane.setEditable(false);
	kit = new StyledEditorKit();
	doc = chatTextPane.getStyledDocument();
	chatTextPane.setEditorKit(kit);
	chatTextPane.setDocument(doc);
	scrollPane = new JScrollPane(chatTextPane);
	getContentPane().add(scrollPane, BorderLayout.CENTER);
	scrollPane.setBounds(0, 0, 500, 400);

	scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

	    @Override
	    public void adjustmentValueChanged(AdjustmentEvent e) {
		final JScrollBar sb = (JScrollBar) e.getSource();
		if (sb.getValue() + sb.getVisibleAmount() == sb.getMaximum()) {
		    EventQueue.invokeLater(new Runnable() {
			public void run() {
			    EventQueue.invokeLater(new Runnable() {
				public void run() {
				    sb.setValue(sb.getMaximum());
				}
			    });
			}
		    });
		}
	    }
	});
    }

    public void addMessage(String sender, String message) {
	try {
	    MutableAttributeSet attr = new SimpleAttributeSet();
	    // set bold attribute
	    attr.addAttribute("bold", StyleConstants.Bold);
	    // insert message sender
	    doc.insertString(doc.getLength(), "<" + sender + ">: ", attr);
	    // set sender name bold
	    doc.setCharacterAttributes(doc.getLength() - sender.length() - 4, sender.length() + 4, attr, true);
	    // TODO unset bold attribute, no idea why this works
	    attr.addAttribute("bold", StyleConstants.Italic);
	    // insert message
	    doc.insertString(doc.getLength(), message, attr);
	    // new line
	    doc.insertString(doc.getLength(), System.getProperty("line.separator"), attr);
	    chatTextPane.setCaretPosition(doc.getLength());
	} catch (BadLocationException e) {
	    if (parent.globals._DEBUG)
		e.printStackTrace();
	}
    }
}
