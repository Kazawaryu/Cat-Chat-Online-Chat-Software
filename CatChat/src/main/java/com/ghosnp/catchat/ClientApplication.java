package com.ghosnp.catchat;

import com.ghosnp.catchat.frontend.LoginFrame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                ClientApplication.class.getResource("LoginFrame.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CatChat");
        stage.setScene(scene);
        stage.setResizable(false);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        LoginFrame lg = fxmlLoader.getController();
        lg.showInterface();
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}