/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.model.generic;

import app.lsgui.model.IChannel;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.util.Callback;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class GenericChannel implements IChannel {

    private StringProperty nameProperty;
    private BooleanProperty onlineProperty;
    private BooleanProperty hasReminder = new SimpleBooleanProperty();
    private static final ListProperty<String> availableQualities = new SimpleListProperty<>(
            FXCollections.observableArrayList("Worst", "Best"));

    public GenericChannel(final String name) {
        this.nameProperty = new SimpleStringProperty(name);
        this.onlineProperty = new SimpleBooleanProperty(false);
    }

    @Override
    public StringProperty getName() {
        return this.nameProperty;
    }

    @Override
    public BooleanProperty isOnline() {
        return this.onlineProperty;
    }

    @Override
    public ListProperty<String> getAvailableQualities() {
        return availableQualities;
    }

    @Override
    public BooleanProperty hasReminder() {
        return this.hasReminder;
    }

    @Override
    public void setReminder(final boolean hasReminder) {
        this.hasReminder.set(hasReminder);
    }

    public static Callback<IChannel, Observable[]> extractor() {
        return (IChannel sm) -> new Observable[] { ((GenericChannel) sm).getName(), ((GenericChannel) sm).isOnline() };
    }

}
