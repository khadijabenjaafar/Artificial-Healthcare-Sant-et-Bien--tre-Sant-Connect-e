package org.example.test;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class MainFx extends Application {

    @Override

    public void start(Stage primaryStage) throws Exception {
        Parent root= FXMLLoader.load(getClass().getResource("/freelancer.fxml"));

        Scene scene=new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);

        primaryStage.show();

        primaryStage.setTitle("first Scene");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
