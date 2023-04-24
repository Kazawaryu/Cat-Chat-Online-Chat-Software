package com.ghosnp.catchat.backend;

import com.ghosnp.catchat.backendAssembly.Message;
import com.ghosnp.catchat.frontend.ClientMainFrame;
import javafx.application.Platform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.Base64;
import java.util.Date;

public class ClientWriter extends Thread {

    private Socket socket;
    private PrintWriter writer;
    private String message = null;

    private final ClientMainFrame clientMainFrame;

    private final String account;

    public ClientWriter(Socket socket, String account, ClientMainFrame clientMainFrame) {
        this.clientMainFrame = clientMainFrame;
        this.account = account;
        try {
            this.socket = socket;
            writer = new PrintWriter(socket.getOutputStream(), true);
            Message firstMessage = new Message(account, null, "[HELLO]", new Date());
            writer.println(serialize(firstMessage));
        } catch (IOException e) {
            Platform.runLater(clientMainFrame::ConnectErrorHandle);
        }
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                if (message != null) {
                    writer.println(message);
                    message = null;
                }
                sleep(10);
            }
        } catch (InterruptedException e) {
            Platform.runLater(clientMainFrame::ConnectErrorHandle);
        }
    }

    public void sendMessage(String s, String account, String sendTo) {
        try {
            Message temp = new Message(account, sendTo, s, new Date());
            message = serialize(temp);
        } catch (IOException e) {
            Platform.runLater(clientMainFrame::ConnectErrorHandle);
        }
    }

    public void TransferFile(String TransferFilePath, String to) {
        try {
            if (!TransferFilePath.equals("")) {
                File file = new File(TransferFilePath);
                message = serialize(new Message(account, "F" + to, serialize(file), new Date()));
            }
        } catch (IOException e) {
            Platform.runLater(clientMainFrame::ConnectErrorHandle);
        }
    }

    public void stopClient() {
        try {
            Message bye = new Message(account, null, "[BYE]", new Date());
            writer.println(serialize(bye));
        } catch (IOException e) {
            Platform.runLater(clientMainFrame::ConnectErrorHandle);
        }
    }

    public static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

}
