package app.lsgui.model.channel;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public interface IChannel {

    public StringProperty getName();

    public BooleanProperty isOnline();

    public BooleanProperty hasReminder();

    public void setReminder(final boolean hasReminder);

    public List<String> getAvailableQualities();

}
