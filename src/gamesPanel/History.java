package gamesPanel;

import java.awt.Component;
import java.util.ArrayList;

public class History {

    private int currentView;
    private ArrayList<Component> history;

    public History() {
	currentView = 0;
	history = new ArrayList<Component>();
    }

    public Component getBackView() {
	if (currentView > 0) {
	    currentView--;
	    return history.get(currentView);
	}
	return null;
    }

    public Component getForwardView() {
	if (history.get(currentView + 1) != null) {
	    currentView++;
	    return history.get(currentView);
	}
	return null;
    }

    public void addView(Component c) {
	history.add(currentView, c);
	currentView++;
    }

    public void setView(Component c, int index) {
	if (history.size() == 0) {
	    history.add(index, c);
	} else {
	    history.set(index, c);
	}
    }
}
