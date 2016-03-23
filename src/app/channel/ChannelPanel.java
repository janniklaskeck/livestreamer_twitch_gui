package app.channel;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class ChannelPanel extends BorderPane {
	private FXMLLoader loader;

	public ChannelPanel() {

		loader = new FXMLLoader(getClass().getResource("ChannelPanel.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
