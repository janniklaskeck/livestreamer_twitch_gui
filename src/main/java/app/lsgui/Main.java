package app.lsgui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainWindow;
import javafx.application.Application;

public class Main {

    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

        LOGGER.debug("PRE-Launch finished");

        Application.launch(MainWindow.class, args);

    }

}
