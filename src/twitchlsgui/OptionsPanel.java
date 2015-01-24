package twitchlsgui;

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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * @author Niklas 21.01.2015
 * 
 */
public class OptionsPanel extends JPanel {

    private static final String versionURL = "https://raw.githubusercontent.com/westerwave/livestreamer_twitch_gui/master/VERSION";
    private static final String downloadURL = "https://github.com/westerwave/livestreamer_twitch_gui/releases/tag/v1.x";
    private static final long serialVersionUID = 1L;
    private JTextField timeIntervalTextField;
    private JCheckBox showPreviewCheckBox;
    private JCheckBox autoUpdateCheckBox;
    private JLabel timeIntervalLabel;
    public static JLabel KBLabel;
    private JButton saveSettingsButton;
    private JButton exportButton;
    private JButton importButton;
    private JCheckBox debugCheckBox;
    private JLabel lblCurrentVersion;
    private JLabel lblNewVersion;
    private JButton twitchCredentialsButton;

    public OptionsPanel() {
	setBorder(new EmptyBorder(5, 5, 5, 5));
	GridBagLayout gridBagLayout = new GridBagLayout();
	gridBagLayout.columnWidths = new int[] { 290, 150 };
	gridBagLayout.rowHeights = new int[] { 20, 20, 20, 20, 20, 0, 0, 0, 0,
		0 };
	gridBagLayout.columnWeights = new double[] { 0.0, 0.0 };
	gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		0.0, 0.0, 0.0, 0.0 };
	setLayout(gridBagLayout);

	showPreviewCheckBox = new JCheckBox("Download Preview Image");
	showPreviewCheckBox.setSelected(Main_GUI.showPreview);
	showPreviewCheckBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		Main_GUI.showPreview = showPreviewCheckBox.isSelected();

	    }
	});
	GridBagConstraints gbc_showPreviewCheckBox = new GridBagConstraints();
	gbc_showPreviewCheckBox.fill = GridBagConstraints.BOTH;
	gbc_showPreviewCheckBox.insets = new Insets(0, 0, 5, 5);
	gbc_showPreviewCheckBox.gridx = 0;
	gbc_showPreviewCheckBox.gridy = 1;
	add(showPreviewCheckBox, gbc_showPreviewCheckBox);

	autoUpdateCheckBox = new JCheckBox(
		"Automatically update Streams from Twitch");
	autoUpdateCheckBox.setSelected(Main_GUI.autoUpdate);
	autoUpdateCheckBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		Main_GUI.autoUpdate = autoUpdateCheckBox.isSelected();

	    }
	});
	GridBagConstraints gbc_autoUpdateCheckBox = new GridBagConstraints();
	gbc_autoUpdateCheckBox.fill = GridBagConstraints.BOTH;
	gbc_autoUpdateCheckBox.insets = new Insets(0, 0, 5, 5);
	gbc_autoUpdateCheckBox.gridx = 0;
	gbc_autoUpdateCheckBox.gridy = 2;
	add(autoUpdateCheckBox, gbc_autoUpdateCheckBox);

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
	timeIntervalTextField.setText(Main_GUI.checkTimer + "");
	timeIntervalTextField.getDocument().addDocumentListener(
		new DocumentListener() {

		    @Override
		    public void removeUpdate(DocumentEvent arg0) {
			if (timeIntervalTextField.getText().length() > 0) {
			    try {
				Main_GUI.checkTimer = Integer
					.parseInt(timeIntervalTextField
						.getText());
			    } catch (NumberFormatException e) {
				e.printStackTrace();
			    }
			    if (Main_GUI.checkTimer < 20) {
				Main_GUI.checkTimer = 20;
			    }
			}
		    }

		    @Override
		    public void insertUpdate(DocumentEvent arg0) {
			if (timeIntervalTextField.getText().length() > 0) {
			    try {
				Main_GUI.checkTimer = Integer
					.parseInt(timeIntervalTextField
						.getText());
			    } catch (NumberFormatException e) {
				e.printStackTrace();
			    }
			    if (Main_GUI.checkTimer < 20) {
				Main_GUI.checkTimer = 20;
			    }
			}
		    }

		    @Override
		    public void changedUpdate(DocumentEvent arg0) {
			if (timeIntervalTextField.getText().length() > 0) {
			    try {
				Main_GUI.checkTimer = Integer
					.parseInt(timeIntervalTextField
						.getText());
			    } catch (NumberFormatException e) {
				e.printStackTrace();
			    }
			    if (Main_GUI.checkTimer < 20) {
				Main_GUI.checkTimer = 20;
			    }
			}
		    }
		});
	GridBagConstraints gbc_timeIntervalTextField = new GridBagConstraints();
	gbc_timeIntervalTextField.fill = GridBagConstraints.BOTH;
	gbc_timeIntervalTextField.insets = new Insets(0, 0, 5, 0);
	gbc_timeIntervalTextField.gridx = 1;
	gbc_timeIntervalTextField.gridy = 3;
	add(timeIntervalTextField, gbc_timeIntervalTextField);
	timeIntervalTextField.setColumns(1);

	debugCheckBox = new JCheckBox("Enable Debug Output");
	debugCheckBox.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		Main_GUI._DEBUG = debugCheckBox.isSelected();
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
	gbc_KBLabel.insets = new Insets(0, 0, 5, 0);
	gbc_KBLabel.gridx = 1;
	gbc_KBLabel.gridy = 5;
	add(KBLabel, gbc_KBLabel);

	saveSettingsButton = new JButton("Save Settings");
	saveSettingsButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		Main_GUI.cfgUtil.writeConfig();

	    }
	});

	importButton = new JButton("Import Streams");
	importButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		Main_GUI.cfgUtil.importStreams();
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
		    Main_GUI.twitchUser = twitchUser.getText().trim();
		    Main_GUI.twitchOAuth = twitchOAuth.getText().trim();
		}
	    }

	});
	GridBagConstraints gbc_twitchCredentialsButton = new GridBagConstraints();
	gbc_twitchCredentialsButton.insets = new Insets(0, 0, 5, 5);
	gbc_twitchCredentialsButton.gridx = 0;
	gbc_twitchCredentialsButton.gridy = 6;
	add(twitchCredentialsButton, gbc_twitchCredentialsButton);
	GridBagConstraints gbc_importButton = new GridBagConstraints();
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
		Main_GUI.cfgUtil.exportStreams();
	    }
	});
	GridBagConstraints gbc_exportButton = new GridBagConstraints();
	gbc_exportButton.insets = new Insets(0, 0, 5, 0);
	gbc_exportButton.gridx = 1;
	gbc_exportButton.gridy = 7;
	add(exportButton, gbc_exportButton);

	lblCurrentVersion = new JLabel("Current Version: "
		+ Main_GUI.VERSION.asString());
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
			if (Main_GUI._DEBUG)
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
	    if (Main_GUI.VERSION.isNewerVersion(version)) {
		return true;
	    }
	} catch (IOException e) {
	    if (Main_GUI._DEBUG)
		e.printStackTrace();
	}
	return false;
    }
}
