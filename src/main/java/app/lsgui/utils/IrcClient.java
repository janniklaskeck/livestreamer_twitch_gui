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
