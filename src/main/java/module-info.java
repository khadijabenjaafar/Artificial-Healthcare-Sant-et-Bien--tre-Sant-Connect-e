module org.example.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.json;
    requires javafx.media;


    opens org.example.controllers to javafx.fxml;

    opens org.example.test to javafx.fxml;
    exports org.example.test;


    opens org.example.entities to javafx.fxml;

    // Pour lancer l'application JavaFX
}

