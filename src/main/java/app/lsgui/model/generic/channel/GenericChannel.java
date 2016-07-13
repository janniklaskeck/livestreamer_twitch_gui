package app.lsgui.model.generic.channel;

import java.util.Arrays;
import java.util.List;

import app.lsgui.model.channel.IChannel;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class GenericChannel implements IChannel {

    private StringProperty nameProperty;
    private BooleanProperty onlineProperty;

    /**
     *
     * @param name
     */
    public GenericChannel(final String name) {
	this.nameProperty = new SimpleStringProperty();
	this.onlineProperty = new SimpleBooleanProperty();
	this.nameProperty.setValue(name);
	this.onlineProperty.setValue(false);
    }

    @Override
    public StringProperty getName() {
	return nameProperty;
    }

    @Override
    public BooleanProperty isOnline() {
	return onlineProperty;
    }

    @Override
    public List<String> getAvailableQualities() {
	return Arrays.asList("Worst", "Best");
    }

    /**
     *
     * @return
     */
    public static Callback<IChannel, Observable[]> extractor() {
	return (IChannel sm) -> new Observable[] { ((GenericChannel) sm).getName(), ((GenericChannel) sm).isOnline() };
    }
}
