package app.lsgui.gui;

import app.lsgui.model.service.IService;
import javafx.scene.control.ListCell;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class ServiceCell extends ListCell<IService> {

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
