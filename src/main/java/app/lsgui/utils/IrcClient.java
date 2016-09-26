/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.utils;

import java.util.Locale;

import org.fxmisc.richtext.InlineCssTextArea;
import org.jibble.pircbot.PircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.chat.ChatController;
import javafx.application.Platform;

public final class IrcClient extends PircBot {

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
            final int start = this.chatTextArea.getText().length();
            final int end = start + sender.length() + 1;
            this.chatTextArea.appendText(sender + ": " + message + "\n");
            ChatController.setColoredNickName(this.chatTextArea, start, end);
            ChatController.setChatMessageStyle(this.chatTextArea, end, end + message.length() + 1);
        });
    }

    public void setUserName(String name) {
        this.setName(name);
    }

    @Override
    protected void onConnect() {
        this.sendRawLine("CAP REQ :twitch.tv/membership");
        joinChannel("#" + this.channel.toLowerCase(Locale.ENGLISH));
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }

    @Override
    public boolean equals(final Object other) {
        boolean result = false;
        if (other == null) {
            result = false;
        } else {
            if (other == this) {
                return true;
            }
            if (this.getClass() != other.getClass()) {
                result = false;
            } else {
                final IrcClient otherClient = (IrcClient) other;
                if (this.channel.equalsIgnoreCase(otherClient.channel)) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.channel.hashCode();
    }

}
