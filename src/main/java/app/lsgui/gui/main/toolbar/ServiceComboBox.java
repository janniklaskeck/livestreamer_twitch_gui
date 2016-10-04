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
package app.lsgui.gui.main.toolbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchService;
import app.lsgui.utils.Settings;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public final class ServiceComboBox extends ComboBox<IService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceComboBox.class);

    public ServiceComboBox() {
        // Empty Constructor
    }

    public void initialize(final ServiceOperator serviceOperator) {
        if (Settings.getInstance().servicesProperty().isEmpty()) {
            Settings.getInstance().servicesProperty().add(new TwitchService("Twitch.tv", "http://twitch.tv/"));
        }
        itemsProperty().bind(Settings.getInstance().servicesProperty());
        setCellFactory(listView -> new ServiceCell());
        setConverter(new StringConverter<IService>() {
            @Override
            public String toString(final IService service) {
                if (service == null) {
                    LOGGER.error("Service is null");
                    return "";
                }
                final String name = service.getName().get();
                LOGGER.trace("Display {} as Service name", name);
                return name;
            }

            @Override
            public IService fromString(final String string) {
                return null;
            }
        });
        getSelectionModel().select(0);
        valueProperty().addListener((observable, oldValue, newValue) -> serviceOperator.changeService(newValue));
    }
}
