package twitchlsgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author Niklas 27.09.2014
 * 
 */
public class Main_GUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public static String currentStreamName = "";
    public static String currentQuality = "High";
    public static String customStreamName = "";

    private static ConfigUtil cfgUtil;

    private JList<String> qualityList;
    private JList<JLabel> stream_list;
    public static DefaultListModel<JLabel> streamListModel;
    private JTextField customStreamTF;
    public static JLabel onlineStatus;
    public static boolean showPreview = true;
    private static JLabel previewLabel;
    private JPanel previewPanel;
    private static Main_GUI frame;
    private JCheckBox previewCheckBox;

    private Thread checkThread;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    frame = new Main_GUI();
		    frame.setVisible(true);
		    frame.setTitle("Twitch.tv Livestreamer GUI");
		    UIManager.setLookAndFeel(UIManager
			    .getSystemLookAndFeelClassName());
		    frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			    cfgUtil.writeConfig();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		    });
		} catch (Exception e) {
		    e.printStackTrace();
		}
		setPreviewImage(null);
	    }
	});
    }

    /**
     * Updates the streamListModel of the JList
     */
    public static void updateList() {
	streamListModel.clear();
	for (int i = 0; i < Functions.streamList.size(); i++) {
	    streamListModel.addElement(new JLabel(Functions.streamList.get(i)
		    .getChannel()));
	}
    }

    /**
     * Create the frame.
     */
    public Main_GUI() {
	setIconImage(Toolkit.getDefaultToolkit().getImage(
		Main_GUI.class.getResource("/twitchlsgui/icon.jpg")));
	setResizable(false);
	cfgUtil = new ConfigUtil();

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 577, 400);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(0, 0));

	JPanel stream_panel = new JPanel();
	contentPane.add(stream_panel, BorderLayout.CENTER);

	streamListModel = new DefaultListModel<JLabel>();

	updateList();

	stream_panel.setLayout(null);

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setBounds(10, 0, 307, 185);
	stream_panel.add(scrollPane);

	stream_list = new JList<JLabel>();
	stream_list.setVisibleRowCount(10);
	stream_list.setCellRenderer(new MyListCellRenderer());

	stream_list.setModel(streamListModel);

	stream_list.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent event) {
		setStream();
	    }
	});
	stream_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	scrollPane.setViewportView(stream_list);

	JPanel custom_StreamPanel = new JPanel();
	custom_StreamPanel.setBounds(10, 196, 307, 154);
	stream_panel.add(custom_StreamPanel);
	GridBagLayout gbl_custom_StreamPanel = new GridBagLayout();
	gbl_custom_StreamPanel.columnWidths = new int[] { 30, 30, 30, 30, 30,
		30, 30, 0, 0, 0 };
	gbl_custom_StreamPanel.rowHeights = new int[] { 0, 30, 0, 0, 30, 0, 0 };
	gbl_custom_StreamPanel.columnWeights = new double[] { 0.0, 0.0, 0.0,
		0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
	gbl_custom_StreamPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
		0.0, Double.MIN_VALUE };
	custom_StreamPanel.setLayout(gbl_custom_StreamPanel);

	JLabel lblCustomStream = new JLabel("Custom Stream");
	lblCustomStream.setHorizontalAlignment(SwingConstants.CENTER);
	GridBagConstraints gbc_lblCustomStream = new GridBagConstraints();
	gbc_lblCustomStream.gridwidth = 6;
	gbc_lblCustomStream.insets = new Insets(0, 0, 5, 0);
	gbc_lblCustomStream.gridx = 2;
	gbc_lblCustomStream.gridy = 0;
	custom_StreamPanel.add(lblCustomStream, gbc_lblCustomStream);

	customStreamTF = new JTextField();
	customStreamTF.setHorizontalAlignment(SwingConstants.CENTER);
	GridBagConstraints gbc_textField = new GridBagConstraints();
	gbc_textField.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField.gridwidth = 7;
	gbc_textField.insets = new Insets(0, 0, 5, 0);
	gbc_textField.gridx = 1;
	gbc_textField.gridy = 2;

	custom_StreamPanel.add(customStreamTF, gbc_textField);
	customStreamTF.setColumns(10);
	customStreamTF.getDocument().addDocumentListener(
		new DocumentListener() {

		    @Override
		    public void removeUpdate(DocumentEvent e) {
			customStreamName = customStreamTF.getText();

		    }

		    @Override
		    public void insertUpdate(DocumentEvent e) {
			customStreamName = customStreamTF.getText();

		    }

		    @Override
		    public void changedUpdate(DocumentEvent e) {
			customStreamName = customStreamTF.getText();

		    }
		});

	onlineStatus = new JLabel("No Stream selected");
	onlineStatus.setHorizontalAlignment(SwingConstants.CENTER);

	GridBagConstraints gbc_onlineStatus = new GridBagConstraints();
	gbc_onlineStatus.gridwidth = 8;
	gbc_onlineStatus.fill = GridBagConstraints.HORIZONTAL;
	gbc_onlineStatus.insets = new Insets(0, 0, 5, 0);
	gbc_onlineStatus.gridx = 0;
	gbc_onlineStatus.gridy = 3;
	custom_StreamPanel.add(onlineStatus, gbc_onlineStatus);

	JPanel middle_panel = new JPanel();
	middle_panel.setMaximumSize(new Dimension(200, 200));
	contentPane.add(middle_panel, BorderLayout.EAST);

	GridBagConstraints gbc_middle_panel = new GridBagConstraints();
	gbc_middle_panel.fill = GridBagConstraints.NONE;
	GridBagLayout gbl_middle_panel = new GridBagLayout();
	gbl_middle_panel.setConstraints(middle_panel, gbc_middle_panel);
	gbl_middle_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
	gbl_middle_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
	gbl_middle_panel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };
	gbl_middle_panel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
	middle_panel.setLayout(gbl_middle_panel);

	JButton startStreambutton = new JButton("Start VLC Stream");
	GridBagConstraints gbc_startStreambutton = new GridBagConstraints();
	gbc_startStreambutton.insets = new Insets(0, 0, 5, 0);
	gbc_startStreambutton.gridx = 5;
	gbc_startStreambutton.gridy = 0;
	gbc_startStreambutton.fill = GridBagConstraints.BOTH;
	startStreambutton.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		Functions.OpenStream(currentStreamName, currentQuality);
	    }
	});

	JPanel panel = new JPanel();
	GridBagConstraints gbc_panel = new GridBagConstraints();
	gbc_panel.fill = GridBagConstraints.VERTICAL;
	gbc_panel.insets = new Insets(0, 0, 5, 5);
	gbc_panel.gridx = 3;
	gbc_panel.gridy = 0;
	middle_panel.add(panel, gbc_panel);
	GridBagLayout gbl_panel = new GridBagLayout();
	gbl_panel.columnWidths = new int[] { 0, 0 };
	gbl_panel.rowHeights = new int[] { 0, 0, 0 };
	gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
	gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
	panel.setLayout(gbl_panel);

	JButton addButton = new JButton("");
	addButton.setIcon(new ImageIcon(Main_GUI.class
		.getResource("/twitchlsgui/plus.png")));
	addButton.setToolTipText("Add custom Stream to List");
	addButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		cfgUtil.saveStream(customStreamTF.getText());
		checkThread.interrupt();
	    }
	});
	GridBagConstraints gbc_addButton = new GridBagConstraints();
	gbc_addButton.insets = new Insets(0, 0, 5, 0);
	gbc_addButton.gridx = 0;
	gbc_addButton.gridy = 0;
	panel.add(addButton, gbc_addButton);

	JButton removeButton = new JButton("");
	removeButton.setIcon(new ImageIcon(Main_GUI.class
		.getResource("/twitchlsgui/minus.png")));
	removeButton.setToolTipText("Remove selected Stream");
	removeButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		cfgUtil.removeStream(currentStreamName);
		checkThread.interrupt();
	    }
	});
	GridBagConstraints gbc_removeButton = new GridBagConstraints();
	gbc_removeButton.gridx = 0;
	gbc_removeButton.gridy = 1;
	panel.add(removeButton, gbc_removeButton);
	middle_panel.add(startStreambutton, gbc_startStreambutton);

	JButton startCustomStreamBtn = new JButton("Start Custom VLC Stream");
	GridBagConstraints gbc_startCustomStreamBtn = new GridBagConstraints();
	gbc_startCustomStreamBtn.insets = new Insets(0, 0, 5, 0);
	gbc_startCustomStreamBtn.gridx = 5;
	gbc_startCustomStreamBtn.gridy = 1;
	gbc_startCustomStreamBtn.fill = GridBagConstraints.BOTH;
	startCustomStreamBtn.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		if (customStreamName != "") {
		    Functions.OpenStream(customStreamTF.getText(),
			    currentQuality);
		}
	    }
	});

	qualityList = new JList<String>();
	qualityList.setModel(new AbstractListModel<String>() {

	    private static final long serialVersionUID = 1L;
	    String[] values = new String[] { "Worst", "Low", "Medium", "High",
		    "Best" };

	    public int getSize() {
		return values.length;
	    }

	    public String getElementAt(int index) {
		return values[index];
	    }
	});
	for (int i = 0; i < qualityList.getModel().getSize(); i++) {
	    if (currentQuality.equals(qualityList.getModel().getElementAt(i))) {
		qualityList.setSelectedIndex(i);
	    }
	}

	qualityList.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent event) {
		setQuality(event);
	    }
	});

	qualityList.setVisibleRowCount(5);
	qualityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	GridBagConstraints gbc_qualityList = new GridBagConstraints();
	gbc_qualityList.insets = new Insets(0, 0, 5, 5);
	gbc_qualityList.gridx = 3;
	gbc_qualityList.gridy = 1;
	middle_panel.add(qualityList, gbc_qualityList);

	middle_panel.add(startCustomStreamBtn, gbc_startCustomStreamBtn);

	previewPanel = new JPanel();
	previewLabel = new JLabel();
	GridBagConstraints gbc_preview_panel = new GridBagConstraints();
	gbc_preview_panel.insets = new Insets(0, 0, 5, 0);
	gbc_preview_panel.fill = GridBagConstraints.NONE;
	gbc_preview_panel.gridx = 5;
	gbc_preview_panel.gridy = 2;
	GridBagConstraints gbc_preview_label = new GridBagConstraints();
	gbc_preview_label.fill = GridBagConstraints.NONE;
	middle_panel.add(previewPanel, gbc_preview_panel);
	previewPanel.add(previewLabel, gbc_preview_label);

	JButton exitBtn = new JButton("Exit");
	GridBagConstraints gbc_exitBtn = new GridBagConstraints();
	gbc_exitBtn.anchor = GridBagConstraints.SOUTH;
	gbc_exitBtn.gridx = 5;
	gbc_exitBtn.gridy = 4;
	exitBtn.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		cfgUtil.writeConfig();
		System.exit(0);

	    }
	});

	previewCheckBox = new JCheckBox("Show preview");
	GridBagConstraints gbc_previewCheckBox = new GridBagConstraints();
	gbc_previewCheckBox.gridx = 5;
	gbc_previewCheckBox.gridy = 3;
	middle_panel.add(previewCheckBox, gbc_previewCheckBox);
	middle_panel.add(exitBtn, gbc_exitBtn);
	previewCheckBox.setSelected(showPreview);
	previewCheckBox.setToolTipText("Also disables loading images");
	previewCheckBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		setPreviewLoading();
	    }
	});
	showPreview = previewCheckBox.isSelected();
	// start checker thread
	checkThread = new Thread(new StreamCheck());
	checkThread.start();
    }

    private void setPreviewLoading() {
	showPreview = previewCheckBox.isSelected();
    }

    /**
     * Scales and sets the previewImage
     * 
     * @param prev
     */
    public static void setPreviewImage(BufferedImage prev) {
	int newWidth = 176;
	int newHeight = 99;
	BufferedImage small = new BufferedImage(newWidth, newHeight,
		BufferedImage.TYPE_INT_RGB);
	Graphics g = small.createGraphics();
	if (prev != null && showPreview) {
	    g.drawImage(prev, 0, 0, newWidth, newHeight, null);
	} else {
	    g.setColor(frame.getBackground());
	    g.fillRect(0, 0, newWidth, newHeight);
	}
	g.dispose();
	previewLabel.setIcon(new ImageIcon(small));
    }

    /**
     * Sets selected Quality
     * 
     * @param event
     */
    private void setQuality(ListSelectionEvent event) {
	currentQuality = qualityList.getSelectedValue();
    }

    /**
     * Sets current Stream from Stream list
     * 
     * @param event
     */
    private void setStream() {
	if (stream_list.getSelectedValue() != null) {
	    currentStreamName = stream_list.getSelectedValue().getText();
	    for (TwitchStream ts : Functions.streamList) {
		if (ts.getChannel().equals(currentStreamName)) {
		    if (ts.isOnline()) {
			onlineStatus.setText(ts.getOnlineString());
			setPreviewImage(ts.getPreview());
		    } else {
			onlineStatus.setText("Stream is Offline");
			setPreviewImage(null);
		    }
		}
	    }
	} else {
	    currentStreamName = "";
	}
    }
}
