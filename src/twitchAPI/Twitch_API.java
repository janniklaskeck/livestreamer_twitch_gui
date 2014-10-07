package twitchAPI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Twitch_API {
    public static Gson gson = new Gson();

    public static Twitch_Stream getStream(String channelname) {
	try {
	    String json = API
		    .readJsonFromUrl("https://api.twitch.tv/kraken/streams/"
			    + channelname);

	    Twitch_Stream stream = new Twitch_Stream();

	    JsonObject a = gson.fromJson(json, JsonObject.class);
	    JsonObject b = null;

	    if (!a.get("stream").toString().equals("null")) {
		b = gson.fromJson(a.get("stream").toString(), JsonObject.class);
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
}