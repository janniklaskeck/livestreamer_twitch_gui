package app.lsgui.browser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.ListIterator;

import org.controlsfx.control.GridView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.game.TwitchGames;
import javafx.collections.FXCollections;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class BrowserCore {

    private static BrowserCore instance;

    private static final LinkedList<String> history = new LinkedList<>();
    private ListIterator<String> current = history.listIterator();
    private String home = "";
    private GridView<ITwitchItem> gridView;

    private BrowserCore() {
    }

    public static BrowserCore getInstance() {
        if (instance == null) {
            instance = new BrowserCore();
        }
        return instance;
    }

    public void setGridView(final GridView<ITwitchItem> displayGridView) {
        gridView = displayGridView;
    }

    public void setHome(final String newHome) {
        if (!"".equals(home) && home != null) {
            history.removeFirst();
        }
        home = newHome;
        history.addFirst(home);
    }

    /**
     * Go to main directory page
     */
    public void goToHome() {
        final FileInputStream fis;
        try {
            fis = new FileInputStream(new File(getClass().getClassLoader().getResource("gamesDump.json").getPath()));
            final InputStreamReader isr = new InputStreamReader(fis);
            final BufferedReader bufferedReader = new BufferedReader(isr);
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            final Gson g = new Gson();
            final JsonObject jo = g.fromJson(sb.toString(), JsonObject.class);
            final TwitchGames games = new TwitchGames(jo);
            //new TwitchGamesUpdateService(games).start();
            gridView.setItems(games.getGames());
        } catch (IOException e) {

        }
    }

    /**
     * Refresh the current Page
     */
    public void refresh() {
        gridView.setItems(null);

    }

    /**
     * Goes one page forward
     */
    public void forward() {
        if (current.hasNext()) {
            current.next();
        }
    }

    /**
     * Goes one page back
     */
    public void backward() {
        if (current.hasPrevious()) {
            current.previous();
        }
    }

    public void openGame(final String name) {
        gridView.setItems(FXCollections.observableArrayList());
    }

}
