package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridCell;

import app.lsgui.browser.BrowserCore;
import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.channel.TwitchChannel;
import app.lsgui.model.twitch.game.TwitchGame;
import app.lsgui.utils.LivestreamerUtils;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TwitchItemPane extends GridCell<ITwitchItem> { // NOSONAR

    public static final float RATIO_GAME = 1.4f;
    public static final float RATIO_CHANNEL = 0.5625f;
    public static final float WIDTH = 150;
    public static final DoubleProperty HEIGHT_PROPERTY = new SimpleDoubleProperty();
    public static final float HEIGHT_GAME = WIDTH * RATIO_GAME;
    public static final float HEIGHT_CHANNEL = WIDTH * RATIO_CHANNEL;

    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty viewers = new SimpleStringProperty();

    @Override
    protected void updateItem(final ITwitchItem item, final boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (item instanceof TwitchGame) {
                final TwitchGame game = (TwitchGame) item;
                name.bind(game.getName());
                viewers.bind(game.getViewers());
                imageProperty.bind(game.getBoxImage());
                setGraphic(createGameBorderPane());
                HEIGHT_PROPERTY.set(HEIGHT_GAME + 50);
            } else if (item instanceof TwitchChannel) {
                final TwitchChannel channel = (TwitchChannel) item;
                name.bind(channel.getName());
                viewers.bind(channel.getViewersString());
                imageProperty.bind(channel.getPreviewImage());
                setGraphic(createChannelBorderPane());
                HEIGHT_PROPERTY.set(HEIGHT_CHANNEL + 50);
            }
        }
    }

    private BorderPane createGameBorderPane() {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView gameImage = new ImageView();
        gameImage.imageProperty().bind(imageProperty);
        gameImage.setFitWidth(WIDTH);
        gameImage.setFitHeight(HEIGHT_GAME);
        final Label nameLabel = new Label();
        nameLabel.textProperty().bind(name);
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label();
        viewersLabel.textProperty().bind(viewers);
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final VBox textBox = new VBox(nameLabel, viewersLabel);
        contentBorderPane.setCenter(gameImage);
        contentBorderPane.setBottom(textBox);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            BrowserCore.getInstance().openGame(name.get());
            event.consume();
        });
        return contentBorderPane;
    }

    private BorderPane createChannelBorderPane() {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView channelImage = new ImageView();
        channelImage.imageProperty().bind(imageProperty);
        channelImage.setFitWidth(WIDTH);
        channelImage.setFitHeight(HEIGHT_CHANNEL);
        final Label nameLabel = new Label();
        nameLabel.textProperty().bind(name);
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label();
        viewersLabel.textProperty().bind(viewers);
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final VBox textBox = new VBox(nameLabel, viewersLabel);
        contentBorderPane.setCenter(channelImage);
        contentBorderPane.setBottom(textBox);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LivestreamerUtils.startLivestreamer("twitch.tv/" + name.get(), "source");
            event.consume();
        });
        return contentBorderPane;
    }
}
