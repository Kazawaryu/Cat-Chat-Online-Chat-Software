package com.ghosnp.catchat.frontendAssembly;

import com.ghosnp.catchat.frontend.ClientMainFrame;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class NameBarFrontend {
    private final ClientMainFrame clientMainFrame;
    private final ScrollPane ScrollPaneNameBar;

    private final FlowPane FlowPaneForUsers;

    private final Map<String, NameBarUnit> FriendsBarPane = new HashMap<>();

    private final Map<String, NameBarUnit> GroupsBarPane = new HashMap<>();

    private final FlowPane FlowPaneForGroups;


    private final FlowPane FlowPaneForSearch;

    public NameBarFrontend(ScrollPane scrollPane, ClientMainFrame clientMainFrame) {
        this.clientMainFrame = clientMainFrame;
        this.ScrollPaneNameBar = scrollPane;
        this.FlowPaneForUsers = new FlowPane();
        this.FlowPaneForGroups = new FlowPane();
        this.FlowPaneForSearch = new FlowPane();
        FlowPaneForUsers.setPrefHeight(464);
        FlowPaneForGroups.setPrefHeight(464);
        FlowPaneForSearch.setPrefHeight(464);
        FlowPaneForUsers.setPrefWidth(280);
        FlowPaneForGroups.setPrefWidth(280);
        FlowPaneForSearch.setPrefWidth(280);
        ScrollPaneNameBar.setContent(FlowPaneForUsers);
        FlowPaneForUsers.setStyle("-fx-background-color: #EBEBEB");
        FlowPaneForGroups.setStyle("-fx-background-color: #EBEBEB");
    }

    public void ChangeNameBar2Users() {
        ScrollPaneNameBar.setContent(FlowPaneForUsers);
    }

    public void ChangeNameBar2Groups() {
        ScrollPaneNameBar.setContent(FlowPaneForGroups);
    }

    public void ChangeNameBar2Search() {
        ScrollPaneNameBar.setContent(FlowPaneForSearch);
    }

    public void CreateUserNameBar(String account, String avatar, String name, String sign, boolean online) {
        NameBarUnit barUnit = new NameBarUnit(name, account, avatar, sign, true);
        VBox vBox = barUnit.getuBar();
        vBox.setOnMouseClicked(event -> UpdateSeenMessage(account, name, sign));
        FlowPaneForUsers.getChildren().add(vBox);
        FriendsBarPane.put(account, barUnit);
        if (online) {
            barUnit.Online();
            TopMessage2User(account.strip());
        }
    }

    public void CreateGroupNameBar(String groupNum, String groupName) {
        NameBarUnit barUnit = new NameBarUnit(groupName, groupNum,
                "/images/catchat.png", groupNum, false);
        VBox vBox = barUnit.getuBar();
        vBox.setOnMouseClicked(event -> UpdateSeenGroupMessage(groupNum, groupName));
        FlowPaneForGroups.getChildren().add(vBox);
        GroupsBarPane.put(groupNum, barUnit);
    }

    public void CreateGptNameBar() {
        NameBarUnit barUnit = new NameBarUnit("Nyaer", "CatFood", "0000", "Welcome to use Cat Chat~", true);
        VBox vBox = barUnit.getuBar();
        vBox.setOnMouseClicked(event -> UpdateSeenMessage("CatFood", "Nyaer", "Welcome to use Cat Chat~"));
        FlowPaneForUsers.getChildren().add(vBox);
        FriendsBarPane.put("CatFood", barUnit);
        barUnit.Online();
    }


    public void TopMessage2User(String account) {
        NameBarUnit barUnit = FriendsBarPane.get(account);
        FlowPaneForUsers.getChildren().remove(barUnit.getuBar());
        FlowPaneForUsers.getChildren().add(0, barUnit.getuBar());
    }

    public void UpdateUnseenMessage(String account) {
        NameBarUnit barUnit = FriendsBarPane.get(account);
        barUnit.UpdateListUnseenMessage();
        FlowPaneForUsers.getChildren().remove(barUnit.getuBar());
        FlowPaneForUsers.getChildren().add(0, barUnit.getuBar());

    }

    public void UpdateSeenMessage(String account, String name, String sign) {
        NameBarUnit barUnit = FriendsBarPane.get(account);
        barUnit.UpdateListSeenMessage();
        clientMainFrame.ChangeMessagePane(account, name, sign);
    }

    public void TopMessage2Group(String account) {
        NameBarUnit barUnit = GroupsBarPane.get(account.substring(1));
        FlowPaneForGroups.getChildren().remove(barUnit.getuBar());
        FlowPaneForGroups.getChildren().add(0, barUnit.getuBar());
    }

    public void UpdateUnseenGroupMessage(String groupNum) {
        NameBarUnit barUnit = GroupsBarPane.get(groupNum.strip().substring(1));
        barUnit.UpdateListUnseenMessage();
        FlowPaneForGroups.getChildren().remove(barUnit.getuBar());
        FlowPaneForGroups.getChildren().add(0, barUnit.getuBar());
    }

    public void UpdateSeenGroupMessage(String groupNum, String groupName) {
        GroupsBarPane.get(groupNum).UpdateListSeenMessage();
        clientMainFrame.ChangeGroupMessagePane(groupNum, groupName);
    }

    public void UpdateLoginOutState(String account) {
        FriendsBarPane.get(account).Offline();
    }

    public void UpdateLoginInState(String account) {
        FriendsBarPane.get(account).Online();
        Platform.runLater(() -> TopMessage2User(account));
    }
}
