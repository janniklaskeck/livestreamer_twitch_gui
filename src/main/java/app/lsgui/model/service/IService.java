package app.lsgui.model.service;

import app.lsgui.model.channel.IChannel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public interface IService {

    /**
     *
     * @return
     */
    public StringProperty getName();

    /**
     *
     * @return
     */
    public StringProperty getUrl();

    /**
     *
     * @return
     */
    public ListProperty<IChannel> getChannels();

    /**
     *
     * @param channelName
     */
    public void addChannel(final String channelName);

    /**
     *
     * @param channel
     */
    public void removeChannel(final IChannel channel);

}
