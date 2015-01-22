package twitchAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import twitchlsgui.Main_GUI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Twitch_API {
    private static Gson gson = new Gson();
    private static String jsonString;

    /**
     * Downloads the content from the Twitch.tv API to be processed as JSON into
     * a Twitch_Stream
     * 
     * @param channelname
     * @return
     */
    public static Twitch_Stream getStream(String channelname) {
	JsonObject a = null;
	JsonObject b = null;
	try {

	    jsonString = readJsonFromUrl("https://api.twitch.tv/kraken/streams/"
		    + channelname);
	    if (jsonString != null) {
		Twitch_Stream stream = new Twitch_Stream();
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
	    if (Main_GUI._DEBUG)
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
	try {
	    URL url = new URL(urlString);
	    reader = new BufferedReader(new InputStreamReader(url.openStream()));
	    StringBuffer buffer = new StringBuffer();
	    int read;
	    char[] chars = new char[1024];
	    while ((read = reader.read(chars)) != -1) {
		buffer.append(chars, 0, read);
	    }
	    Main_GUI.downloadedBytes += buffer.toString().getBytes("UTF-8").length;
	    if (reader != null) {
		reader.close();
	    }
	    return buffer.toString();
	} catch (IOException e) {
	    if (Main_GUI._DEBUG)
		e.printStackTrace();
	    return null;
	}
    }

    public static String checkTwitch(String channelname) {
	return readJsonFromUrl("https://api.twitch.tv/kraken/streams/"
		+ channelname);
    }
}