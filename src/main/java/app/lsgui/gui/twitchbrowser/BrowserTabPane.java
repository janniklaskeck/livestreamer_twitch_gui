package app.lsgui.gui.twitchbrowser;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TabPane;

public class BrowserTabPane extends TabPane {

    public BrowserTabPane() {
        super();
    }

    public ObservableList<BrowserTab> getBrowserTabs() {
        final List<BrowserTab> browserTabs = getTabs().stream().map(tab -> (BrowserTab) tab)
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(browserTabs);
    }

}
