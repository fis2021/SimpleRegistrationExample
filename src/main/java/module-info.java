module hellofx {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.loose.sle to javafx.fxml;
    exports org.loose.sle;
}
