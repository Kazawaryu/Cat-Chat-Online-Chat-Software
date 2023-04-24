package com.ghosnp.catchat.testAssembly;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class dragPane extends Application {


    private Stage stage;
    // 点击鼠标时的x坐标值
    private double dragOffsetX;
    // 点击鼠标时的y坐标值
    private double dragOffsetY;
    public static void main(String[] args) {
        Application.launch(dragPane.class, args);
    }
    @Override
    public void start(Stage primaryStage) {
        // Store the stage reference in the instance variable to
        // use it in the mouse pressed event handler later.
        this.stage = primaryStage;
        Label msgLabel = new Label("Press the mouse button and drag.");
        Button closeButton = new Button("Close");
        // closeButton.setOnAction(e -> stage.close());
        closeButton.setOnAction(e -> primaryStage.close());
        VBox root = new VBox();
        root.getChildren().addAll(msgLabel, closeButton);
        Scene scene = new Scene(root, 300, 200);
        // Set lambda of mouse pressed and dragged even handlers for the scene
        scene.setOnMousePressed(this::handleMousePressed);
        // scene.setOnMousePressed(this::handleMousePressed(e));
        scene.setOnMouseDragged(this::handleMouseDragged);
        stage.setScene(scene);
        stage.setTitle("Moving a Stage");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    protected void handleMousePressed(MouseEvent e) {
        // Store the mouse x and y coordinates with respect to the
        // stage in the reference variables to use them in the drag event
        // 点击鼠标时，获取鼠标在窗体上点击时相对应窗体左上角的偏移
        this.dragOffsetX = e.getScreenX() - stage.getX();
        this.dragOffsetY = e.getScreenY() - stage.getY();
    }

    protected void handleMouseDragged(MouseEvent e) {
        // Move the stage by the drag amount
        // 拖动鼠标后，获取鼠标相对应显示器坐标减去鼠标相对窗体的坐标，并将其设置为窗体在显示器上的坐标
        stage.setX(e.getScreenX() - this.dragOffsetX);
        stage.setY(e.getScreenY() - this.dragOffsetY);
    }

}
