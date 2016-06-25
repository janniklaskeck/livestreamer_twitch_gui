package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridCell;

import app.lsgui.model.twitch.game.TwitchGame;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Niklas 25.06.2016
 *
 */
public class TwitchGamePane extends GridCell<TwitchGame> { // NOSONAR

    public static final float RATIO = 1.4f;
    public static final float WIDTH = 150;
    public static final float HEIGHT = WIDTH * RATIO;

    @Override
    protected void updateItem(TwitchGame item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(createContentBorderPane(item.getName(), item.getViewers(), item.getBoxImage()));
        }
    }

    private BorderPane createContentBorderPane(final String name, final int viewers, final Image image) {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView gameImage = new ImageView(image);
        gameImage.setFitWidth(WIDTH);
        gameImage.setFitHeight(HEIGHT);
        final Label nameLabel = new Label(name);
        final Label viewersLabel = new Label(Integer.toString(viewers));
        final VBox textBox = new VBox(nameLabel, viewersLabel);
        contentBorderPane.setCenter(gameImage);
        contentBorderPane.setBottom(textBox);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println(nameLabel.getText());
            event.consume();
        });
        return contentBorderPane;
    }
}
