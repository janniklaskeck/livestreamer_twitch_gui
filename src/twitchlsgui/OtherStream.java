package twitchlsgui;

/**
 * Class for handling all streams other than Twitch.tv streams
 * 
 * @author Niklas 21.01.2015
 * 
 */
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
