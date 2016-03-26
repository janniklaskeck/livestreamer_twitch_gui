package app.lsgui.gui.streamInfoPanel;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.TwitchStreamModel;
import app.lsgui.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class StreamInfoPanel extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamInfoPanel.class);
    private static FXMLLoader loader;

    @FXML
    private CheckBox notifyCheckBox;

    @FXML
    private Label streamInfoLabel;

    @FXML
    private ImageView previewImageView;

    public StreamInfoPanel(TwitchStreamModel model) {
        LOGGER.debug("Construct StreamInfoPanel");
        loader = new FXMLLoader(getClass().getResource("/StreamInfoPanel.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startStream() {
        LOGGER.info("Starting Stream {} with Quality {}", "NOSTREAM", "NOQUALITY");
        Utils.startLivestreamer("", "");
    }

    @FXML
    private void recordStream() {
        LOGGER.info("Record Stream {} with Quality {}", "NOSTREAM", "NOQUALITY");
        Utils.recordLivestreamer("", "");
    }

    @FXML
    private void openChat() {

    }

    @FXML
    private void openInBrowser() {
        Utils.openURLInBrowser("");
    }
}
