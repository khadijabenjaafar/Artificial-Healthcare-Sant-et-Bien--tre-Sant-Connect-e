package org.example.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NavigationUtil {

    public static void switchScene(Stage stage, Parent newRoot) {
        newRoot.setOpacity(0);
        Scene scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newRoot);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
