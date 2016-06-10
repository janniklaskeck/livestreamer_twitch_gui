package app.lsgui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainWindow;
import javafx.application.Application;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
	System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

	LOGGER.debug("Pre-Launch finished");

	Application.launch(MainWindow.class, args);
    }
}
