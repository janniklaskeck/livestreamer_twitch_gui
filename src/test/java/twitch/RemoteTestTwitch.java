package twitch;

import org.junit.Test;

import app.lsgui.service.twitch.TwitchProcessor;

public class RemoteTestTwitch {

    TwitchProcessor tp = TwitchProcessor.instance();

    @Test
    public void test() {
        System.out.println(tp.getListOfFollowedStreams("nuamor"));
    }

}
