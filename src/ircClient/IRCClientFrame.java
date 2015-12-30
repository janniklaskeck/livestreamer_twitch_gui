package ircClient;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

import org.jibble.pircbot.IrcException;

import twitchlsgui.Main_GUI;

public class IRCClientFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField messageTextField;
    private IRCClient ircClient;
    private JTextPane chatTextPane;
    private StyledEditorKit kit;
    private String currentChatHistory = "";
    private String channel;
    private JScrollPane scrollPane;
    private JButton connectButton;
    private JButton sendButton;
    private Main_GUI parent;
    private JPanel textPanel;
    private JButton changeChannelBtn;
    private JLabel fontSizeLabel;
    private JLabel fontLabel;
    private JComboBox<String> fontComboBox;
    private JComboBox<Integer> fontSizeComboBox;
    private JComboBox<String> streamListComboBox;

    public IRCClientFrame(Main_GUI parentGUI) {
	this.parent = parentGUI;
	setIconImage(Toolkit.getDefaultToolkit().getImage(IRCClientFrame.class.getResource("/assets/icon.jpg")));
	this.channel = parent.globals.currentStreamName.toLowerCase();
	setTitle("Twitch Chat - " + parent.globals.currentStreamName);
	setBounds(50, 50, 500, 400);
	setResizable(true);
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	ircClient = new IRCClient(this);
	ircClient.setVerbose(false);

	this.textPanel = new JPanel();
	this.textPanel.setBorder(new EmptyBorder(2, 3, 2, 3));
	getContentPane().add(this.textPanel, BorderLayout.NORTH);
	GridBagLayout gbl_textPanel = new GridBagLayout();
	gbl_textPanel.columnWidths = new int[] { 79, 40, 22, 44, 44, 39, 0 };
	gbl_textPanel.rowHeights = new int[] { 23, 0 };
	gbl_textPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
	gbl_textPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
	this.textPanel.setLayout(gbl_textPanel);

	this.streamListComboBox = new JComboBox<String>();
	GridBagConstraints gbc_streamListComboBox = new GridBagConstraints();
	gbc_streamListComboBox.fill = GridBagConstraints.HORIZONTAL;
	gbc_streamListComboBox.insets = new Insets(0, 0, 0, 5);
	gbc_streamListComboBox.gridx = 0;
	gbc_streamListComboBox.gridy = 0;
	this.textPanel.add(this.streamListComboBox, gbc_streamListComboBox);

	this.changeChannelBtn = new JButton("Change Channel");
	GridBagConstraints gbc_changeChannelBtn = new GridBagConstraints();
	gbc_changeChannelBtn.insets = new Insets(0, 0, 0, 5);
	gbc_changeChannelBtn.gridx = 1;
	gbc_changeChannelBtn.gridy = 0;
	this.textPanel.add(this.changeChannelBtn, gbc_changeChannelBtn);

	this.fontLabel = new JLabel("Font");
	GridBagConstraints gbc_fontLabel = new GridBagConstraints();
	gbc_fontLabel.anchor = GridBagConstraints.EAST;
	gbc_fontLabel.insets = new Insets(0, 0, 0, 5);
	gbc_fontLabel.gridx = 2;
	gbc_fontLabel.gridy = 0;
	this.textPanel.add(this.fontLabel, gbc_fontLabel);

	this.fontComboBox = new JComboBox<String>();
	this.fontComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Arial", "Tahoma", "Calibri" }));
	GridBagConstraints gbc_fontComboBox = new GridBagConstraints();
	gbc_fontComboBox.insets = new Insets(0, 0, 0, 5);
	gbc_fontComboBox.fill = GridBagConstraints.HORIZONTAL;
	gbc_fontComboBox.gridx = 3;
	gbc_fontComboBox.gridy = 0;
	this.textPanel.add(this.fontComboBox, gbc_fontComboBox);

	this.fontSizeLabel = new JLabel("Font Size");
	GridBagConstraints gbc_fontSizeLabel = new GridBagConstraints();
	gbc_fontSizeLabel.anchor = GridBagConstraints.EAST;
	gbc_fontSizeLabel.insets = new Insets(0, 0, 0, 5);
	gbc_fontSizeLabel.gridx = 4;
	gbc_fontSizeLabel.gridy = 0;
	this.textPanel.add(this.fontSizeLabel, gbc_fontSizeLabel);

	this.fontSizeComboBox = new JComboBox<Integer>();
	this.fontSizeComboBox.setSelectedIndex(3);
	DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>();
	for (int i = 1; i < 8; i++) {
	    model.addElement(i);
	}
	fontSizeComboBox.setModel(model);
	GridBagConstraints gbc_fontSizeComboBox = new GridBagConstraints();
	gbc_fontSizeComboBox.fill = GridBagConstraints.HORIZONTAL;
	gbc_fontSizeComboBox.gridx = 5;
	gbc_fontSizeComboBox.gridy = 0;
	this.textPanel.add(this.fontSizeComboBox, gbc_fontSizeComboBox);

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

	sendButton = new JButton("Send Message");
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

		    if (!parent.globals.twitchUser.equals("") && !parent.globals.twitchOAuth.equals("")
			    && !parent.globals.currentStreamName.equals("")) {
			ircClient.setUserName(parent.globals.twitchUser);
			try {
			    ircClient.connect("irc.twitch.tv", 6667, parent.globals.twitchOAuth);
			} catch (IOException | IrcException e) {
			    if (parent.globals._DEBUG)
				e.printStackTrace();
			}
			ircClient.joinChannel("#" + channel);

			sendButton.setEnabled(true);
		    } else {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			ircClient.setUserName("justinfan" + new BigInteger(uuid, 16));
			try {
			    ircClient.connect("irc.twitch.tv", 6667, "");
			} catch (IOException | IrcException e) {
			    if (parent.globals._DEBUG)
				e.printStackTrace();
			}
			ircClient.joinChannel("#" + channel);
			sendButton.setEnabled(false);
		    }
		    connectButton.setText("Disconnect");
		} else {
		    ircClient.disconnect();
		    connectButton.setText("Connect");
		}
	    }
	});

	chatTextPane = new JTextPane();
	chatTextPane.setSize(500, 400);
	chatTextPane.setContentType("text/html");
	chatTextPane.setText("");
	chatTextPane.setEditable(false);
	kit = new HTMLEditorKit();
	chatTextPane.setEditorKit(kit);
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
	// calculate some color base on sender hash
	int hash = sender.hashCode();
	int r = (hash & 0xFF0000) >> 16;
	if (r > 200) {
	    r = 200;
	}
	int g = (hash & 0x00FF00) >> 8;
	if (g > 200) {
	    g = 200;
	}
	int b = hash & 0x0000FF;
	if (b > 200) {
	    b = 200;
	}
	String colorString = "rgb(" + r + "," + g + "," + b + ")";
	currentChatHistory += "<b><font size='" + (Integer) fontSizeComboBox.getSelectedItem() + "' face='"
		+ (String) fontComboBox.getSelectedItem() + "' color='" + colorString + "'>&#60" + sender
		+ "&#62:</b></font> <font size='" + (Integer) fontSizeComboBox.getSelectedItem() + "' face='"
		+ (String) fontComboBox.getSelectedItem() + "'>" + message + "</font><br>";
	chatTextPane.setText("<html><body>" + currentChatHistory + "</body></html>");
    }
}
