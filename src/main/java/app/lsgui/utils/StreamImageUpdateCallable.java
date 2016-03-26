package app.lsgui.utils;

import java.util.concurrent.Callable;

import app.lsgui.serviceapi.twitch.TwitchStreamData;
import javafx.scene.image.Image;

public class StreamImageUpdateCallable implements Callable<Void> {

    private TwitchStreamData data;

    public StreamImageUpdateCallable(TwitchStreamData data) {
        this.data = data;
    }

    @Override
    public Void call() throws Exception {
        data.setPreviewImage(new Image(data.getPreviewURL()));
        data.setLogoImage(new Image(data.getLogoURL()));
        return null;
    }

}
