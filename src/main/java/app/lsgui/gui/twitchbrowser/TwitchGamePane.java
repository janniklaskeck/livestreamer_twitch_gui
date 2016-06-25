package app.lsgui.gui.twitchbrowser;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Niklas 25.06.2016
 *
 */
public class TwitchGamePane extends BorderPane { // NOSONAR

    private static final float RATIO = 1.4f;
    private static final float WIDTH = 150;
    private static final float HEIGHT = WIDTH * RATIO;
    private final ImageView gameImage;
    private final Label gameName;

    /**
     *
     * @param name
     * @param image
     */
    public TwitchGamePane(final String name, final Image image) {
        gameName = new Label(name);
        gameImage = new ImageView(image);
        setCenter(gameImage);
        setBottom(gameName);
        setMinWidth(WIDTH);
        setMinHeight(HEIGHT + 50);
        setMaxWidth(WIDTH);
        setMaxHeight(HEIGHT + 50);
        setPadding(new Insets(5.0));
        gameImage.setFitWidth(WIDTH);
        gameImage.setFitHeight(HEIGHT);
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> event.consume());
    }
}
