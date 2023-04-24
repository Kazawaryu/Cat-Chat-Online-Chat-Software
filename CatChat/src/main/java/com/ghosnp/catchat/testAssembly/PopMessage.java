package com.ghosnp.catchat.testAssembly;

import com.leewyatt.rxcontrols.controls.RXAvatar;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class PopMessage {

    @FXML
    private FlowPane FlowInfoPane;

    @FXML
    private RXAvatar PopAvatar1;

    @FXML
    private RXAvatar PopAvatar2;

    @FXML
    private HBox PopHBox1;

    @FXML
    private HBox PopHBox2;

    @FXML
    private Text PopText1;

    @FXML
    private Text PopText2;

    @FXML
    private Text PopText21;

    @FXML
    private Pane PopTextField1;

    @FXML
    private Pane PopTextField2;

    @FXML
    private TextFlow PopTextFlow1;

    @FXML
    private TextFlow PopTextFlow2;

    public PopMessage() {
        System.out.println("Now in pop message");
    }

}
