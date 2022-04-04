package helpers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class GUI {
    private static final String VIEW_PATH = "view";

    private static final float INITIAL_BUTTON_OPACITY = 0.7f;
    private static final float BUTTON_INITIAL_SCALE = 0.9f;
    public static final int BUTTON_ANIMATION_DURATION = 200;

    public static void decorateBtn(Node button, EventHandler<? super MouseEvent> clickHandler) {
        button.setCursor(Cursor.HAND);
        button.setOpacity(INITIAL_BUTTON_OPACITY);
        button.setScaleX(BUTTON_INITIAL_SCALE);
        button.setScaleY(BUTTON_INITIAL_SCALE);
        button.hoverProperty().addListener(new ChangeListener<Boolean>() {
            FadeTransition ft =
                    new FadeTransition(Duration.millis(BUTTON_ANIMATION_DURATION), button);
            ScaleTransition st =
                    new ScaleTransition(Duration.millis(BUTTON_ANIMATION_DURATION), button);
            ParallelTransition pt = new ParallelTransition(ft, st);

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                    Boolean newPropertyValue) {
                if (newPropertyValue) {
                    ft.setFromValue(INITIAL_BUTTON_OPACITY);
                    ft.setToValue(1);
                    st.setToX(1);
                    st.setToY(1);
                    pt.play();
                } else {
                    ft.setFromValue(1);
                    ft.setToValue(INITIAL_BUTTON_OPACITY);
                    st.setToX(BUTTON_INITIAL_SCALE);
                    st.setToY(BUTTON_INITIAL_SCALE);
                    pt.play();
                }
            }
        });
        button.setOnMouseClicked(clickHandler);
    }

    public static void decorateStage(Stage stage) {
        stage.getIcons().addAll(new Image(IO.res("img/icon64.png").toString()),
                new Image(IO.res("img/icon32.png").toString()),
                new Image(IO.res("img/icon16.png").toString()));
    }

    public static void decorateStage(Stage stage, String name) {
        decorateStage(stage);
        stage.setTitle(String.format("Pallet: %s", name));
    }

    public static FXMLLoader getFXMLLoader(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(IO.res(String.format("%s/%s.fxml", VIEW_PATH, name)));
        return loader;
    }
}
