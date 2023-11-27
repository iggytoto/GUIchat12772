module com.example.gui_chat12772 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.gui_chat12772 to javafx.fxml;
    exports com.example.gui_chat12772;
}