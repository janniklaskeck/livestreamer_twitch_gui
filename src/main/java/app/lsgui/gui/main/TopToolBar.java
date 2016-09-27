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
package app.lsgui.gui.main;

import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.main.list.ChannelList;
import app.lsgui.gui.settings.SettingsWindow;
import app.lsgui.gui.twitchbrowser.BrowserWindow;
import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchService;
import app.lsgui.remote.twitch.TwitchChannelUpdateService;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.PopOverUtil;
import app.lsgui.utils.TwitchUtils;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public final class TopToolBar extends ToolBar {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopToolBar.class);
    private static final BooleanProperty hasPopOver = new SimpleBooleanProperty(false);
    private static final double PREF_HEIGHT = 35.0D;

    private final Button importButton = GlyphsDude.createIconButton(FontAwesomeIcon.USERS);
    private final Button addButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS);
    private final Button twitchBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.SEARCH);

    private QualityComboBox qualityComboBox = new QualityComboBox();
    private ServiceComboBox serviceComboBox = new ServiceComboBox();
    private BorderPane contentBorderPane;
    private ChannelList channelList;
    private PopOver popOver;

    public TopToolBar() {
        setPrefHeight(PREF_HEIGHT);
        setMinHeight(PREF_HEIGHT);
        this.serviceComboBox.initialize(this::changeService);
        this.qualityComboBox.initialize();
        final Button removeButton = GlyphsDude.createIconButton(FontAwesomeIcon.MINUS);

        this.addButton.setOnAction(event -> this.addAction());
        removeButton.setOnAction(event -> this.removeAction());
        this.importButton.setOnAction(event -> this.importFollowedChannels());
        this.twitchBrowserButton.setOnAction(event -> this.openTwitchBrowser());

        final ProgressIndicator updateProgressIndicator = new ProgressIndicator();
        updateProgressIndicator.setVisible(false);
        TwitchChannelUpdateService.getActiveChannelServicesProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                updateProgressIndicator.setVisible(true);
            } else {
                updateProgressIndicator.setVisible(false);
            }
        });

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(Region.USE_PREF_SIZE);

        final Button settingsButton = GlyphsDude.createIconButton(FontAwesomeIcon.COG);
        settingsButton.setOnAction(event -> this.openSettings());

        this.addItemAtEnd(this.serviceComboBox);
        this.addItemAtEnd(this.addButton);
        this.addItemAtEnd(removeButton);
        this.addItemAtEnd(this.importButton);
        this.addItemAtEnd(this.twitchBrowserButton);
        this.addItemAtEnd(new Separator());
        this.addItemAtEnd(updateProgressIndicator);
        this.addItemAtEnd(spacer);
        this.addItemAtEnd(new Separator());
        this.addItemAtEnd(this.qualityComboBox);
        this.addItemAtEnd(settingsButton);
    }

    private void addItemAtEnd(final Node element) {
        getItems().add(getItems().size(), element);
    }

    public void initialize(final BorderPane contentBoderPane, final ChannelList channelList) {
        this.contentBorderPane = contentBoderPane;
        this.channelList = channelList;
    }

    private void openSettings() {
        final SettingsWindow settingsWindow = new SettingsWindow(this.contentBorderPane.getScene().getWindow());
        settingsWindow.showAndWait();
    }

    private void openTwitchBrowser() {
        final BrowserWindow browserWindow = new BrowserWindow(this.twitchBrowserButton.getScene().getWindow());
        browserWindow.showAndWait();
    }

    private void addAction() {
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null) {

            final PopOver popOverNew = PopOverUtil.createAddDialog(this.addButton, service);
            if (hasPopOver.get() && this.popOver != null) {
                this.popOver.hide();
            }
            hasPopOver.bind(popOverNew.showingProperty());
            this.popOver = popOverNew;
        }
    }

    private void removeAction() {
        final IChannel channel = this.channelList.getListView().getSelectionModel().getSelectedItem();
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (channel != null && service != null) {
            LsGuiUtils.removeChannelFromService(channel, service);
        } else if (channel == null && service != null && this.serviceComboBox.getItems().size() > 1) {
            this.serviceComboBox.getSelectionModel().select(0);
            LsGuiUtils.removeService(service);
        }
    }

    private void importFollowedChannels() {
        final TwitchService service = (TwitchService) this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null) {
            final PopOver popOverNew = PopOverUtil.createImportPopOver(this.importButton, service);
            if (hasPopOver.get() && this.popOver != null) {
                this.popOver.hide();
            }
            hasPopOver.bind(popOverNew.showingProperty());
            this.popOver = popOverNew;
        }
    }

    private void changeService(final IService newService) {
        LOGGER.debug("Change Service to {}", newService.getName().get());
        this.channelList.getStreams().bind(newService.getChannelProperty());
        this.channelList.getListView().setUserData(newService);
        if (TwitchUtils.isTwitchService(newService)) {
            this.importButton.setDisable(false);
            this.twitchBrowserButton.setDisable(false);
        } else {
            this.importButton.setDisable(true);
            this.twitchBrowserButton.setDisable(true);
        }
    }

    public QualityComboBox getQualityComboBox() {
        return this.qualityComboBox;
    }

    public ServiceComboBox getServiceComboBox() {
        return this.serviceComboBox;
    }

}
