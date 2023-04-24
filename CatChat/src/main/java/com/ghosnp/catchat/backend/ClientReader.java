package com.ghosnp.catchat.backend;

import com.ghosnp.catchat.backendAssembly.Message;
import com.ghosnp.catchat.frontend.ClientMainFrame;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Date;

public class ClientReader extends Thread {

    private ClientMainFrame clientMainFrame;
    private Socket socket;
    private BufferedReader reader;

    public ClientReader(Socket socket) {
        try {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Platform.runLater(clientMainFrame::ConnectErrorHandle);
        }
    }

    String message = "";

    @Override
    public void run() {
        try {
            while ((message = reader.readLine()) != null) {
                Message unpackedMessage = (Message) deserialize(message);
                String from = unpackedMessage.fromWho().strip();
                String to = unpackedMessage.sendToWho();
                String str = unpackedMessage.message().strip();
                Date date = unpackedMessage.date();
                if (to == null) {
                    // 特殊消息
                    if (str.equals("[BYE]")) {
                        if (from.equals(clientMainFrame.GetAccount())) {
                            reader.close();
                            break;
                        } else {
                            // 有好友离线，需要更新namebar的account为from的状态
                            clientMainFrame.UpdateLoginOutState(from);
                        }
                    } else if (str.equals("[HELLO]")) {
                        // 有好友登录，需要更新namebar的account为from的状态
                        clientMainFrame.UpdateLoginInState(from);
                    }
                } else {
                    if (to.charAt(0) == 'G') {
                        // 群聊消息
                        viewGroupMessage(str, from, to);
                    } else if (to.charAt(0) == 'F') {
                        // 文件消息
                        saveFile(to.substring(1).strip(), str,from);
                    } else {
                        // 普通消息
                        viewMessage(str, from);
                    }
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            // 与服务器断开连接
            Platform.runLater(clientMainFrame::ConnectErrorHandle);
        }
    }

    public void setClientMainFrame(ClientMainFrame clientMainFrame) {
        this.clientMainFrame = clientMainFrame;
    }

    private void viewMessage(String s, String account) {
        Platform.runLater(() -> clientMainFrame.ShowMessageFromServer(account, s));
    }

    private void viewGroupMessage(String message, String sender, String groupNum) {
        Platform.runLater(() -> clientMainFrame.ShowMessageForGroup(groupNum, sender, message));
    }

    private void saveFile(String userAccount, String fileString, String fromWho) throws IOException, ClassNotFoundException {
        File fileOrigin = (File) deserialize(fileString);
        File fileSave = new File("D:/CatChatUser/" + userAccount + "/file/" + fileOrigin.getName());

        Platform.runLater(() -> clientMainFrame.ShowFileFromServer(fileOrigin.getName(), fromWho));
        fileSave.delete();
        fileSave.createNewFile();
        byte[] bs = new byte[1024];

        FileInputStream originStream = new FileInputStream(fileOrigin);
        FileOutputStream saveStream = new FileOutputStream(fileSave);
        int len;
        while ((len = originStream.read(bs)) != -1) {
            saveStream.write(bs, 0, len);
        }
        originStream.close();
        saveStream.close();
    }


    public static Object deserialize(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

}
