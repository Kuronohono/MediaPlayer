module org.example.mediaplayer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires jaudiotagger;
    requires javafx.web;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires java.desktop;
    requires isoparser;
    requires org.bytedeco.javacv;
    requires jdk.xml.dom;
    requires javafx.graphics;
    requires javafx.base;

    opens org.example.mediaplayer to javafx.fxml;
    exports org.example.mediaplayer;
}