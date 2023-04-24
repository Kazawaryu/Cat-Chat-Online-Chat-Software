package com.ghosnp.catchat.frontend;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ExceptionHandler {
    private Stage window;
    @FXML
    private AnchorPane pane;
    @FXML
    private Text warnningCH;
    @FXML
    private Text warnningEN;

    public void setWindow(Stage window) {
        this.window = window;
        pane.setStyle("-fx-background-color: rgba(255,255,255,0) ");
    }

    @FXML
    void ClickAgreeButton(MouseEvent event) {
        window.close();
        System.exit(0);
    }

}
