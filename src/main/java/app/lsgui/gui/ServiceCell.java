package app.lsgui.gui;

import app.lsgui.model.service.IService;
import javafx.scene.control.ListCell;

public class ServiceCell extends ListCell<IService> {// NOSONAR

    @Override
    protected void updateItem(IService item, boolean isEmpty) {
        super.updateItem(item, isEmpty);
        if (isEmpty || item == null) {
            setText(null);
        } else {
            setText(item.getName().get());
        }
    }
}
