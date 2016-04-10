package app.lsgui.gui.chat;

import org.jibble.pircbot.PircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TextArea;

public class ChatClient extends PircBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClient.class);

    private TextArea cta;
    private String channel;

    public ChatClient(final String user, final TextArea chatTextArea) {
        cta = chatTextArea;
        LOGGER.info("ChatClient init");
        setName(user);
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        LOGGER.info("Messege send to {} from {}", channel, sender);
        cta.appendText(sender + ": " + message + "\n");
    }

    public void setUserName(final String name) {
        setName(name);
    }

    @Override
    protected void onConnect() {
        super.onConnect();
        LOGGER.info("Connection Successful");
    }

    @Override
    protected void onDisconnect() {
        super.onDisconnect();
        LOGGER.info("Disconnected");
    }

    /**
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }
}
