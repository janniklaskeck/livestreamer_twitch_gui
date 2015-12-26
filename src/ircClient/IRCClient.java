package ircClient;

import org.jibble.pircbot.PircBot;

public class IRCClient extends PircBot {

    private IRCClientFrame parent;

    public IRCClient(IRCClientFrame parent, String login) {
	this.setName(login);
	this.parent = parent;
    }

    public IRCClient(IRCClientFrame parent) {
	this.setName("");
	this.parent = parent;
    }

    public void onMessage(String channel, String sender, String login,
	    String hostname, String message) {
	parent.addMessage(sender, message);
    }

    public void setUserName(String name) {
	this.setName(name);
    }

    @Override
    protected void onConnect() {
        super.onConnect();
        System.out.println("Connection successful");
    }
    @Override
    protected void onDisconnect() {
        super.onDisconnect();
        System.out.println("Disconnected");
    }
}
