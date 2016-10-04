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
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.Settings;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    public static final float WIDTH = 150;
    public static final DoubleProperty HEIGHT_PROPERTY = new SimpleDoubleProperty();

    private static final int BOTTOM_OFFSET = 40;
    private static final double RATIO_GAME = 1.4D;
    private static final double RATIO_CHANNEL = 0.5625D;
    private static final double HEIGHT_GAME = WIDTH * RATIO_GAME;
    private static final double HEIGHT_CHANNEL = WIDTH * RATIO_CHANNEL;

    public TwitchItemPane() {
        // Empty Constructor
    }

    @Override
    protected void updateItem(final ITwitchItem item, final boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (item instanceof TwitchGame) {
                final TwitchGame game = (TwitchGame) item;
                setGraphic(this.createGameBorderPane(game));
                HEIGHT_PROPERTY.set(HEIGHT_GAME + BOTTOM_OFFSET);
            } else if (item instanceof TwitchChannel) {
                final TwitchChannel channel = (TwitchChannel) item;
                setGraphic(this.createChannelBorderPane(channel));
                HEIGHT_PROPERTY.set(HEIGHT_CHANNEL + BOTTOM_OFFSET);
            }
        }
    }

    private BorderPane createGameBorderPane(final TwitchGame game) {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView gameImage = new ImageView();
        gameImage.imageProperty().bind(game.getBoxImage());
        gameImage.setFitWidth(WIDTH);
        gameImage.setFitHeight(HEIGHT_GAME);
        final Label nameLabel = new Label();
        nameLabel.setTooltip(new Tooltip("Name of Category"));
        nameLabel.textProperty().bind(game.getName());
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label();
        viewersLabel.setTooltip(new Tooltip("Amount of Viewers"));
        viewersLabel.textProperty().bind(game.getViewers());
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GROUP));
        final Label channelLabel = new Label();
        channelLabel.setTooltip(new Tooltip("Amount of Channels"));
        channelLabel.textProperty().bind(game.getChannelCount());
        channelLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final VBox textBox = new VBox(nameLabel, viewersLabel, channelLabel);
        contentBorderPane.setCenter(gameImage);
        contentBorderPane.setBottom(textBox);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                BrowserCore.getInstance().openGame(game.getName().get());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                final ContextMenu contextMenu = createContextMenu(game);
                this.contextMenuProperty().set(contextMenu);
            }
            event.consume();
        });
        return contentBorderPane;
    }

    private BorderPane createChannelBorderPane(final TwitchChannel channel) {
        final BorderPane contentBorderPane = new BorderPane();
        final ImageView channelImage = new ImageView();
        channelImage.imageProperty().bind(channel.getPreviewImageMedium());
        channelImage.setFitWidth(WIDTH);
        channelImage.setFitHeight(HEIGHT_CHANNEL);
        final Label nameLabel = new Label();
        nameLabel.setTooltip(new Tooltip("Name of the Channel"));
        nameLabel.textProperty().bind(channel.getDisplayName());
        nameLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
        final Label viewersLabel = new Label();
        viewersLabel.setTooltip(new Tooltip("Amount of Viewers"));
        viewersLabel.textProperty().bind(channel.getViewersString());
        viewersLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));
        final Label uptimeLabel = new Label();
        uptimeLabel.setTooltip(new Tooltip("Uptime of the Channel"));
        uptimeLabel.textProperty().bind(channel.getUptimeString());
        uptimeLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CLOCK_ALT));
        final VBox textBox = new VBox(nameLabel, viewersLabel, uptimeLabel);
        contentBorderPane.setCenter(channelImage);
        contentBorderPane.setBottom(textBox);
        final Tooltip titleTooltip = new Tooltip();
        titleTooltip.textProperty().bind(channel.getTitle());
        final Node node = contentBorderPane;
        Tooltip.install(node, titleTooltip);
        contentBorderPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                BrowserCore.getInstance().startStream(channel.getName().get());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                final ContextMenu contextMenu = createContextMenu(channel);
                this.contextMenuProperty().set(contextMenu);
            }
            event.consume();
        });
        return contentBorderPane;
    }

    private static ContextMenu createContextMenu(final TwitchChannel channel) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem startStream = new MenuItem("Start Stream");
        startStream.setOnAction(eventStartContext -> BrowserCore.getInstance().startStream(channel.getName().get()));
        final MenuItem addToList = new MenuItem("Add Stream To Favourites");
        final IService twitchService = Settings.getInstance().getTwitchService();
        addToList
                .setOnAction(eventAddContext -> LsGuiUtils.addChannelToService(channel.getName().get(), twitchService));
        contextMenu.getItems().add(startStream);
        contextMenu.getItems().add(addToList);
        return contextMenu;
    }

    private static ContextMenu createContextMenu(final TwitchGame game) {
        final ContextMenu contextMenu = new ContextMenu();
        if (!Settings.getInstance().favouriteGamesProperty().contains(game.getName().get())) {
            final MenuItem addToFavourites = new MenuItem("Add to Favourites");
            addToFavourites
                    .setOnAction(eventStartContext -> Settings.getInstance().addFavouriteGame(game.getName().get()));
            contextMenu.getItems().add(addToFavourites);
        } else {
            final MenuItem removeFromFavourites = new MenuItem("Remove from Favourites");
            removeFromFavourites
                    .setOnAction(eventStartContext -> Settings.getInstance().removeFavouriteGame(game.getName().get()));
            contextMenu.getItems().add(removeFromFavourites);
        }
        return contextMenu;
    }
}
