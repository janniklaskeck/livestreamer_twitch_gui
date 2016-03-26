package twitch;

import org.junit.Test;

import app.lsgui.serviceapi.twitch.TwitchProcessor;

public class RemoteTestTwitch {

    TwitchProcessor tp = TwitchProcessor.instance();

    @Test
    public void test() {
        System.out.println(tp.getListOfFollowedStreams("nuamor"));
    }

}
