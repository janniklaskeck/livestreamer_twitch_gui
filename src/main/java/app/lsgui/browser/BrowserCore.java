package app.lsgui.browser;

import java.util.LinkedList;
import java.util.ListIterator;

import org.controlsfx.control.GridView;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.game.TwitchGames;

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
        final TwitchGames games = new TwitchGames();
        gridView.setItems(games.getGames());
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

}
