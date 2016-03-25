package app.lsgui.serviceapi.twitch;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.sun.javafx.collections.MappingChange.Map;

import app.lsgui.model.TwitchGameModel;

public class TwitchGames {

	private Map<String, TwitchGameModel> gamesMap;

	public TwitchGames(JsonObject twitchData) {
		gamesMap = new Map<String, TwitchGameModel>();
	}

}
