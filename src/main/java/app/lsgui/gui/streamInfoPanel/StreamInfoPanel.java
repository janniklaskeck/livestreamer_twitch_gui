package app.lsgui.gui.streamInfoPanel;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.ServiceModel;
import app.lsgui.model.StreamModel;
import app.lsgui.model.twitch.TwitchStreamModel;
import app.lsgui.utils.Utils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class StreamInfoPanel extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamInfoPanel.class);
    private static FXMLLoader loader;

    private ComboBox<ServiceModel> serviceComboBox;
    private ComboBox<String> qualityComboBox;

    private ObjectProperty<StreamModel> modelProperty;

    @FXML
    private CheckBox notifyCheckBox;

    @FXML
    private Label streamInfoLabel;

    @FXML
    private ImageView previewImageView;

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
        modelProperty.addListener(new ChangeListener<StreamModel>() {
            @Override
            public void changed(ObservableValue<? extends StreamModel> observable, StreamModel oldValue,
                    StreamModel newValue) {
                StreamModel valueStreamModel = newValue == null ? oldValue : newValue;
                if (valueStreamModel.getClass().equals(TwitchStreamModel.class)) {
                    previewImageView.imageProperty().bind(((TwitchStreamModel) valueStreamModel).getPreviewImage());
                    streamInfoLabel.textProperty().bind(((TwitchStreamModel) valueStreamModel).getDescription());
                } else {
                    streamInfoLabel.textProperty().bind(modelProperty.get().getName());
                }
            }
        });
    }

    public void setStream(StreamModel model) {
        modelProperty.setValue(model);
    }

    @FXML
    private void startStream() {
        if (modelProperty.get() != null) {
            Utils.startLivestreamer(serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get(),
                    modelProperty.get().getName().get(), qualityComboBox.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void recordStream() {
        Utils.recordLivestreamer("", "");
    }

    @FXML
    private void openChat() {

    }

    @FXML
    private void openInBrowser() {
        Utils.openURLInBrowser(
                serviceComboBox.getSelectionModel().getSelectedItem().getUrl().get() + modelProperty.get().getName().get());
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
}
