package app.lsgui.gui.chat;

import org.fxmisc.richtext.InlineCssTextArea;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class ChatListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatListener.class);

    private InlineCssTextArea cta;

    /**
     *
     * @param chatTextArea
     */
    public ChatListener(final InlineCssTextArea chatTextArea) {
        cta = chatTextArea;
        LOGGER.info("ChatClient init");
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        Platform.runLater(() -> {
            final int start = cta.getText().length();
            final int end = start + event.getUser().getNick().length() + 1;
            cta.appendText(event.getUser().getNick() + ": " + event.getMessage() + "\n");
            ChatController.setColoredNickName(cta, start, end);
            ChatController.setChatMessageStyle(cta, end, end + event.getMessage().length() + 1);
        });
    }

}
