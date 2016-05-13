package app.lsgui.rest.twitch;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import app.lsgui.model.twitch.TwitchGame;

public class TwitchGameData {

    private Map<String, TwitchGame> gamesMap;

    public TwitchGameData(JsonObject jo) {
        gamesMap = new HashMap<>();
        JsonObject joda = jo;
        joda.isJsonArray();
    }

    /**
     * @return the gamesMap
     */
    public Map<String, TwitchGame> getGamesMap() {
        return gamesMap;
    }

    /**
     * @param gamesMap
     *            the gamesMap to set
     */
    public void setGamesMap(Map<String, TwitchGame> gamesMap) {
        this.gamesMap = gamesMap;
    }

}
