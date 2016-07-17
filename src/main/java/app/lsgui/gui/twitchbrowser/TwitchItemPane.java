package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridCell;

import app.lsgui.browser.BrowserCore;
import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.channel.TwitchChannel;
import app.lsgui.model.twitch.game.TwitchGame;
import app.lsgui.utils.LivestreamerUtils;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TwitchItemPane extends GridCell<ITwitchItem> { // NOSONAR

    public static final float RATIO = 1.4f;
    public static final float WIDTH = 150;
    public static final float HEIGHT = WIDTH * RATIO;

    @Override
    protected void updateItem(final ITwitchItem item, final boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (item instanceof TwitchGame) {
                final TwitchGame game = (TwitchGame) item;
                setGraphic(createGameBorderPane(game.getName(), game.getViewers(), game.getBoxImage()));
            } else if (item instanceof TwitchChannel) {
                final TwitchChannel channel = (TwitchChannel) item;
                setGraphic(createChannelBorderPane(channel.getName().get(), channel.getViewers().get(),
                        channel.getPreviewImage().get()));
            }
        }
    }

    private BorderPane createGameBorderPane(final String name, final int viewers, final Image image) {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView gameImage = new ImageView(image);
        gameImage.setFitWidth(WIDTH);
        gameImage.setFitHeight(HEIGHT);
        final Label nameLabel = new Label(name);
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label(Integer.toString(viewers));
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final VBox textBox = new VBox(nameLabel, viewersLabel);
        contentBorderPane.setCenter(gameImage);
        contentBorderPane.setBottom(textBox);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            BrowserCore.getInstance().openGame(name);
            event.consume();
        });
        return contentBorderPane;
    }

    private BorderPane createChannelBorderPane(final String name, final int viewers, final Image image) {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView gameImage = new ImageView(image);
        gameImage.setFitWidth(WIDTH);
        gameImage.setFitHeight(HEIGHT);
        final Label nameLabel = new Label(name);
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label(Integer.toString(viewers));
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final VBox textBox = new VBox(nameLabel, viewersLabel);
        contentBorderPane.setCenter(gameImage);
        contentBorderPane.setBottom(textBox);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LivestreamerUtils.startLivestreamer("twitch.tv/" + name, "source");
            event.consume();
        });
        return contentBorderPane;
    }
}
