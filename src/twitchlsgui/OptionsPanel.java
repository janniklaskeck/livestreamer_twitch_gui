package twitchlsgui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class OptionsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField timeIntervalTextField;
    private JCheckBox showPreviewCheckBox;
    private JCheckBox autoUpdateCheckBox;
    private JLabel timeIntervalLabel;
    public static JLabel KBLabel;
    private JButton saveSettingsButton;

    public OptionsPanel() {
	setBorder(new EmptyBorder(5, 5, 5, 5));
	GridBagLayout gridBagLayout = new GridBagLayout();
	gridBagLayout.columnWidths = new int[] { 290, 150 };
	gridBagLayout.rowHeights = new int[] { 20, 20, 20, 20, 20, 0, 0 };
	gridBagLayout.columnWeights = new double[] { 0.0, 0.0 };
	gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		0.0 };
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

	JCheckBox unusedCheckBox = new JCheckBox("Unused");
	GridBagConstraints gbc_unusedCheckBox = new GridBagConstraints();
	gbc_unusedCheckBox.fill = GridBagConstraints.BOTH;
	gbc_unusedCheckBox.insets = new Insets(0, 0, 5, 5);
	gbc_unusedCheckBox.gridx = 0;
	gbc_unusedCheckBox.gridy = 4;
	add(unusedCheckBox, gbc_unusedCheckBox);

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
	GridBagConstraints gbc_saveSettingsButton = new GridBagConstraints();
	gbc_saveSettingsButton.insets = new Insets(0, 0, 0, 5);
	gbc_saveSettingsButton.gridx = 0;
	gbc_saveSettingsButton.gridy = 6;
	add(saveSettingsButton, gbc_saveSettingsButton);
    }

}
