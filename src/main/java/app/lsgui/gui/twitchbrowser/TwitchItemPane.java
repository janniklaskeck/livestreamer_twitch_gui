/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridCell;

import app.lsgui.model.IService;
import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.model.twitch.TwitchGame;
import app.lsgui.utils.BrowserCore;
import app.lsgui.utils.LivestreamerUtils;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.Settings;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public final class TwitchItemPane extends GridCell<ITwitchItem> {

    private static final int BOTTOM_OFFSET = 40;
    private static final double RATIO_GAME = 1.4D;
    private static final double RATIO_CHANNEL = 0.5625D;
    public static final float WIDTH = 150;
    private static final double HEIGHT_GAME = WIDTH * RATIO_GAME;
    private static final double HEIGHT_CHANNEL = WIDTH * RATIO_CHANNEL;
    public static final DoubleProperty HEIGHT_PROPERTY = new SimpleDoubleProperty();

    private TwitchChannel channel;
    private TwitchGame game;
    private StringProperty quality = new SimpleStringProperty();

    public TwitchItemPane(final ReadOnlyObjectProperty<String> quality) {
        this.quality.bind(quality);
    }

    @Override
    protected void updateItem(final ITwitchItem item, final boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (item instanceof TwitchGame) {
                this.game = (TwitchGame) item;
                setGraphic(this.createGameBorderPane());
                HEIGHT_PROPERTY.set(HEIGHT_GAME + BOTTOM_OFFSET);
            } else if (item instanceof TwitchChannel) {
                this.channel = (TwitchChannel) item;
                setGraphic(this.createChannelBorderPane());
                HEIGHT_PROPERTY.set(HEIGHT_CHANNEL + BOTTOM_OFFSET);
            }
        }
    }

    private BorderPane createGameBorderPane() {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView gameImage = new ImageView();
        gameImage.imageProperty().bind(this.game.getBoxImage());
        gameImage.setFitWidth(WIDTH);
        gameImage.setFitHeight(HEIGHT_GAME);
        final Label nameLabel = new Label();
        nameLabel.setTooltip(new Tooltip("Name of Category"));
        nameLabel.textProperty().bind(this.game.getName());
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label();
        viewersLabel.setTooltip(new Tooltip("Amount of Viewers"));
        viewersLabel.textProperty().bind(this.game.getViewers());
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GROUP));
        final Label channelLabel = new Label();
        channelLabel.setTooltip(new Tooltip("Amount of Channels"));
        channelLabel.textProperty().bind(this.game.getChannelCount());
        channelLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final VBox textBox = new VBox(nameLabel, viewersLabel, channelLabel);
        contentBorderPane.setCenter(gameImage);
        contentBorderPane.setBottom(textBox);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                BrowserCore.getInstance().openGame(this.game.getName().get());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                final ContextMenu contextMenu = new ContextMenu();
                if (!Settings.getInstance().getFavouriteGames().contains(this.game.getName().get())) {
                    final MenuItem addToFavourites = new MenuItem("Add to Favourites");
                    addToFavourites.setOnAction(
                            eventStartContext -> Settings.getInstance().addFavouriteGame(this.game.getName().get()));
                    contextMenu.getItems().add(addToFavourites);
                } else {
                    final MenuItem removeFromFavourites = new MenuItem("Remove from Favourites");
                    removeFromFavourites.setOnAction(
                            eventStartContext -> Settings.getInstance().removeFavouriteGame(this.game.getName().get()));
                    contextMenu.getItems().add(removeFromFavourites);
                }
                this.contextMenuProperty().set(contextMenu);
            }
            event.consume();
        });
        return contentBorderPane;
    }

    private BorderPane createChannelBorderPane() {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView channelImage = new ImageView();
        channelImage.imageProperty().bind(this.channel.getPreviewImageMedium());
        channelImage.setFitWidth(WIDTH);
        channelImage.setFitHeight(HEIGHT_CHANNEL);
        final Label nameLabel = new Label();
        nameLabel.setTooltip(new Tooltip("Name of the Channel"));
        nameLabel.textProperty().bind(this.channel.getName());
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label();
        viewersLabel.setTooltip(new Tooltip("Amount of Viewers"));
        viewersLabel.textProperty().bind(this.channel.getViewersString());
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final Label uptimeLabel = new Label();
        uptimeLabel.setTooltip(new Tooltip("Uptime of the Channel"));
        uptimeLabel.textProperty().bind(this.channel.getUptimeString());
        uptimeLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CLOCK_ALT));
        final VBox textBox = new VBox(nameLabel, viewersLabel, uptimeLabel);
        contentBorderPane.setCenter(channelImage);
        contentBorderPane.setBottom(textBox);
        final Tooltip titleTooltip = new Tooltip();
        titleTooltip.textProperty().bind(this.channel.getTitle());
        final Node node = contentBorderPane;
        Tooltip.install(node, titleTooltip);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                LivestreamerUtils.startLivestreamer("twitch.tv/" + this.channel.getName().get(), this.quality.get());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem startStream = new MenuItem("Start Stream");
                startStream.setOnAction(eventStartContext -> LivestreamerUtils
                        .startLivestreamer("twitch.tv/" + this.channel.getName().get(), this.quality.get()));
                final MenuItem addToList = new MenuItem("Add Stream To Favourites");
                final IService twitchService = Settings.getInstance().getTwitchService();
                addToList.setOnAction(
                        eventAddContext -> LsGuiUtils.addChannelToService(this.channel.getName().get(), twitchService));
                contextMenu.getItems().add(startStream);
                contextMenu.getItems().add(addToList);
                this.contextMenuProperty().set(contextMenu);
            }
            event.consume();
        });
        return contentBorderPane;
    }
}
