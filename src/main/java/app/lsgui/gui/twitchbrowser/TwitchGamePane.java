package app.lsgui.gui.twitchbrowser;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class TwitchGamePane extends BorderPane {
    
    private final ImageView gameImage;
    private final Label gameName;
    
    public TwitchGamePane(final String name, final Image image) {
        gameName = new Label(name);
        gameImage = new ImageView(image);
        setCenter(gameImage);
        setBottom(gameName);
        setMinWidth(100L);
        setMinHeight(100L);
        setMaxWidth(100L);
        setMaxHeight(100L);
    }
}
