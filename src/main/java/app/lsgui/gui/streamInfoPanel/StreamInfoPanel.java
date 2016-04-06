package app.lsgui.gui.streamInfoPanel;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.MainWindow;
import app.lsgui.model.ServiceModel;
import app.lsgui.model.StreamModel;
import app.lsgui.model.twitch.TwitchStreamModel;
import app.lsgui.utils.Utils;
import app.lsgui.utils.WrappedImageView;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class StreamInfoPanel extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamInfoPanel.class);
    private static FXMLLoader loader;

    private ComboBox<ServiceModel> serviceComboBox;
    private ComboBox<String> qualityComboBox;

    private ObjectProperty<StreamModel> modelProperty;

    private WrappedImageView previewImageView;

    private Label streamDescription;
    private Label streamUptime;
    private Label streamViewers;
    private Label streamGame;

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private CheckBox notifyCheckBox;

    @FXML
    private GridPane descriptionGrid;

    @FXML
    private ToolBar buttonBox;

    public StreamInfoPanel(ComboBox<ServiceModel> serviceComboBox, ComboBox<String> qualityComboBox) {
        LOGGER.debug("Construct StreamInfoPanel");
        modelProperty = new SimpleObjectProperty<StreamModel>();

        this.serviceComboBox = serviceComboBox;
        this.qualityComboBox = qualityComboBox;
        loader = new FXMLLoader(getClass().getResource("/StreamInfoPanel.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        previewImageView = new WrappedImageView(null);
        rootBorderPane.setCenter(previewImageView);

        streamDescription = new Label();
        streamDescription.setWrapText(true);
        streamUptime = new Label();
        streamViewers = new Label();
        streamGame = new Label();

        descriptionGrid.add(streamGame, 0, 0, 1, 1);
        descriptionGrid.add(streamViewers, 0, 1, 1, 1);
        descriptionGrid.add(streamUptime, 0, 2, 1, 1);
        descriptionGrid.add(streamDescription, 0, 3, 1, 1);

        modelProperty.addListener(new ChangeListener<StreamModel>() {
            @Override
            public void changed(ObservableValue<? extends StreamModel> observable, StreamModel oldValue,
                    StreamModel newValue) {
                StreamModel valueStreamModel = newValue == null ? oldValue : newValue;
                if (valueStreamModel.getClass().equals(TwitchStreamModel.class)) {
                    previewImageView.imageProperty().bind(((TwitchStreamModel) valueStreamModel).getPreviewImage());
                    streamDescription.textProperty().bind(((TwitchStreamModel) valueStreamModel).getDescription());

                    streamUptime.textProperty().bind(((TwitchStreamModel) valueStreamModel).getUptimeString());
                    streamUptime.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CLOCK_ALT));

                    streamViewers.textProperty().bind(((TwitchStreamModel) valueStreamModel).getViewersString());
                    streamViewers.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER));

                    streamGame.textProperty().bind(((TwitchStreamModel) valueStreamModel).getGame());
                    streamGame.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.GAMEPAD));
                } else {
                    streamDescription.textProperty().bind(modelProperty.get().getName());
                }
            }
        });

        Button startStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.PLAY);
        startStreamButton.setOnAction((event) -> {
            startStream();
        });

        Button recordStreamButton = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD);
        recordStreamButton.setOnAction((event) -> {
            recordStream();
        });

        Button openChatButton = GlyphsDude.createIconButton(FontAwesomeIcon.COMMENT);
        openChatButton.setOnAction((event) -> {
            openChat();
        });

        Button openBrowserButton = GlyphsDude.createIconButton(FontAwesomeIcon.EDGE);
        openBrowserButton.setOnAction((event) -> {
            openBrowser();
        });

        buttonBox.getItems().add(startStreamButton);
        buttonBox.getItems().add(recordStreamButton);
        buttonBox.getItems().add(openChatButton);
        buttonBox.getItems().add(openBrowserButton);
    }

    public void setStream(StreamModel model) {
        modelProperty.setValue(model);
    }

    private void startStream() {
        if (modelProperty.get() != null) {
            String URL = buildURL();
            String quality = buildQuality();
            Utils.startLivestreamer(URL, quality);
        }
    }

    private void recordStream() {
        final String URL = buildURL();
        final String quality = buildQuality();

        FileChooser recordFileChooser = new FileChooser();
        recordFileChooser.setTitle("Choose Target file");
        recordFileChooser.getExtensionFilters().add(new ExtensionFilter("MPEG4", ".mpeg4"));
        final File recordFile = recordFileChooser.showSaveDialog(MainWindow.getRootStage());
        Utils.recordLivestreamer(URL, quality, recordFile);
    }

    private void openChat() {

    }

    private void openBrowser() {
        Utils.openURLInBrowser(serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get()
                + modelProperty.get().getName().get());
    }

    /**
     * @return the modelProperty
     */
    public ObjectProperty<StreamModel> getModelProperty() {
        return modelProperty;
    }

    /**
     * @param modelProperty
     *            the modelProperty to set
     */
    public void setModelProperty(ObjectProperty<StreamModel> modelProperty) {
        this.modelProperty = modelProperty;
    }

    private String buildURL() {
        return serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get()
                + modelProperty.get().getName().get();
    }

    private String buildQuality() {
        return qualityComboBox.getSelectionModel().getSelectedItem();
    }
}
