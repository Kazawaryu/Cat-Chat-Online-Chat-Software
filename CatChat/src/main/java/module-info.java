module com.ghosnp.catchat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires rxcontrols;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires javafx.web;
    requires jackson.core;
    requires jackson.databind;
    requires com.google.gson;
    requires org.json;
    requires javafx.media;

    opens com.ghosnp.catchat to javafx.fxml;
    exports com.ghosnp.catchat;
    exports com.ghosnp.catchat.frontend;
    opens com.ghosnp.catchat.frontend to javafx.fxml;
    exports com.ghosnp.catchat.testAssembly;
    opens com.ghosnp.catchat.testAssembly to javafx.fxml, com.google.gson,jackson.databind;
    exports com.ghosnp.catchat.backendAssembly;
    opens com.ghosnp.catchat.backendAssembly to javafx.fxml;
    exports com.ghosnp.catchat.frontendAssembly;
    opens com.ghosnp.catchat.frontendAssembly to com.google.gson,jackson.databind;


}