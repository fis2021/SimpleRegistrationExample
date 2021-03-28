module hellofx {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.loose.fis.sle to javafx.fxml;
    exports org.loose.fis.sle;
}
