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

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author Niklas 27.09.2014
 * 
 */
public class Main_GUI extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public static String currentStreamName = "";
    public static String currentQuality = "high";

    private static ConfigUtil cfgUtil;

    public JList<String> qualityList;
    public JList<String> stream_list;
    public static DefaultListModel<String> streamListModel;
    private JTextField customStreamTF;
    public static JLabel onlineStatus;
    private JLabel previewLabel;
    private JPanel previewPanel;

    // private JLabel previewImage;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Main_GUI frame = new Main_GUI();
		    frame.setVisible(true);
		    frame.setTitle("Twitch.tv Livestreamer GUI");
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
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			    cfgUtil.writeConfig();
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		    });
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public static void updateList() {
	// streamListModel = new DefaultListModel<String>();
	streamListModel.clear();

	for (int i = 0; i < Functions.streamList.size(); i++) {
	    streamListModel.addElement(Functions.streamList.get(i).channel);
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

	streamListModel = new DefaultListModel<String>();

	updateList();

	stream_panel.setLayout(null);

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setBounds(10, 0, 307, 185);
	stream_panel.add(scrollPane);

	stream_list = new JList<String>();
	stream_list.setVisibleRowCount(10);
	stream_list.setCellRenderer(new MyCellListRenderer());

	stream_list.setModel(streamListModel);

	stream_list.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent event) {
		setStream(event);
	    }
	});
	stream_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	scrollPane.setViewportView(stream_list);

	JPanel custom_StreamPanel = new JPanel();
	custom_StreamPanel.setBounds(10, 196, 307, 130);
	stream_panel.add(custom_StreamPanel);
	GridBagLayout gbl_custom_StreamPanel = new GridBagLayout();
	gbl_custom_StreamPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0,
		0, 0 };
	gbl_custom_StreamPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
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

	onlineStatus = new JLabel("No Stream selected");
	onlineStatus.setHorizontalAlignment(SwingConstants.CENTER);

	GridBagConstraints gbc_onlineStatus = new GridBagConstraints();
	gbc_onlineStatus.gridwidth = 8;
	gbc_onlineStatus.fill = GridBagConstraints.HORIZONTAL;
	gbc_onlineStatus.insets = new Insets(0, 0, 5, 0);
	gbc_onlineStatus.gridx = 0;
	gbc_onlineStatus.gridy = 3;
	custom_StreamPanel.add(onlineStatus, gbc_onlineStatus);

	JButton btnAddStreamTo = new JButton("Add custom Stream to List");
	btnAddStreamTo.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		cfgUtil.saveStream(customStreamTF.getText());

	    }
	});
	GridBagConstraints gbc_btnAddStreamTo = new GridBagConstraints();
	gbc_btnAddStreamTo.gridwidth = 8;
	gbc_btnAddStreamTo.insets = new Insets(0, 0, 0, 5);
	gbc_btnAddStreamTo.gridx = 0;
	gbc_btnAddStreamTo.gridy = 4;
	custom_StreamPanel.add(btnAddStreamTo, gbc_btnAddStreamTo);

	JPanel middle_panel = new JPanel();
	middle_panel.setMaximumSize(new Dimension(200, 200));
	contentPane.add(middle_panel, BorderLayout.EAST);

	GridBagConstraints gbc_middle_panel = new GridBagConstraints();
	gbc_middle_panel.fill = GridBagConstraints.NONE;
	GridBagLayout gbl_middle_panel = new GridBagLayout();
	gbl_middle_panel.setConstraints(middle_panel, gbc_middle_panel);
	gbl_middle_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
	gbl_middle_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
	gbl_middle_panel.columnWeights = new double[] { 1.0, 1.0, 0.0, 1.0 };
	gbl_middle_panel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
	middle_panel.setLayout(gbl_middle_panel);

	qualityList = new JList<String>();

	DefaultListModel<String> qualityListModel = new DefaultListModel<String>();
	qualityListModel.addElement("Worst");
	qualityListModel.addElement("Low");
	qualityListModel.addElement("Medium");
	qualityListModel.addElement("High");
	qualityListModel.addElement("Best");
	qualityList.setModel(qualityListModel);
	// qualityList.setSelectedIndex(0);
	qualityList.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent event) {
		setQuality(event);
	    }
	});

	JButton startStreambutton = new JButton("Start VLC Stream");
	GridBagConstraints gbc_startStreambutton = new GridBagConstraints();
	gbc_startStreambutton.insets = new Insets(0, 0, 5, 5);
	gbc_startStreambutton.gridx = 3;
	gbc_startStreambutton.gridy = 0;
	gbc_startStreambutton.fill = GridBagConstraints.BOTH;
	startStreambutton.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		System.out.println(currentStreamName);
		Functions.OpenStream(currentStreamName, currentQuality);

	    }
	});
	middle_panel.add(startStreambutton, gbc_startStreambutton);

	qualityList.setVisibleRowCount(5);
	qualityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	GridBagConstraints gbc_qualityList = new GridBagConstraints();
	gbc_qualityList.insets = new Insets(0, 0, 5, 5);
	gbc_qualityList.fill = GridBagConstraints.BOTH;
	gbc_qualityList.gridx = 2;
	gbc_qualityList.gridy = 1;
	middle_panel.add(qualityList, gbc_qualityList);

	JButton startCustomStreamBtn = new JButton("Start Custom VLC Stream");
	GridBagConstraints gbc_startCustomStreamBtn = new GridBagConstraints();
	gbc_startCustomStreamBtn.insets = new Insets(0, 0, 5, 5);
	gbc_startCustomStreamBtn.gridx = 3;
	gbc_startCustomStreamBtn.gridy = 1;
	gbc_startCustomStreamBtn.fill = GridBagConstraints.BOTH;
	startCustomStreamBtn.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		if (customStreamTF.getText() != null
			|| customStreamTF.getText() != "") {
		    Functions.OpenStream(customStreamTF.getText(),
			    currentQuality);
		}

	    }
	});

	middle_panel.add(startCustomStreamBtn, gbc_startCustomStreamBtn);

	JButton exitBtn = new JButton("Exit");
	GridBagConstraints gbc_exitBtn = new GridBagConstraints();
	gbc_exitBtn.insets = new Insets(0, 0, 5, 5);
	gbc_exitBtn.anchor = GridBagConstraints.SOUTH;
	gbc_exitBtn.gridx = 3;
	gbc_exitBtn.gridy = 3;
	exitBtn.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		System.exit(0);

	    }
	});
	middle_panel.add(exitBtn, gbc_exitBtn);

	previewPanel = new JPanel();
	previewLabel = new JLabel();
	GridBagConstraints gbc_preview_panel = new GridBagConstraints();
	gbc_preview_panel.fill = GridBagConstraints.NONE;
	gbc_preview_panel.gridx = 3;
	gbc_preview_panel.gridy = 2;
	GridBagConstraints gbc_preview_label = new GridBagConstraints();
	gbc_preview_label.fill = GridBagConstraints.NONE;
	middle_panel.add(previewPanel, gbc_preview_panel);
	previewPanel.add(previewLabel, gbc_preview_label);
	previewLabel.setIcon(new ImageIcon(new BufferedImage(176, 99,
		BufferedImage.TYPE_INT_RGB)));
	// start checker thread
	Thread t = new Thread(new StreamCheck());
	t.start();
    }

    /**
     * Scales and sets the previewImage
     * 
     * @param prev
     */
    public void setPreviewImage(BufferedImage prev) {
	int newWidth = 176;
	int newHeight = 99;
	BufferedImage small = new BufferedImage(newWidth, newHeight,
		BufferedImage.TYPE_INT_RGB);
	Graphics g = small.createGraphics();
	g.drawImage(prev, 0, 0, newWidth, newHeight, null);
	g.dispose();
	previewLabel.setIcon(new ImageIcon(small));
    }

    /**
     * 
     * @param event
     */
    public void setQuality(ListSelectionEvent event) {
	if (currentQuality.equals(qualityList.getModel().getElementAt(
		event.getLastIndex()))) {
	    if (event.getValueIsAdjusting() == false) {
		currentQuality = qualityList.getModel().getElementAt(
			event.getFirstIndex());
	    }
	} else {
	    if (event.getValueIsAdjusting() == false) {
		currentQuality = qualityList.getModel().getElementAt(
			event.getLastIndex());
	    }
	}
    }

    /**
     * 
     * @param event
     */
    public void setStream(ListSelectionEvent event) {
	if (stream_list.getModel().getSize() > 0) {
	    if (currentStreamName.equals(stream_list.getModel().getElementAt(
		    event.getLastIndex()))) {
		if (event.getValueIsAdjusting() == false) {
		    currentStreamName = stream_list.getModel().getElementAt(
			    event.getFirstIndex());
		    for (TwitchStream ts : Functions.streamList) {
			if (ts.channel.equals(currentStreamName)) {
			    if (ts.isOnline()) {
				onlineStatus.setText("<html>Playing "
					+ ts.getGame() + "<br>" + ts.getTitle()
					+ "</html>");
				setPreviewImage(ts.preview);
			    } else {
				onlineStatus.setText("Stream is Offline");
				setPreviewImage(null);
			    }
			}
		    }

		}
	    } else {
		if (event.getValueIsAdjusting() == false) {
		    currentStreamName = stream_list.getModel().getElementAt(
			    event.getLastIndex());
		    for (TwitchStream ts : Functions.streamList) {
			if (ts.channel.equals(currentStreamName)) {
			    if (ts.isOnline()) {
				onlineStatus.setText("<html>Playing "
					+ ts.getGame() + "<br>" + ts.getTitle()
					+ "</html>");
				setPreviewImage(ts.preview);
			    } else {
				onlineStatus.setText("Stream is Offline");
				setPreviewImage(null);
			    }
			}
		    }

		}
	    }
	}
    }
}
