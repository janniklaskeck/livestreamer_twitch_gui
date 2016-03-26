package app.lsgui.gui.streamList;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class StreamList extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamList.class);
    private static FXMLLoader loader;

    public StreamList() {
        LOGGER.debug("Construct StreamList");
        loader = new FXMLLoader(getClass().getResource("/StreamList.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}