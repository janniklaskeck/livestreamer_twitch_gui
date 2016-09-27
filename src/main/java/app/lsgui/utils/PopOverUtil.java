/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
    private static final Insets INSETS = new Insets(8);

    private PopOverUtil() {
    }

    public static PopOver createAddDialog(final Node root, final IService service) {
        final PopOver popOver = createBasePopOver("Add new Channel or Service");
        final VBox dialogBox = new VBox();
        final HBox buttonBox = new HBox();
        final Button submitButton = new Button("Submit");
        final Button cancelButton = new Button("Cancel");
        final HBox nameBox = new HBox();
        final Label nameLabel = new Label("Name ");
        final TextField nameTextField = new TextField();
        final Button addChannelButton = new Button("Add Channel");
        final Button addServiceButton = new Button("Add Service");
        final HBox urlBox = new HBox();
        final Label urlLabel = new Label("URL ");
        final TextField urlTextField = new TextField();
        nameBox.getChildren().add(nameLabel);
        nameBox.getChildren().add(nameTextField);

        urlBox.getChildren().add(urlLabel);
        urlBox.getChildren().add(urlTextField);

        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(cancelButton);

        dialogBox.setPadding(INSETS);

        dialogBox.getChildren().add(addChannelButton);
        dialogBox.getChildren().add(addServiceButton);
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
                LsGuiUtils.addService(serviceName, serviceUrl);
            } else {
                final String channelName = nameTextField.getText();
                LsGuiUtils.addChannelToService(channelName, service);
            }
            popOver.hide();
        });
        submitButton.setDefaultButton(true);
        cancelButton.setOnAction(event -> popOver.hide());

        popOver.setContentNode(dialogBox);
        final Point2D clickedPoint = getClickedPoint(root);
        popOver.show(root.getParent(), clickedPoint.getX(), clickedPoint.getY());
        return popOver;
    }

    public static PopOver createImportPopOver(final Node root, final TwitchService service) {
        final PopOver popOver = createBasePopOver("Import followed Twitch.tv Channels");
        final VBox dialogBox = new VBox();
        final HBox buttonBox = new HBox();
        final Button submitButton = new Button("Import");
        final Button cancelButton = new Button("Cancel");
        final TextField nameTextField = new TextField();
        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(cancelButton);

        dialogBox.setPadding(INSETS);
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
        final Point2D clickedPoint = getClickedPoint(root);
        popOver.show(root.getParent(), clickedPoint.getX(), clickedPoint.getY());
        return popOver;
    }

    private static PopOver createBasePopOver(final String title) {
        final PopOver popOver = new PopOver();
        popOver.getRoot().getStylesheets().add(PopOverUtil.class
                .getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
        popOver.setCornerRadius(CORDER_RADIUS);
        popOver.setTitle(title);
        return popOver;
    }

    private static Point2D getClickedPoint(final Node root) {
        LOGGER.debug("Get clicked Point from Node root");
        final Scene scene = root.getScene();
        final Point2D nodeCoord = root.localToScene(0.0D, 25.0D);
        final Window sceneWindow = scene.getWindow();
        final Point2D windowCoord = new Point2D(sceneWindow.getX(), sceneWindow.getY());
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final double clickX = Math.round(windowCoord.getX() + sceneCoord.getY() + nodeCoord.getX());
        final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());
        LOGGER.debug("Return clicked Point(X: {}, Y: {}) from Node root", clickX, clickY);
        return new Point2D(clickX, clickY);
    }
}
