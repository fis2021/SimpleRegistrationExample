module simpleRegistrationExample {
    requires javafx.controls;
    requires javafx.fxml;
    requires nitrite;
    requires com.fasterxml.jackson.annotation;
    requires jackson.databind;

    opens org.loose.fis.sle to javafx.fxml;
    opens org.loose.fis.sle.controllers to javafx.fxml;
    opens org.loose.fis.sle.model to jackson.databind;
    exports org.loose.fis.sle;
}
