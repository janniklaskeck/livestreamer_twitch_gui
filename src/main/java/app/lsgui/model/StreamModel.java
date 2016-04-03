package app.lsgui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

public interface StreamModel {

    public StringProperty getName();
    public BooleanProperty isOnline();

}
