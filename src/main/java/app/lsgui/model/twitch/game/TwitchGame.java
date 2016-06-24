package app.lsgui.model.twitch.game;

import javafx.scene.image.Image;

public class TwitchGame {

    private String name;
    private int viewers;
    private Image boxImage;
    
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
