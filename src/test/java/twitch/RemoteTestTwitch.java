package twitch;

import org.junit.Test;

import app.lsgui.rest.twitch.TwitchAPIClient;

public class RemoteTestTwitch {

    TwitchAPIClient tp = TwitchAPIClient.instance();

    @Test
    public void test() {
        System.out.println(tp.getListOfFollowedStreams("nuamor"));
    }

}
