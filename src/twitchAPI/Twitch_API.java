package twitchAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import twitchlsgui.Main_GUI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Twitch_API {
    private static Gson gson = new Gson();
    private static String jsonString;
    private static String jsonString2;
    private static Main_GUI parent;

    public Twitch_API(Main_GUI parent) {
	Twitch_API.parent = parent;
    }

    /**
     * Downloads the content from the Twitch.tv API to be processed as JSON into
     * a Twitch_Stream
     * 
     * @param channelname
     * @return
     */
    public Twitch_Json getStream(String channelname) {
	JsonObject a = null;
	JsonObject b = null;
	try {

	    jsonString = readJsonFromUrl("https://api.twitch.tv/kraken/streams/"
		    + channelname);
	    if (jsonString != null) {
		Twitch_Json stream = new Twitch_Json();
		a = gson.fromJson(jsonString, JsonObject.class);
		if (a.get("stream") != null) {
		    if (!a.get("stream").toString().equals("null")) {
			b = gson.fromJson(a.get("stream").toString(),
				JsonObject.class);
		    }
		}
		if (b != null) {
		    stream.setOnline(true);
		    stream.load(b);
		} else {
		    stream.setOnline(false);
		    return stream;
		}
		return stream;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Downloads the top 100 games from Twitch to json
     * 
     * @param in
     * @return
     */
    public JsonObject getGames() {
	JsonObject a = null;
	try {

	    jsonString2 = readJsonFromUrl("https://api.twitch.tv/kraken/games/top?limit=56&offset=0");
	    if (jsonString2 != null) {
		a = gson.fromJson(jsonString2, JsonObject.class);
		return a;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Downloads the top 100 games from Twitch to json
     * 
     * @param in
     * @return
     */
    public static JsonObject getGame(String name) {
	JsonObject a = null;
	try {
	    System.out.println(name);
	    String url = "https://api.twitch.tv/kraken/streams?game=" + name
		    + "&limit=56";
	    jsonString2 = readJsonFromUrl(url);
	    if (jsonString2 != null) {
		a = gson.fromJson(jsonString2, JsonObject.class);
		return a;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Responsible for downloading the source from an URL and returning it as a
     * String
     * 
     * @param urlString
     * @return
     */
    public static String readJsonFromUrl(String urlString) {
	BufferedReader reader = null;
	HttpURLConnection connUrl;
	try {
	    URL url = new URL(urlString);
	    connUrl = (HttpURLConnection) url.openConnection();
	    if (connUrl.getResponseCode() == HttpURLConnection.HTTP_OK) {
		reader = new BufferedReader(new InputStreamReader(
			connUrl.getInputStream()));
	    } else {
		return null;
	    }
	    StringBuffer buffer = new StringBuffer();
	    int read;
	    char[] chars = new char[1024];
	    while ((read = reader.read(chars)) != -1) {
		buffer.append(chars, 0, read);
	    }
	    parent.globals.downloadedBytes += buffer.toString().getBytes(
		    "UTF-8").length;
	    if (reader != null) {
		reader.close();
	    }
	    return buffer.toString();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public boolean checkTwitch(String channelname) {
	return readJsonFromUrl("https://api.twitch.tv/kraken/streams/"
		+ channelname) != null;
    }
}