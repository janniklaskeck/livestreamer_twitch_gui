package app.lsgui.gui;

import app.lsgui.model.ServiceModel;
import javafx.scene.control.ListCell;

public class ServiceCell extends ListCell<ServiceModel> {

    @Override
    protected void updateItem(ServiceModel item, boolean isEmpty) {
        super.updateItem(item, isEmpty);

        if (isEmpty || item == null) {
            setText(null);
        } else {
            setText(item.getName().get());
        }
    }
}
