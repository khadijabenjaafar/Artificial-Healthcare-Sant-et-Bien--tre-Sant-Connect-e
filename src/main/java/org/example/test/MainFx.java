package org.example.test;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controllers.IndexFront;

public class MainFx extends Application {
   /* @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root= FXMLLoader.load(getClass().getResource("/indexFront.fxml"));

        Scene scene=new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.show();

        primaryStage.setTitle("first Scene");
    }

    public static void main(String[] args) {
        launch(args);
    }*/
   @Override
   public void start(Stage primaryStage) throws Exception {
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/indexFront.fxml"));
       Parent root = loader.load();

       Scene scene = new Scene(root);

       // 💡 Détection de la touche Échap
       scene.setOnKeyPressed(event -> {
           switch (event.getCode()) {
               case ESCAPE -> primaryStage.close();
           }
       });

       primaryStage.initStyle(StageStyle.UNDECORATED);
       primaryStage.setScene(scene);
       primaryStage.show();

       IndexFront controller = loader.getController();
       controller.setStage(primaryStage);
   }

}
