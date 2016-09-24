package app.lsgui.gui.main;

import app.lsgui.model.IService;

@FunctionalInterface
public interface ServiceOperator {

    void changeService(final IService service);

}
