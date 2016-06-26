package app.lsgui.model.twitch.game;

import app.lsgui.model.twitch.ITwitchItem;
import javafx.scene.image.Image;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class TwitchGame implements ITwitchItem {

    private String name;
    private int viewers;
    private Image boxImage;

    /**
     *
     * @param name
     * @param viewers
     * @param boxImage
     */
    public TwitchGame(final String name, final int viewers, final Image boxImage) {
        this.name = name;
        this.viewers = viewers;
        this.boxImage = boxImage;
    }

    public String getName() {
        return name;
    }

    public int getViewers() {
        return viewers;
    }

    public Image getBoxImage() {
        return boxImage;
    }

}
