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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.main.infopanel.ChannelInfoPanel;
import app.lsgui.gui.main.list.ChannelList;
import app.lsgui.model.IService;
import app.lsgui.utils.Settings;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class LsGuiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGuiController.class);

    private ChannelList channelList = new ChannelList();
    private TopToolBar topToolBar = new TopToolBar();

    @FXML
    private BorderPane contentBorderPane;

    @FXML
    private BorderPane topBorderPane;

    public LsGuiController() {
        // Empty Constructor
    }

    @FXML
    public void initialize() {
        LOGGER.debug("Initialize Main Window");
        this.topBorderPane.setCenter(this.topToolBar);
        this.setupChannelList();
        this.setupChannelInfoPanel();
        this.topToolBar.initialize(this.contentBorderPane, this.channelList);
    }

    private void setupChannelList() {
        LOGGER.debug("Initialize ChannelList");
        this.channelList.getListView().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        final QualityComboBox qualityComboBox = this.topToolBar.getQualityComboBox();
                        qualityComboBox.itemsProperty().bind(newValue.getAvailableQualities());
                        if (qualityComboBox.getItems().size() > 1) {
                            final String quality = Settings.getInstance().getQuality().get();
                            if (qualityComboBox.getItems().contains(quality)) {
                                qualityComboBox.getSelectionModel().select(quality);
                            } else {
                                qualityComboBox.getSelectionModel().select("Best");
                            }
                        } else {
                            qualityComboBox.getSelectionModel().select(0);
                        }
                    }
                });
        final ServiceComboBox serviceComboBox = this.topToolBar.getServiceComboBox();
        final IService service = serviceComboBox.getSelectionModel().getSelectedItem();
        this.channelList.getStreams().bind(service.getChannelProperty());
        this.channelList.getListView().setUserData(service);
        this.contentBorderPane.setLeft(this.channelList);
    }

    private void setupChannelInfoPanel() {
        LOGGER.debug("Initialize ChannelInfoPanel");
        final ServiceComboBox serviceComboBox = this.topToolBar.getServiceComboBox();
        final QualityComboBox qualityComboBox = this.topToolBar.getQualityComboBox();
        final ChannelInfoPanel channelInfoPanel = new ChannelInfoPanel(serviceComboBox, qualityComboBox);
        channelInfoPanel.getChannelProperty().bind(this.channelList.getSelectedChannelProperty());
        this.contentBorderPane.setCenter(channelInfoPanel);
    }

}
