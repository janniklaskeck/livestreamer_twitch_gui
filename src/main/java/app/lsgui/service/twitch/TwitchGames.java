package app.lsgui.serviceapi.twitch;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import app.lsgui.model.twitch.TwitchGameModel;

public class TwitchGames {

    private Map<String, TwitchGameModel> gamesMap;

    public TwitchGames(JsonObject twitchData) {
        gamesMap = new HashMap<String, TwitchGameModel>();
    }

    /**
     * @return the gamesMap
     */
    public Map<String, TwitchGameModel> getGamesMap() {
        return gamesMap;
    }

    /**
     * @param gamesMap
     *            the gamesMap to set
     */
    public void setGamesMap(Map<String, TwitchGameModel> gamesMap) {
        this.gamesMap = gamesMap;
    }

}
