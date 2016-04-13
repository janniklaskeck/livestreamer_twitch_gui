package app.lsgui.gui.chat;

import org.fxmisc.richtext.InlineCssTextArea;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.utils.Utils;
import javafx.application.Platform;

public class ChatListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatListener.class);

    private InlineCssTextArea cta;

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
            cta.setStyle(start, end,
                    "-fx-fill: " + Utils.getColorFromString(event.getUser().getNick()) + "; -fx-font-size: 12pt");
            cta.setStyle(end, end + event.getMessage().length() + 1, "-fx-font-size: 12pt");
        });
    }

}
