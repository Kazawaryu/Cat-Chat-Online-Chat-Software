package com.ghosnp.catchat.frontendAssembly;

import com.leewyatt.rxcontrols.controls.RXAvatar;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Objects;

public final class NameBarUnit {
    private final String uName;
    private final String uAccount;
    private final String uAvatar;
    private final String uSign;

    private final boolean uUser;
    private boolean uOnline;

    private final VBox uBar;

    private final Circle ustate;

    private final RXAvatar uAvatarIcon;

    public NameBarUnit(String name, String account, String avatar, String sign, boolean user) {
        this.uName = name;
        this.uAccount = account;
        this.uAvatar = avatar;
        this.uSign = sign;
        this.uUser = user;

        if (user) {
            avatar = "/AvatarIcon/" + avatar + ".png";
        }
        uBar = new VBox();
        HBox hBox = new HBox();
        Pane paneAvatar = new Pane();
        Pane paneInfo = new Pane();
        Line spiltLine = new Line();

        uBar.setStyle("-fx-pref-height: 62; -fx-pref-width: 280;");
        hBox.setStyle("-fx-pref-height: 60; -fx-pref-width: 270;");
        paneAvatar.setStyle("-fx-pref-height: 60; -fx-pref-width: 60;");
        paneInfo.setStyle("-fx-pref-height: 60; -fx-pref-width: 200");
        spiltLine.setEndX(250);
        spiltLine.setStyle("-fx-stroke-width: 0.6; -fx-stroke-miter-limit: 10; -fx-stroke: #798a8a66;");
        VBox.setMargin(spiltLine, new Insets(0, 0, 0, 10));

        hBox.getChildren().add(paneAvatar);
        hBox.getChildren().add(paneInfo);
        uBar.getChildren().add(hBox);
        uBar.getChildren().add(spiltLine);


        uAvatarIcon = new RXAvatar();
        ustate = new Circle();
        Text userName = new Text(name);
        Text userSign = new Text();
        if (sign.length() <= 15) {
            userSign.setText(sign);
        } else {
            userSign.setText(sign.substring(0, 14) + "...");
        }

        ustate.setRadius(6);
        ustate.setFill(Color.valueOf("#EE3B3B"));

        ustate.setLayoutX(54);
        ustate.setLayoutY(49);
        uAvatarIcon.setStyle("-fx-pref-width: 50; "
                + "-fx-background-color: transparent; -fx-padding: 5,5,5,5;");
        uAvatarIcon.setEffect(new DropShadow(2, Color.valueOf("#000000")));
        uAvatarIcon.setLayoutX(4);
        uAvatarIcon.setLayoutY(-1);
        uAvatarIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource(avatar)).toExternalForm()));

        userName.setFont(new Font(16));
        userSign.setFont(new Font(12));
        userName.setLayoutX(15);
        userName.setLayoutY(25);
        userSign.setLayoutX(15);
        userSign.setLayoutY(45);

        paneAvatar.getChildren().addAll(uAvatarIcon);
        if (user) {
            paneAvatar.getChildren().add(ustate);
        }
        paneInfo.getChildren().add(userName);
        paneInfo.getChildren().add(userSign);

    }

    public void Online() {
        uOnline = true;
        ustate.setFill(Color.valueOf("#1fff69"));
    }

    public void Offline() {
        uOnline = false;
        ustate.setFill(Color.valueOf("#EE3B3B"));
    }

    public void UpdateListUnseenMessage() {
        uAvatarIcon.setEffect(new DropShadow(4, Color.valueOf("#F70909")));
    }

    public void UpdateListSeenMessage() {
        uAvatarIcon.setEffect(new DropShadow(2, Color.valueOf("#000000")));
    }


    public String getuName() {
        return uName;
    }

    public String getuAccount() {
        return uAccount;
    }

    public boolean isuOnline() {
        return uOnline;
    }

    public VBox getuBar() {
        return uBar;
    }

    public Circle getUstate() {
        return ustate;
    }

    public RXAvatar getuAvatarIcon() {
        return uAvatarIcon;
    }


}
