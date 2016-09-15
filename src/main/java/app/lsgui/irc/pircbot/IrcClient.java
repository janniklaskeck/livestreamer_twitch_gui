package app.lsgui.irc.pircbot;

import java.util.Locale;

import org.fxmisc.richtext.InlineCssTextArea;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.chat.ChatController;
import javafx.application.Platform;

public class IrcClient extends PircBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(IrcClient.class);
    private String channel;
    private InlineCssTextArea chatTextArea;

    public IrcClient(final InlineCssTextArea chatTextArea) {
        this.chatTextArea = chatTextArea;
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        LOGGER.trace("{} {} {}", channel, sender, message);
        Platform.runLater(() -> {
            final int start = chatTextArea.getText().length();
            final int end = start + sender.length() + 1;
            chatTextArea.appendText(sender + ": " + message + "\n");
            ChatController.setColoredNickName(chatTextArea, start, end);
            ChatController.setChatMessageStyle(chatTextArea, end, end + message.length() + 1);
        });
    }

    public void setUserName(String name) {
        this.setName(name);
    }

    @Override
    protected void onConnect() {
        this.sendRawLine("CAP REQ :twitch.tv/membership");
        joinChannel("#" + channel.toLowerCase(Locale.ENGLISH));
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
            String notice) {
        LOGGER.debug("{}", notice);
    }

    @Override
    protected void onUserList(String channel, User[] users) {
        LOGGER.debug("{}", users.length);
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        LOGGER.debug("{}", message);
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

}
