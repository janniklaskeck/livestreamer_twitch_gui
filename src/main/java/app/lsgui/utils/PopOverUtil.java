package app.lsgui.utils;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchService;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public final class PopOverUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PopOverUtil.class);
    private static final int CORDER_RADIUS = 4;

    private PopOverUtil() {
    }

    public static PopOver createAddDialog(final Node root, final IService service) {
        final PopOver popOver = new PopOver();
        popOver.getRoot().getStylesheets().add(PopOverUtil.class
                .getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        final Scene scene = root.getScene();
        final Point2D nodeCoord = root.localToScene(0.0, 25.0);
        final Window sceneWindow = scene.getWindow();
        final Point2D windowCoord = new Point2D(sceneWindow.getX(), sceneWindow.getY());
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final double clickX = Math.round(windowCoord.getX() + sceneCoord.getY() + nodeCoord.getX());
        final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());

        final Insets inset = new Insets(8);

        final VBox dialogBox = new VBox();
        dialogBox.setPadding(inset);

        final HBox buttonBox = new HBox();
        final Button submitButton = new Button("Submit");
        final Button cancelButton = new Button("Cancel");

        final HBox nameBox = new HBox();
        final Label nameLabel = new Label("Name ");
        final TextField nameTextField = new TextField();
        nameBox.getChildren().add(nameLabel);
        nameBox.getChildren().add(nameTextField);

        final HBox urlBox = new HBox();
        final Label urlLabel = new Label("URL ");
        final TextField urlTextField = new TextField();
        urlBox.getChildren().add(urlLabel);
        urlBox.getChildren().add(urlTextField);

        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(cancelButton);

        final Button addChannelButton = new Button("Add Channel");
        final Button addServiceButton = new Button("Add Service");
        addChannelButton.setOnAction(event -> {
            dialogBox.getChildren().clear();
            dialogBox.getChildren().add(nameBox);
            dialogBox.getChildren().add(buttonBox);
        });

        addServiceButton.setOnAction(event -> {
            dialogBox.getChildren().clear();
            dialogBox.getChildren().add(nameBox);
            dialogBox.getChildren().add(urlBox);
            dialogBox.getChildren().add(buttonBox);
        });

        submitButton.setOnAction(event -> {
            if (dialogBox.getChildren().contains(urlBox)) {
                final String serviceName = nameTextField.getText();
                final String serviceUrl = urlTextField.getText();
                LOGGER.info("Adding service");
                LsGuiUtils.addService(serviceName, serviceUrl);
            } else {
                final String channelName = nameTextField.getText();
                LOGGER.info("Adding channel");
                LsGuiUtils.addChannelToService(channelName, service);
            }
            popOver.hide();
        });
        submitButton.setDefaultButton(true);

        cancelButton.setOnAction(event -> popOver.hide());
        dialogBox.getChildren().add(addChannelButton);
        dialogBox.getChildren().add(addServiceButton);

        popOver.setContentNode(dialogBox);
        popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
        popOver.setCornerRadius(CORDER_RADIUS);
        popOver.setTitle("Add new Channel or Service");
        popOver.show(root.getParent(), clickX, clickY);
        return popOver;
    }

    public static PopOver createImportPopOver(final Node root, final TwitchService service) {
        final PopOver popOver = new PopOver();
        popOver.getRoot().getStylesheets().add(PopOverUtil.class
                .getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        final Scene scene = root.getScene();

        final Window sceneWindow = scene.getWindow();
        final Point2D windowCoord = new Point2D(sceneWindow.getX(), sceneWindow.getY());
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final Point2D nodeCoord = root.localToScene(0.0, 25.0);
        final double clickX = Math.round(windowCoord.getX() + sceneCoord.getY() + nodeCoord.getX());
        final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());

        final Insets inset = new Insets(8);

        final VBox dialogBox = new VBox();
        dialogBox.setPadding(inset);

        final HBox buttonBox = new HBox();
        final Button submitButton = new Button("Import");
        final Button cancelButton = new Button("Cancel");
        final TextField nameTextField = new TextField();

        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(cancelButton);

        dialogBox.getChildren().add(nameTextField);
        dialogBox.getChildren().add(buttonBox);

        submitButton.setOnAction(event -> {
            final String username = nameTextField.getText();
            TwitchUtils.addFollowedChannelsToService(username, service);
            popOver.hide();
        });
        submitButton.setDefaultButton(true);

        cancelButton.setOnAction(event -> popOver.hide());

        popOver.setContentNode(dialogBox);
        popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
        popOver.setCornerRadius(CORDER_RADIUS);
        popOver.setTitle("Import followed Twitch.tv Channels");
        popOver.show(root.getParent(), clickX, clickY);
        return popOver;
    }
}
