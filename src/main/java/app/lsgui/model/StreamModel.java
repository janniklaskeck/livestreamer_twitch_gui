package app.lsgui.model;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

public interface StreamModel {

    public StringProperty getName();

    public BooleanProperty isOnline();

    public List<String> getAvailableQualities();

}
