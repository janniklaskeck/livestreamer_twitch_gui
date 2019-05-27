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
package app.lsgui.gui.main.toolbar;

import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.main.list.ChannelList;
import app.lsgui.gui.settings.SettingsWindow;
import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchService;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.PopOverUtil;
import app.lsgui.utils.TwitchUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public final class TopToolBar extends ToolBar
{

    private static final Logger LOGGER = LoggerFactory.getLogger(TopToolBar.class);
    private static final BooleanProperty hasPopOver = new SimpleBooleanProperty(false);
    private static final double PREF_HEIGHT = 35.0D;

    private final Button importButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.USERS));
    private final Button addButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.PLUS));
    private final Button twitchBrowserButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.SEARCH));
    private final Button removeButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.MINUS));
    private final Button settingsButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.COG));

    private final QualityComboBox qualityComboBox = new QualityComboBox();
    private final ServiceComboBox serviceComboBox = new ServiceComboBox();
    private BorderPane contentBorderPane;
    private ChannelList channelList;
    private PopOver popOver;

    public TopToolBar()
    {
        this.setPrefHeight(PREF_HEIGHT);
        this.setMinHeight(PREF_HEIGHT);
        this.serviceComboBox.initialize(this::changeService);
        this.qualityComboBox.initialize();

        this.addButton.setOnAction(event -> this.addAction());
        this.removeButton.setOnAction(event -> this.removeAction());
        this.importButton.setOnAction(event -> this.importFollowedChannels());

        final ProgressIndicator updateProgressIndicator = new ProgressIndicator();
        updateProgressIndicator.setVisible(false);


        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(Region.USE_PREF_SIZE);

        this.settingsButton.setOnAction(event -> this.openSettings());

        this.setButtonToolTips();

        this.addItemAtEnd(this.serviceComboBox);
        this.addItemAtEnd(this.addButton);
        this.addItemAtEnd(this.removeButton);
        this.addItemAtEnd(this.importButton);
        this.addItemAtEnd(this.twitchBrowserButton);
        this.addItemAtEnd(new Separator());
        this.addItemAtEnd(updateProgressIndicator);
        this.addItemAtEnd(spacer);
        this.addItemAtEnd(new Separator());
        this.addItemAtEnd(this.qualityComboBox);
        this.addItemAtEnd(this.settingsButton);
    }

    private void setButtonToolTips()
    {
        final Tooltip addTooltip = new Tooltip("Add channels to the current service or add a new service");
        this.addButton.setTooltip(addTooltip);
        final Tooltip removeTooltip = new Tooltip("Remove the currently selected channel");
        this.removeButton.setTooltip(removeTooltip);
        final Tooltip importTooltip = new Tooltip("Import the channels followed by a Twitch.tv User");
        this.importButton.setTooltip(importTooltip);
        final Tooltip browserTooltip = new Tooltip("Open Twitch.tv Browser");
        this.twitchBrowserButton.setTooltip(browserTooltip);
        final Tooltip settingsTooltip = new Tooltip("Open Settings");
        this.settingsButton.setTooltip(settingsTooltip);
        final Tooltip qualityTooltip = new Tooltip("Stream Quality Settings");
        this.qualityComboBox.setTooltip(qualityTooltip);
    }

    private void addItemAtEnd(final Node element)
    {
        this.getItems().add(this.getItems().size(), element);
    }

    public void initialize(final BorderPane contentBoderPane, final ChannelList channelList)
    {
        this.contentBorderPane = contentBoderPane;
        this.channelList = channelList;
    }

    private void openSettings()
    {
        final SettingsWindow settingsWindow = new SettingsWindow(this.contentBorderPane.getScene().getWindow());
        settingsWindow.showAndWait();
    }

    private void addAction()
    {
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null)
        {

            final PopOver popOverNew = PopOverUtil.createAddDialog(this.addButton, service);
            if (hasPopOver.get() && this.popOver != null)
            {
                this.popOver.hide();
            }
            hasPopOver.bind(popOverNew.showingProperty());
            this.popOver = popOverNew;
        }
    }

    private void removeAction()
    {
        final IChannel channel = this.channelList.getListView().getSelectionModel().getSelectedItem();
        final IService service = this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (channel != null && service != null)
        {
            LsGuiUtils.removeChannelFromService(channel, service);
        }
        else if (channel == null && service != null && this.serviceComboBox.getItems().size() > 1)
        {
            this.serviceComboBox.getSelectionModel().select(0);
            LsGuiUtils.removeService(service);
        }
    }

    private void importFollowedChannels()
    {
        final TwitchService service = (TwitchService) this.serviceComboBox.getSelectionModel().getSelectedItem();
        if (service != null)
        {
            final PopOver popOverNew = PopOverUtil.createImportPopOver(this.importButton, service);
            if (hasPopOver.get() && this.popOver != null)
            {
                this.popOver.hide();
            }
            hasPopOver.bind(popOverNew.showingProperty());
            this.popOver = popOverNew;
        }
    }

    private void changeService(final IService newService)
    {
        LOGGER.debug("Change Service to {}", newService.getName().get());
        this.channelList.channelListProperty().bind(newService.getChannelProperty());
        this.channelList.getListView().setUserData(newService);
        if (TwitchUtils.isTwitchService(newService))
        {
            this.importButton.setDisable(false);
            this.twitchBrowserButton.setDisable(false);
        }
        else
        {
            this.importButton.setDisable(true);
            this.twitchBrowserButton.setDisable(true);
        }
    }

    public QualityComboBox getQualityComboBox()
    {
        return this.qualityComboBox;
    }

    public ServiceComboBox getServiceComboBox()
    {
        return this.serviceComboBox;
    }

}
