package com.ghosnp.catchat.frontendAssembly;

import com.ghosnp.catchat.backendAssembly.ListenerHandle;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

import java.io.File;
import java.io.IOException;

public final class MessagePaneUnit {
    private final FlowPane flowPane;
    private final ListenerHandle<Number> listenerHandle;
    private final String account;
    private final File history;
    private boolean ifShown = false;

    public MessagePaneUnit(ScrollPane ScrollMessagePane, String account, String controlAccount) {
        this.account = account;
        flowPane = new FlowPane();
        flowPane.setStyle("-fx-pref-width: 520; "
                + "-fx-pref-height: 338; -fx-background-color: #EBEBEB #EBEBEB");
        listenerHandle = new ListenerHandle<>(flowPane.heightProperty(),
                (observable, oldValue, newValue) -> ScrollMessagePane.setVvalue(flowPane.getHeight() - 5));

        this.history = new File("D:/CatChatUser/"
                + controlAccount + "/history/" + account + ".json");
        try {
            if (!history.exists()) {
                history.createNewFile();
            }
        } catch (IOException ignore) {
        }
    }

    public FlowPane GetFlowPane() {
        return flowPane;
    }

    public String GetAccount() {
        return account;
    }

    public void AttachHandle() {
        listenerHandle.attach();
    }

    public void DetachHandle() {
        listenerHandle.detach();
    }

    public File GetHistoryFile() {
        return history;
    }

    public void SetIfShown() {
        ifShown = true;
    }

    public boolean GetIfShown() {
        return ifShown;
    }
}
