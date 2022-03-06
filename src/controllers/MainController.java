package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MainController implements Initializable {
    @FXML
    private ImageView clearSearch;
    @FXML
    private TextField search;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearSearch.setOpacity(0);
        clearSearch.setOnMouseClicked((event -> search.clear()));
        search.focusedProperty().addListener(new ChangeListener<Boolean>() {
            FadeTransition ft = new FadeTransition(Duration.millis(400), clearSearch);

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                    Boolean newPropertyValue) {
                if (newPropertyValue) {
                    ft.setFromValue(0.0);
                    ft.setToValue(0.7);
                    ft.play();
                } else {
                    ft.setFromValue(0.7);
                    ft.setToValue(0.0);
                    ft.play();
                }
            }
        });
    }
}
