package app.lsgui.gui.main;

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
        if (Settings.getInstance().getStreamServices().isEmpty()) {
            Settings.getInstance().getStreamServices().add(new TwitchService("Twitch.tv", "http://twitch.tv/"));
        }
        itemsProperty().bind(Settings.getInstance().getStreamServices());
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
