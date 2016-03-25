package twitch;
import org.junit.Test;

import app.lsgui.serviceapi.twitch.TwitchManager;

public class RemoteTestTwitch {

    TwitchManager tm = TwitchManager.instance();

    @Test
    public void testGetAPIResponse() {
        @SuppressWarnings("deprecation")
        String a = tm.readJsonFromUrl("https://api.twitch.tv/kraken/streams/elajjaz");
        String b = tm.getAPIResponse("https://api.twitch.tv/kraken/streams/elajjaz");
        assert (a.equals(b));
    }

}
