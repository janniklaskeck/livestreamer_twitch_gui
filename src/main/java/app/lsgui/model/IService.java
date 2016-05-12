package app.lsgui.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;

public interface IService {

    public StringProperty getName();

    public StringProperty getUrl();

    public ListProperty<IChannel> getChannels();

    public void addChannel(final String channelName);

    public void removeChannel(final IChannel channel);

}
