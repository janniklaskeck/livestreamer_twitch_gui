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

    /**
     *
     * @return
     */
    public StringProperty getName();

    /**
     *
     * @return
     */
    public BooleanProperty isOnline();

    /**
     *
     * @return
     */
    public List<String> getAvailableQualities();

}
