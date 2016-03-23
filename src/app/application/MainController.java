package app.application;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import app.channel.Channel;
import app.channel.TwitchChannel;
import app.logger.LogReader;
import app.logger.LogWriter;
import app.logger.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import settings.SettingManager;
import streamService.StreamService;
import util.WrappedImageView;

public class MainController {

	private WrappedImageView streamImageView;

	@FXML
	private ListView<Channel> streamListView;

	@FXML
	private ComboBox<StreamService> serviceComboBox;

	@FXML
	private ChoiceBox<String> qualityChoiceBox;

	@FXML
	private TextArea logTextArea;

	@FXML
	private Label streamDescriptionLabel;

	@FXML
	private TextField streamTextField;

	@FXML
	private BorderPane previewBorderPane;

	@FXML
	private Button startStreamButton;

	@FXML
	private Button startCustomStreamButton;

	@FXML
	private Button recordStreamButton;

	@FXML
	private CheckBox sortStreamsCheckBox;

	@FXML
	private CheckBox previewCheckBox;

	@FXML
	private CheckBox autoUpdateCheckBox;

	@FXML
	private CheckBox trayCheckBox;

	@FXML
	private ChoiceBox<Integer> gamesChoiceBox;

	@FXML
	private ChoiceBox<Integer> channelsChoiceBox;

	@FXML
	private TextField updateIntervalTextField;

	@FXML
	private Label versionLabel;

	@FXML
	public void initialize() {
		streamImageView = new WrappedImageView(null);
		previewBorderPane.setCenter(streamImageView);

		initQuality();
		initStreamList();
		initSettings();
		initLogging();
	}

	private void initSettings() {
		sortStreamsCheckBox.setSelected(SettingManager.getInstance().isSortTwitch());
		previewCheckBox.setSelected(SettingManager.getInstance().isShowPreview());
		autoUpdateCheckBox.setSelected(SettingManager.getInstance().isAutoUpdate());
		trayCheckBox.setSelected(SettingManager.getInstance().isMinimizeToTray());
	}

	private void initLogging() {
		LogWriter lw = new LogWriter(logTextArea);
		SettingManager.getInstance().setLogPrintStream(new PrintStream(lw, true));
		System.setErr(SettingManager.getInstance().getLogPrintStream());
		System.setOut(SettingManager.getInstance().getLogPrintStream());
	}

	private void initQuality() {
		qualityChoiceBox
				.setItems(FXCollections.observableArrayList("Audio", "Mobile", "Low", "Medium", "High", "Best"));
		qualityChoiceBox.getSelectionModel().select("Best");
	}

	private void initStreamList() {
		serviceComboBox.setCellFactory(new Callback<ListView<StreamService>, ListCell<StreamService>>() {
			@Override
			public ListCell<StreamService> call(ListView<StreamService> param) {
				return new ListCell<StreamService>() {
					@Override
					protected void updateItem(StreamService item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							setText(item.getName());
						}
					}
				};
			}
		});
		if (SettingManager.getInstance().getStreamServices().size() == 0) {
			SettingManager.getInstance().getStreamServices().add(new StreamService("Twitch.tv", "http://twitch.tv"));
		}
		serviceComboBox.setItems(SettingManager.getInstance().getStreamServicesObservable());
		serviceComboBox.getSelectionModel().select(0);

		streamListView.setItems(serviceComboBox.getSelectionModel().getSelectedItem().getChannelsObservable());
		serviceComboBox.getSelectionModel().getSelectedItem().getChannelsObservable()
				.addListener(new ListChangeListener<Channel>() {
					@Override
					public void onChanged(Change<? extends Channel> c) {
						streamListView.setItems(
								serviceComboBox.getSelectionModel().getSelectedItem().getChannelsObservable());
					}
				});
		streamListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		streamListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Channel>() {
			@Override
			public void changed(ObservableValue<? extends Channel> observable, Channel oldValue, Channel newValue) {
				if (newValue.getClass().equals(TwitchChannel.class)) {
					TwitchChannel twitchChannel = (TwitchChannel) newValue;
					if (twitchChannel.isOnline()) {
						streamDescriptionLabel.setText(twitchChannel.getStreamDescription());
						streamImageView.setImage(twitchChannel.getPreviewImage());
					} else {
						streamDescriptionLabel.setText("Channel is offline");
						streamImageView.setImage(null);
					}
				} else {
					streamDescriptionLabel.setText("No Description available");
					streamImageView.setImage(null);
				}
			}
		});
	}

	@FXML
	private void openChat() {
		Logger.warning("no chat implemented");
	}

	@FXML
	private void openInBrowser() {
		if (serviceComboBox.getItems().size() > 0 && streamListView.getSelectionModel().getSelectedItem() != null) {
			String channel = streamListView.getSelectionModel().getSelectedItem().getName();
			String url = serviceComboBox.getSelectionModel().getSelectedItem().getUrl();
			URI uri = null;
			try {
				uri = new URI(url + "/" + channel);
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@FXML
	private void startStream() {
		if (serviceComboBox.getSelectionModel().getSelectedItem() != null
				&& streamListView.getSelectionModel().getSelectedItem() != null) {
			String channel = streamListView.getSelectionModel().getSelectedItem().getName();
			String url = serviceComboBox.getSelectionModel().getSelectedItem().getUrl();

			String stream = url + "/" + channel;
			String quality = qualityChoiceBox.getSelectionModel().getSelectedItem();
			startLivestreamer(Arrays.asList("livestreamer", stream, quality));
		}
	}

	@FXML
	private void startCustomStream() {
		if (serviceComboBox.getSelectionModel().getSelectedItem() != null && !streamTextField.getText().equals("")) {
			String channel = streamTextField.getText();
			String url = serviceComboBox.getSelectionModel().getSelectedItem().getUrl();

			String stream = url + "/" + channel;
			String quality = qualityChoiceBox.getSelectionModel().getSelectedItem();
			startLivestreamer(Arrays.asList("livestreamer", stream, quality));
		}
	}

	@FXML
	private void recordStream() {
		Logger.warning("no recording implemented");
	}

	@FXML
	private void addStream() {
		if (!streamTextField.getText().equals("") && serviceComboBox.getSelectionModel().getSelectedItem() != null) {
			Channel newChannel = new TwitchChannel(streamTextField.getText());
			serviceComboBox.getSelectionModel().getSelectedItem().getChannels().add(newChannel);
		}
	}

	@FXML
	private void removeStream() {
		if (streamListView.getSelectionModel().getSelectedItem() != null
				&& serviceComboBox.getSelectionModel().getSelectedItem() != null) {
			serviceComboBox.getSelectionModel().getSelectedItem().getChannels()
					.remove(streamListView.getSelectionModel().getSelectedItem());
		}

	}

	@FXML
	private void exitAction() {
		if (SettingManager.getInstance().isMinimizeToTray()) {
			if (SystemTray.isSupported()) {
				Platform.setImplicitExit(false);
				addSystemTray();
			}
			Main.mainStage.hide();
		} else {
			Platform.exit();
		}
	}

	@FXML
	private void twitchCredentialsInput() {
		Logger.warning("notwitchinput");
	}

	@FXML
	private void exportSettingsAndStreams() {
		Logger.warning("noexport");
	}

	@FXML
	private void importSettingsAndStreams() {
		Logger.warning("noimport");
	}

	@FXML
	private void setAutoSortStreams() {
		SettingManager.getInstance().setSortTwitch(autoUpdateCheckBox.isSelected());
	}

	@FXML
	private void setDownloadPreviewImages() {
		SettingManager.getInstance().setShowPreview(previewCheckBox.isSelected());
	}

	@FXML
	private void setAutoUpdate() {
		SettingManager.getInstance().setAutoUpdate(autoUpdateCheckBox.isSelected());
	}

	@FXML
	private void setMinimizeToTray() {
		SettingManager.getInstance().setMinimizeToTray(trayCheckBox.isSelected());
		Logger.info(SettingManager.getInstance().isMinimizeToTray() + "");
		Logger.info(trayCheckBox.isSelected() + "");
	}

	private void startLivestreamer(List<String> cmd) {
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			Process prc = pb.start();
			Thread reader = new Thread(new LogReader(prc.getInputStream()));

			reader.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addSystemTray() {
		final Stage primaryStage = Main.mainStage;
		SystemTray sTray = null;
		sTray = SystemTray.getSystemTray();
		java.awt.Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/icon.jpg"));

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				if (SettingManager.getInstance().isMinimizeToTray()) {
					primaryStage.hide();
				} else {
					Platform.exit();
				}
			}
		});

		PopupMenu popup = new PopupMenu();
		MenuItem showItem = new MenuItem("Show");
		MenuItem exitItem = new MenuItem("Exit");

		showItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						primaryStage.show();
					}
				});
			}
		});
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						SettingManager.getInstance().saveSettings();
						System.exit(0);
					}
				});
			}
		});

		popup.add(showItem);
		popup.add(exitItem);

		TrayIcon icon = new TrayIcon(image, "Livestreamer GUI", popup);
		icon.setImageAutoSize(true);

		try {
			sTray.add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
