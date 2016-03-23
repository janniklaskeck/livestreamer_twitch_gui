package channel;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.converter.DateTimeStringConverter;
import logger.Logger;
import remoteAPI.Twitch_API;
import remoteAPI.TwitchChannel_Json;

public class TwitchChannel extends AnchorPane implements Channel {

    private String game = "";
    private String name = "";
    private String title = "";
    private String description = "";
    private String previewURL = "";
    private Image previewImage;
    private boolean isOnline = false;
    private int viewers = 0;
    private long uptime = 0L;
    private FXMLLoader loader;

    @FXML
    private Label channelLabel;

    public TwitchChannel(String name) {
        setName(name);
        loader = new FXMLLoader(getClass().getResource("Channel.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        channelLabel.setTextFill(Color.BLACK);
        channelLabel.setText(getName());
    }

    @Override
    public void updateChannel() {
        TwitchChannel_Json json = Twitch_API.getStream(getName());
        if (json != null) {
            setGame(json.getMeta_game());
            setDescription(json.getStatus());
            setTitle(json.getTitle());
            setViewers(json.getCurrent_viewers());
            setPreviewURL(json.getScreen_cap_url_large());
            setOnline(json.isOnline());
            setUptime(json.getCreated_At(), json.getUpdated_At());
            if (isOnline()) {
                Logger.info("Update " + getName());
                setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            }
            if (getPreviewURL() != null) {
                for (int i = 0; i < 5; i++) {
                    previewImage = new Image(getPreviewURL());
                    if (previewImage != null) {
                        setPreviewImage(previewImage);
                        break;
                    }
                }
            }
        }
    }

    private void setUptime(String createdAt, String updatedAt) {
        if (isOnline()) {
            try {
                DateTimeStringConverter a = new DateTimeStringConverter("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Calendar created = Calendar.getInstance();
                created.setTime(a.fromString(createdAt));

                Calendar updated = Calendar.getInstance();
                updated.setTime(a.fromString(updatedAt));

                Calendar diff = Calendar.getInstance();
                diff.setTimeInMillis(updated.getTimeInMillis() - created.getTimeInMillis());

                Calendar diffToNow = Calendar.getInstance();
                diffToNow.setTimeInMillis(new Date().getTime() - diff.getTimeInMillis());

                uptime = diffToNow.getTimeInMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getStreamDescription() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(uptime);
        String desc = "Currently playing: " + getGame() + "\nOnline for: " + c.get(Calendar.HOUR_OF_DAY) + ":"
                + c.get(Calendar.MINUTE) + " hours |" + " Current Viewers: " + getViewers() + "\n" + getTitle();
        return desc;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;

    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    /**
     * @return the isOnline
     */
    @Override
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * @param isOnline
     *            the isOnline to set
     */
    @Override
    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * @return the previewImage
     */
    public Image getPreviewImage() {
        return previewImage;
    }

    /**
     * @param previewImage
     *            the previewImage to set
     */
    public void setPreviewImage(Image previewImage) {
        this.previewImage = previewImage;
    }

}
