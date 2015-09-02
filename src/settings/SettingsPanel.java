package settings;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import twitchlsgui.Main_GUI;

/**
 * 
 * @author Niklas 21.01.2015
 * 
 */
public class SettingsPanel extends JPanel {

    private static final String versionURL = "https://raw.githubusercontent.com/westerwave/livestreamer_twitch_gui/master/VERSION";
    private static final String downloadURL = "https://github.com/westerwave/livestreamer_twitch_gui/releases";
    private static final long serialVersionUID = 1L;
    private JTextField timeIntervalTextField;
    private JCheckBox showPreviewCheckBox;
    private JCheckBox autoUpdateCheckBox;
    private JLabel timeIntervalLabel;
    private JLabel KBLabel;
    private JButton saveSettingsButton;
    private JButton exportButton;
    private JButton importButton;
    private JCheckBox debugCheckBox;
    private JLabel lblCurrentVersion;
    private JLabel lblNewVersion;
    private JButton twitchCredentialsButton;
    private JCheckBox showOnlineTwitchCheckBox;
    private Main_GUI parent;
    private JComboBox<String> lookAndFeelComboBox;
    private JComboBox<Integer> maxChannelsLoad;
    private JComboBox<Integer> maxGamesLoad;
    private JLabel lblMaxChannelLoad;
    private JLabel lblMaxGamesLoad;
    private JLabel lblLookandfeel;

    public SettingsPanel(Main_GUI parentGUI) {
	this.parent = parentGUI;
	setBorder(new EmptyBorder(5, 5, 5, 5));
	GridBagLayout gridBagLayout = new GridBagLayout();
	gridBagLayout.columnWidths = new int[] { 290, 75, 75 };
	gridBagLayout.rowHeights = new int[] { 20, 20, 20, 20, 20 };
	gridBagLayout.columnWeights = new double[] { 0.0, 1.0 };
	gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		0.0, 0.0, 0.0, 0.0 };
	setLayout(gridBagLayout);

	showPreviewCheckBox = new JCheckBox("Download Preview Image");
	showPreviewCheckBox.setSelected(parent.globals.showPreview);
	showPreviewCheckBox.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		parent.globals.showPreview = showPreviewCheckBox.isSelected();
	    }
	});

	showOnlineTwitchCheckBox = new JCheckBox(
		"Show online Twitch streams first");
	showOnlineTwitchCheckBox.setSelected(parent.globals.sortTwitch);
	showOnlineTwitchCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
	showOnlineTwitchCheckBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		parent.globals.sortTwitch = showOnlineTwitchCheckBox
			.isSelected();

	    }
	});

	GridBagConstraints gbc_ShowOnlineTwitchCheckBox = new GridBagConstraints();
	gbc_ShowOnlineTwitchCheckBox.fill = GridBagConstraints.BOTH;
	gbc_ShowOnlineTwitchCheckBox.insets = new Insets(0, 0, 5, 5);
	gbc_ShowOnlineTwitchCheckBox.gridx = 0;
	gbc_ShowOnlineTwitchCheckBox.gridy = 0;
	add(showOnlineTwitchCheckBox, gbc_ShowOnlineTwitchCheckBox);

	lookAndFeelComboBox = new JComboBox<String>();
	lookAndFeelComboBox.setSelectedItem(parent.globals.lookAndFeel);
	lookAndFeelComboBox.setModel(new DefaultComboBoxModel<String>(
		new String[] { "system", "cross-Platform" }));
	lookAndFeelComboBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String currentLaF = (String) lookAndFeelComboBox
			.getSelectedItem();
		if (currentLaF == "system") {
		    try {
			UIManager.setLookAndFeel(UIManager
				.getSystemLookAndFeelClassName());
		    } catch (ClassNotFoundException | InstantiationException
			    | IllegalAccessException
			    | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		    }
		    SwingUtilities.updateComponentTreeUI(parent);
		    parent.pack();
		} else if (currentLaF == "cross-Platform") {
		    try {
			UIManager.setLookAndFeel(UIManager
				.getCrossPlatformLookAndFeelClassName());
		    } catch (ClassNotFoundException | InstantiationException
			    | IllegalAccessException
			    | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		    }
		    SwingUtilities.updateComponentTreeUI(parent);
		    parent.pack();
		}
	    }
	});

	this.lblLookandfeel = new JLabel("LookAndFeel");
	GridBagConstraints gbc_lblLookandfeel = new GridBagConstraints();
	gbc_lblLookandfeel.insets = new Insets(0, 0, 5, 5);
	gbc_lblLookandfeel.anchor = GridBagConstraints.EAST;
	gbc_lblLookandfeel.gridx = 1;
	gbc_lblLookandfeel.gridy = 0;
	add(this.lblLookandfeel, gbc_lblLookandfeel);
	GridBagConstraints gbc_lookAndFeelComboBox = new GridBagConstraints();
	gbc_lookAndFeelComboBox.insets = new Insets(0, 0, 5, 0);
	gbc_lookAndFeelComboBox.fill = GridBagConstraints.HORIZONTAL;
	gbc_lookAndFeelComboBox.gridx = 2;
	gbc_lookAndFeelComboBox.gridy = 0;
	add(lookAndFeelComboBox, gbc_lookAndFeelComboBox);
	GridBagConstraints gbc_showPreviewCheckBox = new GridBagConstraints();
	gbc_showPreviewCheckBox.fill = GridBagConstraints.BOTH;
	gbc_showPreviewCheckBox.insets = new Insets(0, 0, 5, 5);
	gbc_showPreviewCheckBox.gridx = 0;
	gbc_showPreviewCheckBox.gridy = 1;
	add(showPreviewCheckBox, gbc_showPreviewCheckBox);

	autoUpdateCheckBox = new JCheckBox(
		"Automatically update Streams from Twitch");
	autoUpdateCheckBox.setSelected(parent.globals.autoUpdate);
	autoUpdateCheckBox.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		parent.globals.autoUpdate = autoUpdateCheckBox.isSelected();
	    }
	});

	this.lblMaxGamesLoad = new JLabel("Max Games Load");
	GridBagConstraints gbc_lblMaxGamesLoad = new GridBagConstraints();
	gbc_lblMaxGamesLoad.insets = new Insets(0, 0, 5, 5);
	gbc_lblMaxGamesLoad.anchor = GridBagConstraints.EAST;
	gbc_lblMaxGamesLoad.gridx = 1;
	gbc_lblMaxGamesLoad.gridy = 1;
	add(this.lblMaxGamesLoad, gbc_lblMaxGamesLoad);

	this.maxGamesLoad = new JComboBox<Integer>();
	this.maxGamesLoad.setModel(new DefaultComboBoxModel<Integer>(
		new Integer[] { 20, 40, 60, 80, 100 }));
	this.maxGamesLoad.setMaximumRowCount(5);
	GridBagConstraints gbc_maxGamesLoad = new GridBagConstraints();
	gbc_maxGamesLoad.insets = new Insets(0, 0, 5, 0);
	gbc_maxGamesLoad.fill = GridBagConstraints.HORIZONTAL;
	gbc_maxGamesLoad.gridx = 2;
	gbc_maxGamesLoad.gridy = 1;
	maxGamesLoad.setSelectedIndex((parent.globals.maxGamesLoad - 20) / 20);
	maxGamesLoad.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		parent.globals.maxGamesLoad = (int) maxGamesLoad
			.getSelectedItem();
	    }
	});
	add(this.maxGamesLoad, gbc_maxGamesLoad);

	GridBagConstraints gbc_autoUpdateCheckBox = new GridBagConstraints();
	gbc_autoUpdateCheckBox.fill = GridBagConstraints.BOTH;
	gbc_autoUpdateCheckBox.insets = new Insets(0, 0, 5, 5);
	gbc_autoUpdateCheckBox.gridx = 0;
	gbc_autoUpdateCheckBox.gridy = 2;
	add(autoUpdateCheckBox, gbc_autoUpdateCheckBox);

	this.lblMaxChannelLoad = new JLabel("Max Channel Load");
	GridBagConstraints gbc_lblMaxChannelLoad = new GridBagConstraints();
	gbc_lblMaxChannelLoad.insets = new Insets(0, 0, 5, 5);
	gbc_lblMaxChannelLoad.anchor = GridBagConstraints.EAST;
	gbc_lblMaxChannelLoad.gridx = 1;
	gbc_lblMaxChannelLoad.gridy = 2;
	add(this.lblMaxChannelLoad, gbc_lblMaxChannelLoad);

	this.maxChannelsLoad = new JComboBox<Integer>();
	this.maxChannelsLoad.setMaximumRowCount(5);
	this.maxChannelsLoad.setModel(new DefaultComboBoxModel<Integer>(
		new Integer[] { 20, 40, 60, 80, 100 }));
	GridBagConstraints gbc_maxChannelsLoad = new GridBagConstraints();
	gbc_maxChannelsLoad.insets = new Insets(0, 0, 5, 0);
	gbc_maxChannelsLoad.fill = GridBagConstraints.HORIZONTAL;
	gbc_maxChannelsLoad.gridx = 2;
	gbc_maxChannelsLoad.gridy = 2;
	maxChannelsLoad
		.setSelectedIndex((parent.globals.maxChannelsLoad - 20) / 20);
	maxChannelsLoad.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		parent.globals.maxChannelsLoad = (int) maxChannelsLoad
			.getSelectedItem();
	    }
	});
	add(this.maxChannelsLoad, gbc_maxChannelsLoad);

	timeIntervalLabel = new JLabel(
		"Automatic Update Time Interval in seconds");
	timeIntervalLabel.setHorizontalAlignment(SwingConstants.CENTER);
	GridBagConstraints gbc_timeIntervalLabel = new GridBagConstraints();
	gbc_timeIntervalLabel.insets = new Insets(0, 0, 5, 5);
	gbc_timeIntervalLabel.anchor = GridBagConstraints.WEST;
	gbc_timeIntervalLabel.gridx = 0;
	gbc_timeIntervalLabel.gridy = 3;
	add(timeIntervalLabel, gbc_timeIntervalLabel);

	timeIntervalTextField = new JTextField();
	timeIntervalTextField.setText(parent.globals.checkTimer + "");
	timeIntervalTextField.getDocument().addDocumentListener(
		new DocumentListener() {

		    @Override
		    public void removeUpdate(DocumentEvent arg0) {
			if (timeIntervalTextField.getText().length() > 0) {
			    try {
				parent.globals.checkTimer = Integer
					.parseInt(timeIntervalTextField
						.getText());
			    } catch (NumberFormatException e) {
				e.printStackTrace();
			    }
			    if (parent.globals.checkTimer < 20) {
				parent.globals.checkTimer = 20;
			    }
			}
		    }

		    @Override
		    public void insertUpdate(DocumentEvent arg0) {
			if (timeIntervalTextField.getText().length() > 0) {
			    try {
				parent.globals.checkTimer = Integer
					.parseInt(timeIntervalTextField
						.getText());
			    } catch (NumberFormatException e) {
				e.printStackTrace();
			    }
			    if (parent.globals.checkTimer < 20) {
				parent.globals.checkTimer = 20;
			    }
			}
		    }

		    @Override
		    public void changedUpdate(DocumentEvent arg0) {
			if (timeIntervalTextField.getText().length() > 0) {
			    try {
				parent.globals.checkTimer = Integer
					.parseInt(timeIntervalTextField
						.getText());
			    } catch (NumberFormatException e) {
				e.printStackTrace();
			    }
			    if (parent.globals.checkTimer < 20) {
				parent.globals.checkTimer = 20;
			    }
			}
		    }
		});
	GridBagConstraints gbc_timeIntervalTextField = new GridBagConstraints();
	gbc_timeIntervalTextField.gridwidth = 2;
	gbc_timeIntervalTextField.fill = GridBagConstraints.BOTH;
	gbc_timeIntervalTextField.insets = new Insets(0, 0, 5, 0);
	gbc_timeIntervalTextField.gridx = 1;
	gbc_timeIntervalTextField.gridy = 3;
	add(timeIntervalTextField, gbc_timeIntervalTextField);
	timeIntervalTextField.setColumns(1);

	debugCheckBox = new JCheckBox("Enable Debug Output");
	debugCheckBox.setSelected(parent.globals._DEBUG);
	debugCheckBox.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		parent.globals._DEBUG = debugCheckBox.isSelected();
	    }
	});
	GridBagConstraints gbc_debugCheckBox = new GridBagConstraints();
	gbc_debugCheckBox.fill = GridBagConstraints.BOTH;
	gbc_debugCheckBox.insets = new Insets(0, 0, 5, 5);
	gbc_debugCheckBox.gridx = 0;
	gbc_debugCheckBox.gridy = 4;
	add(debugCheckBox, gbc_debugCheckBox);

	JLabel estKBDownloadedLabel = new JLabel(
		"Estimated kB downloaded per Twitch.tv List update: ");
	GridBagConstraints gbc_estKBDownloadedLabel = new GridBagConstraints();
	gbc_estKBDownloadedLabel.insets = new Insets(0, 0, 5, 5);
	gbc_estKBDownloadedLabel.gridx = 0;
	gbc_estKBDownloadedLabel.gridy = 5;
	add(estKBDownloadedLabel, gbc_estKBDownloadedLabel);

	KBLabel = new JLabel("0");
	GridBagConstraints gbc_KBLabel = new GridBagConstraints();
	gbc_KBLabel.gridwidth = 2;
	gbc_KBLabel.insets = new Insets(0, 0, 5, 0);
	gbc_KBLabel.gridx = 1;
	gbc_KBLabel.gridy = 5;
	add(KBLabel, gbc_KBLabel);

	saveSettingsButton = new JButton("Save Settings");
	saveSettingsButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		parent.globals.settingsManager.writeSettings();
	    }
	});

	importButton = new JButton("Import Streams");
	importButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		parent.globals.settingsManager.importStreams();
	    }
	});

	twitchCredentialsButton = new JButton(
		"Enter Twitch.tv Username and OAuth Token");
	twitchCredentialsButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		JTextField twitchUser = new JTextField(20);
		JTextField twitchOAuth = new JTextField(20);
		JPanel myPanel = new JPanel();
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(new JLabel("Twitch.tv Username:"));
		myPanel.add(twitchUser);
		myPanel.add(Box.createHorizontalStrut(15)); // a spacer
		myPanel.add(new JLabel("Twitch.tv OAuth Token:"));
		myPanel.add(twitchOAuth);
		int result = JOptionPane.showConfirmDialog(null, myPanel,
			"Please enter your Twitch.tv username and OAuth Token",
			JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
		    parent.globals.twitchUser = twitchUser.getText().trim();
		    parent.globals.twitchOAuth = twitchOAuth.getText().trim();
		}
	    }
	});
	GridBagConstraints gbc_twitchCredentialsButton = new GridBagConstraints();
	gbc_twitchCredentialsButton.insets = new Insets(0, 0, 5, 5);
	gbc_twitchCredentialsButton.gridx = 0;
	gbc_twitchCredentialsButton.gridy = 6;
	add(twitchCredentialsButton, gbc_twitchCredentialsButton);
	GridBagConstraints gbc_importButton = new GridBagConstraints();
	gbc_importButton.gridwidth = 2;
	gbc_importButton.insets = new Insets(0, 0, 5, 0);
	gbc_importButton.gridx = 1;
	gbc_importButton.gridy = 6;
	add(importButton, gbc_importButton);
	GridBagConstraints gbc_saveSettingsButton = new GridBagConstraints();
	gbc_saveSettingsButton.insets = new Insets(0, 0, 5, 5);
	gbc_saveSettingsButton.gridx = 0;
	gbc_saveSettingsButton.gridy = 7;
	add(saveSettingsButton, gbc_saveSettingsButton);

	exportButton = new JButton("Export Streams");
	exportButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		parent.globals.settingsManager.exportStreams();
	    }
	});
	GridBagConstraints gbc_exportButton = new GridBagConstraints();
	gbc_exportButton.gridwidth = 2;
	gbc_exportButton.insets = new Insets(0, 0, 5, 0);
	gbc_exportButton.gridx = 1;
	gbc_exportButton.gridy = 7;
	add(exportButton, gbc_exportButton);

	lblCurrentVersion = new JLabel("Current Version: "
		+ parent.globals.VERSION.asString());
	GridBagConstraints gbc_lblCurrentVersion = new GridBagConstraints();
	gbc_lblCurrentVersion.insets = new Insets(0, 0, 5, 5);
	gbc_lblCurrentVersion.gridx = 0;
	gbc_lblCurrentVersion.gridy = 8;
	add(lblCurrentVersion, gbc_lblCurrentVersion);

	lblNewVersion = new JLabel("No new Version available");
	GridBagConstraints gbc_lblNewVersion = new GridBagConstraints();
	gbc_lblNewVersion.insets = new Insets(0, 0, 0, 5);
	gbc_lblNewVersion.gridx = 0;
	gbc_lblNewVersion.gridy = 9;
	add(lblNewVersion, gbc_lblNewVersion);
	if (checkForNewVersion()) {
	    lblNewVersion
		    .setText("<html>New Version available!<br>Click here to open the Download Website</html>");
	    lblNewVersion.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent event) {
		    try {
			Desktop.getDesktop().browse(new URI(downloadURL));
		    } catch (URISyntaxException | IOException e) {
			if (parent.globals._DEBUG)
			    e.printStackTrace();
		    }
		}
	    });
	}
    }

    private boolean checkForNewVersion() {
	BufferedReader reader = null;
	Version version = new Version();
	try {
	    URL url = new URL(versionURL);
	    reader = new BufferedReader(new InputStreamReader(url.openStream()));
	    StringBuffer buffer = new StringBuffer();
	    int read;
	    char[] chars = new char[1024];
	    while ((read = reader.read(chars)) != -1) {
		buffer.append(chars, 0, read);
	    }
	    if (reader != null) {
		reader.close();
	    }
	    version = new Version(buffer.toString());
	    if (parent.globals.VERSION.isNewerVersion(version)) {
		return true;
	    }
	} catch (IOException e) {
	    if (parent.globals._DEBUG)
		e.printStackTrace();
	}
	return false;
    }

    public JLabel getKBLabel() {
	return KBLabel;
    }

    public void setKBLabel(String text) {
	KBLabel.setText(text);
    }
}
