package app.lsgui.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.Channel;
import app.lsgui.model.Service;
import app.lsgui.service.Settings;
import app.lsgui.service.twitch.TwitchChannelUpdateService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);
	private static Stage rootstage;

	@Override
	public void init() {
		Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
			LOGGER.error("Uncaught Exception on JavaFX Thread", throwable);
			LOGGER.error("Exiting JavaFX Thread...");
			Platform.exit();
		});
		Platform.setImplicitExit(false);
		Settings.instance();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/mainwindow.fxml"));
		} catch (IOException e) {
			LOGGER.error("ERROR while load main fxml", e);
			Platform.exit();
		}
		Scene scene = new Scene(root);
		setRootStage(primaryStage);
		scene.getStylesheets().add(getClass().getResource("/lightStyle.css").toString());

		primaryStage.setMinHeight(550);
		primaryStage.setHeight(550);

		primaryStage.setMinWidth(650);
		primaryStage.setWidth(750);

		primaryStage.setTitle("Livestreamer GUI v3.0");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(event -> {
			Settings.instance().saveSettings();
			Iterator<Map.Entry<Channel, TwitchChannelUpdateService>> it = Service.UPDATESERVICES.entrySet().iterator();
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
			Platform.exit();
		});

		primaryStage.setOnHiding(event -> {
			Settings.instance().saveSettings();
			Iterator<Map.Entry<Channel, TwitchChannelUpdateService>> it = Service.UPDATESERVICES.entrySet().iterator();
			while (it.hasNext()) {
				it.next();
				it.remove();
			}
			Platform.exit();
		});
	}

	public static final Stage getRootStage() {
		return rootstage;
	}

	private static final void setRootStage(final Stage newRootStage) {
		rootstage = newRootStage;
	}

}
