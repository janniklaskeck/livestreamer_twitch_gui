package app.lsgui.gui.chat;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TextArea;

public class ChatListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatListener.class);

    private TextArea cta;

    public ChatListener(final TextArea chatTextArea) {
        cta = chatTextArea;
        LOGGER.info("ChatClient init");
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        cta.appendText(event.getUser().getNick() + ": " + event.getMessage() + "\n");
    }

}
