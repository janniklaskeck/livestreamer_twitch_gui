package app.lsgui.gui.chat;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ChatListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatListener.class);

    private TextArea cta;
    private String channel;

    public ChatListener(final TextArea chatTextArea) {
        cta = chatTextArea;
        LOGGER.info("ChatClient init");
    }

    @Override
    public void onEvent(Event event) {

        LOGGER.info("Messege {}", event.getClass());
        // Platform.runLater(() -> cta.appendText(event.getUser() + ": " +
        // event.getMessage() + "\n"));
    }

}
