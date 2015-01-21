package twitchAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import twitchlsgui.Main_GUI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Twitch_API {
    public static Gson gson = new Gson();

    /**
     * Downloads the content from the Twitch.tv API to be processed as JSON into
     * a Twitch_Stream
     * 
     * @param channelname
     * @return
     */
    public static Twitch_Stream getStream(String channelname) {
	try {
	    String json = readJsonFromUrl("https://api.twitch.tv/kraken/streams/"
		    + channelname);

	    Twitch_Stream stream = new Twitch_Stream();

	    JsonObject a = gson.fromJson(json, JsonObject.class);
	    JsonObject b = null;
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
	} catch (Exception error) {
	    error.printStackTrace();
	}
	return null;
    }

    /**
     * Responsible for downloading the source from an URL and returning it as a
     * String
     * 
     * @param urlString
     * @return
     * @throws Exception
     */
    public static String readJsonFromUrl(String urlString) throws Exception {
	BufferedReader reader = null;
	try {
	    URL url = new URL(urlString);
	    reader = new BufferedReader(new InputStreamReader(url.openStream()));
	    StringBuffer buffer = new StringBuffer();
	    int read;
	    char[] chars = new char[1024];
	    while ((read = reader.read(chars)) != -1)
		buffer.append(chars, 0, read);
	    Main_GUI.downloadedBytes += buffer.toString().getBytes("UTF-8").length;
	    return buffer.toString();

	} finally {
	    if (reader != null)
		reader.close();
	}
    }
}