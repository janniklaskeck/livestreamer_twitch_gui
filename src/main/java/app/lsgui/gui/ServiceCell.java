package app.lsgui.gui;

import app.lsgui.model.Service;
import javafx.scene.control.ListCell;

public class ServiceCell extends ListCell<Service> {// NOSONAR

    @Override
    protected void updateItem(Service item, boolean isEmpty) {
        super.updateItem(item, isEmpty);
        if (isEmpty || item == null) {
            setText(null);
        } else {
            setText(item.getName().get());
        }
    }
}
