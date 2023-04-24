package com.ghosnp.catchat.frontendAssembly;

import com.ghosnp.catchat.backendAssembly.Message;
import com.google.gson.Gson;
import com.leewyatt.rxcontrols.controls.RXAvatar;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class MessagePaneFrontend {
    private final ScrollPane ScrollMessagePane;
    private MessagePaneUnit CurrentMessagePaneUnit;
    private FlowPane CurrentPane;
    private WebView helpViewer;

    private final String ControlAccount;
    Map<String, MessagePaneUnit> account2pane = new HashMap<>();

    public MessagePaneFrontend(ScrollPane scrollPane, String controlAccount) {
        this.ScrollMessagePane = scrollPane;
        this.ControlAccount = controlAccount;
    }

    public void ChangeCurrentMessagePane(String account) {
        if (!account2pane.containsKey(account)) {
            MessagePaneUnit messagePaneUnit = new MessagePaneUnit(ScrollMessagePane, account, ControlAccount);
            account2pane.put(account, messagePaneUnit);
        }
        if (CurrentMessagePaneUnit != null) {
            CurrentMessagePaneUnit.DetachHandle();
        }
        CurrentMessagePaneUnit = account2pane.get(account);
        CurrentMessagePaneUnit.AttachHandle();
        CurrentPane = CurrentMessagePaneUnit.GetFlowPane();
        this.ScrollMessagePane.setContent(CurrentPane);
    }

    public boolean IsAvailable2SendMessage() {
        return CurrentMessagePaneUnit != null;
    }

    public String GetCurrentPaneSendToAccount() {
        return CurrentMessagePaneUnit.GetAccount();
    }

    public void showMessageFromServer(String account, String s, String avatar, boolean isHistory) {
        RXAvatar rxAvatar = new RXAvatar();
        Image defaultImage = new Image(Objects.requireNonNull(getClass().getResource("/AvatarIcon/" + avatar + ".png")).toExternalForm());
        rxAvatar.setImage(defaultImage);
        rxAvatar.setShapeType(RXAvatar.Type.CIRCLE);
        rxAvatar.setPrefWidth(50);

        HBox hBox = new HBox();
        hBox.setPrefWidth(520);

        hBox.getChildren().add(rxAvatar);
        HBox.setMargin(rxAvatar, new Insets(10, 0, 10, 10));

        Pane textBackground = new Pane();

        textBackground.setStyle("-fx-border-radius: 15; " +
                "-fx-background-radius: 15; -fx-background-color: #ffffff;");
        TextFlow textFlow = new TextFlow();
        textBackground.getChildren().add(textFlow);
        textFlow.setPadding(new Insets(15, 0, 15, 15));
        Text message = new Text(s);
        message.setFont(new Font(15));
        message.setFill(Color.valueOf("#000000"));

        double textLength = Math.min(300, message.getBoundsInLocal().getWidth() + 15);
        double singleTextHeight = message.getBoundsInLocal().getHeight();
        double textHeight = singleTextHeight * Math.ceil(1 + ((int) (message.getBoundsInLocal().getWidth()) + 14) / textLength);

        if (s.chars().filter(ch -> ch == '\n').count() > 0) {
            textHeight /= 2;
            textHeight += 15;
        }
        CreateInnerPane(hBox, textBackground, textFlow, message, textLength, textHeight);
        HBox.setMargin(textBackground, new Insets(10, 0, 10, 10));
        hBox.setStyle("-fx-alignment: TOP_LEFT");

        if (!account2pane.containsKey(account)) {
            MessagePaneUnit messagePaneUnit = new MessagePaneUnit(ScrollMessagePane, account, ControlAccount);
            account2pane.put(account, messagePaneUnit);
        }
        if (isHistory) {
            account2pane.get(account).GetFlowPane().getChildren().add(0, hBox);
        } else {
            account2pane.get(account).GetFlowPane().getChildren().add(hBox);
            WriteMessageToJSONFromServer(ControlAccount, account, s);
        }


    }

    private void CreateInnerPane(HBox hBox, Pane textBackground, TextFlow textFlow, Text message, double textLength, double textHeight) {
        message.prefWidth(textLength);
        textFlow.setPrefWidth(textLength + 5);
        textFlow.setPrefHeight(textHeight + 5);
        textBackground.setPrefHeight(textHeight + 15);
        textBackground.setPrefWidth(textLength + 15);

        textFlow.getChildren().add(message);
        hBox.getChildren().add(textBackground);
    }

    public void showMessageFromClient(String account, String s, String avatar, boolean isHistory) {
        RXAvatar rxAvatar = new RXAvatar();
        Image defaultImage = new Image(Objects.requireNonNull(getClass().getResource(avatar)).toExternalForm());
        rxAvatar.setImage(defaultImage);
        rxAvatar.setShapeType(RXAvatar.Type.CIRCLE);
        rxAvatar.setPrefWidth(50);

        HBox hBox = new HBox();
        hBox.setPrefWidth(520);

        HBox.setMargin(rxAvatar, new Insets(10, 18, 10, 10));

        Pane textBackground = new Pane();

        textBackground.setStyle("-fx-border-radius: 15; " +
                "-fx-background-radius: 15; -fx-background-color: #1e90ff;");
        TextFlow textFlow = new TextFlow();
        textBackground.getChildren().add(textFlow);
        textFlow.setPadding(new Insets(15, 0, 15, 15));

        Text message = new Text(s);
        message.setFont(new Font(15));
        message.setFill(Color.valueOf("#ffffff"));


        double textLength = Math.min(300, message.getBoundsInLocal().getWidth() + 15);
        double singleTextHeight = message.getBoundsInLocal().getHeight();
        double textHeight = singleTextHeight
                * Math.ceil(1 + ((int) (message.getBoundsInLocal().getWidth()) + 14) / textLength);

        int textRow = (int) s.chars().filter(ch -> ch == '\n').count();
        if (textRow > 0) {
            textHeight = Math.ceil(textHeight / 2);
            textHeight += 15;
        }

        CreateInnerPane(hBox, textBackground, textFlow, message, textLength, textHeight);
        hBox.getChildren().add(rxAvatar);
        HBox.setMargin(textBackground, new Insets(10, 0, 10, 10));
        hBox.setStyle("-fx-alignment: TOP_RIGHT");


        if (isHistory) {
            CurrentPane.getChildren().add(0, hBox);
        } else {
            CurrentPane.getChildren().add(hBox);
            WriteMessageToJSONFromClient(ControlAccount, account, s);
        }

        CurrentPane = CurrentMessagePaneUnit.GetFlowPane();

    }

    public void ShowFileFromServer(String fileName, String avatar) {
        RXAvatar rxAvatar = new RXAvatar();
        Image senderImage = new Image(
                getClass().getResource("/AvatarIcon/" + avatar + ".png").toExternalForm());
        rxAvatar.setImage(senderImage);
        rxAvatar.setShapeType(RXAvatar.Type.CIRCLE);
        rxAvatar.setPrefWidth(50);

        HBox hBox = new HBox();
        hBox.setPrefWidth(520);
        hBox.setPrefHeight(100);
        hBox.getChildren().add(rxAvatar);
        HBox.setMargin(rxAvatar, new Insets(10, 0, 10, 10));

        Pane textBackground = new Pane();
        textBackground.setStyle("-fx-border-radius: 15; " +
                "-fx-background-radius: 15; -fx-background-color: #ffffff;"); // #1e90ff;


        Text textIcon = new Text("\uD83D\uDCC4");
        textIcon.setFont(new Font(52));
        textIcon.setFill(Color.valueOf("#000000"));
        textIcon.setLayoutX(15);
        textIcon.setLayoutY(60);

        Text textFile = new Text("FILE");
        textFile.setFont(new Font(20));
        textFile.setFill(Color.valueOf("#000000"));
        textFile.setLayoutX(80);
        textFile.setLayoutY(35);

        TextFlow textFlowName = new TextFlow();
        textFlowName.setPrefWidth(110);
        textFlowName.setPrefHeight(30);
        textFlowName.setLayoutX(80);
        textFlowName.setLayoutY(40);

        if (fileName.length() >= 25) {
            String[] temp = fileName.split("\\.");
            fileName = fileName.substring(0, 8) + "..." + temp[temp.length - 1];
        }
        Text textName = new Text(fileName);
        textName.setFont(new Font(13));
        textName.setFill(Color.valueOf("#000000"));

        SetFileMessageIntoPane(hBox, textBackground, textIcon, textFile, textFlowName, textName);
        HBox.setMargin(textBackground, new Insets(10, 0, 10, 10));

        CurrentPane.getChildren().add(hBox);

    }

    public void ShowFileFromClient(String fileName, String avatar) {
        RXAvatar rxAvatar = new RXAvatar();
        Image senderImage = new Image(getClass().getResource(avatar).toExternalForm());
        rxAvatar.setImage(senderImage);
        rxAvatar.setShapeType(RXAvatar.Type.CIRCLE);
        rxAvatar.setPrefWidth(50);

        HBox hBox = new HBox();
        hBox.setPrefWidth(520);
        hBox.setPrefHeight(100);
        HBox.setMargin(rxAvatar, new Insets(10, 18, 10, 10));

        Pane textBackground = new Pane();
        textBackground.setStyle("-fx-border-radius: 15; " +
                "-fx-background-radius: 15; -fx-background-color: #1e90ff;");


        Text textIcon = new Text("\uD83D\uDCC4");
        textIcon.setFont(new Font(52));
        textIcon.setFill(Color.valueOf("#ffffff"));
        textIcon.setLayoutX(15);
        textIcon.setLayoutY(60);

        TextFlow textFlowName = new TextFlow();
        textFlowName.setPrefWidth(110);
        textFlowName.setPrefHeight(30);
        textFlowName.setLayoutX(80);
        textFlowName.setLayoutY(40);

        Text textFile = new Text("FILE");
        textFile.setFont(new Font(20));
        textFile.setFill(Color.valueOf("#ffffff"));
        textFile.setLayoutX(80);
        textFile.setLayoutY(35);

        if (fileName.length() >= 25) {
            String[] temp = fileName.split("\\.");
            fileName = fileName.substring(0, 8) + "..." + temp[temp.length - 1];
        }
        Text textName = new Text(fileName);
        textName.setFont(new Font(13));
        textName.setFill(Color.valueOf("#ffffff"));

        SetFileMessageIntoPane(hBox, textBackground, textIcon, textFile, textFlowName, textName);
        hBox.getChildren().add(rxAvatar);

        hBox.setStyle("-fx-alignment: TOP_RIGHT");

        HBox.setMargin(textBackground, new Insets(10, 0, 10, 10));

        CurrentPane.getChildren().add(hBox);

    }

    private void SetFileMessageIntoPane(HBox hBox, Pane textBackground,
                                        Text textIcon, Text textFile, TextFlow textFlowName, Text textName) {
        textFlowName.getChildren().add(textName);

        textBackground.setPrefHeight(100);
        textBackground.setPrefWidth(200);
        textBackground.getChildren().add(textIcon);
        textBackground.getChildren().add(textFile);
        textBackground.getChildren().add(textFlowName);

        hBox.getChildren().add(textBackground);
    }


    private void WriteMessageToJSONFromClient(String from, String to, String data) {
        try {
            Message message = new Message(from, to, data, new Date());
            FileWriter fw = new FileWriter(CurrentMessagePaneUnit.GetHistoryFile(), true);
            String jsonStr = new Gson().toJson(message) + "|";
            fw.write(jsonStr);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteMessageToJSONFromServer(String from, String to, String data) {
        try {
            String fPath = "D:/CatChatUser/" + ControlAccount + "/history/" + to + ".json";
            Message message = new Message(to, from, data, new Date());
            FileWriter fw = new FileWriter(fPath, true);
            String jsonStr = new Gson().toJson(message) + "|";
            fw.write(jsonStr);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ShowHistory() {
        if (CurrentMessagePaneUnit != null && !CurrentMessagePaneUnit.GetIfShown()) {
            try {
                String fPath = "D:/CatChatUser/" + ControlAccount
                        + "/history/" + CurrentMessagePaneUnit.GetAccount() + ".json";
                BufferedReader br = new BufferedReader(new FileReader(fPath));
                String mesHistList;
                if ((mesHistList = br.readLine()) != null) {
                    Gson gson = new Gson();
                    List<Message> ml = new ArrayList<>();
                    Stream.of(mesHistList.split("\\|")).forEach(s -> ml.add(gson.fromJson(s, Message.class)));
                    Collections.reverse(ml);
                    ml.forEach(ms -> {
                        if (ms.fromWho().equals(ControlAccount)) {
                            showMessageFromClient(ms.sendToWho(),
                                    ms.message(), "/AvatarIcon/0000.png", true);
                        } else {
                            showMessageFromServer(ms.fromWho(),
                                    ms.message(), "0000", true);
                        }
                    });
                    CurrentMessagePaneUnit.SetIfShown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void OpenSettingInterface() {
        if (helpViewer == null) {
            // 这里处理会快一些
            helpViewer = new WebView();
            helpViewer.setPrefHeight(338);
        }
        try {
            // 这里之后要更换为个人信息修改页面
            helpViewer.getEngine().load("https://github.com/Kazawaryu/Cat-Chat-Online-Chat-Software/");
            ScrollMessagePane.setContent(helpViewer);
        } catch (Exception ignored) {
        }
    }


}
