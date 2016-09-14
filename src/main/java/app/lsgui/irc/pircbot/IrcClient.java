package app.lsgui.irc.pircbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IrcClient extends PircBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(IrcClient.class);

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        LOGGER.debug("{} {} {}", channel, sender, message);
    }

    public void setUserName(String name) {
        this.setName(name);
    }

    @Override
    protected void onConnect() {
        this.sendRawLine("CAP REQ :twitch.tv/membership");
        this.sendRawLine("CAP REQ :twitch.tv/tags");
        this.sendRawLine("CAP REQ :twitch.tv/commands");
    }

    @Override
    protected void onUserList(String channel, User[] users) {
        LOGGER.debug("{}", users.length);
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        LOGGER.debug("{}", message);
    }

}
