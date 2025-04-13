module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example to javafx.fxml;
   // exports com.example.demo;
    exports com.example.Controllers;
    opens com.example.Controllers to javafx.fxml;
    exports com.example.test;
    opens com.example.test to javafx.fxml;
}