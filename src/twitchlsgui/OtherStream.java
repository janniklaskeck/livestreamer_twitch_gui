package twitchlsgui;

public class OtherStream implements GenericStream {

    private String channel;

    public OtherStream(String channel) {
	this.channel = channel;
    }

    @Override
    public String getChannel() {
	return channel;
    }

    @Override
    public void setChannel(String channel) {
	this.channel = channel;
    }

}
