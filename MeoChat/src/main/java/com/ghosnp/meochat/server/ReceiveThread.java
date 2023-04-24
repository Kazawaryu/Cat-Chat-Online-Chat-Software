package com.ghosnp.meochat.server;

import com.ghosnp.catchat.backendAssembly.Message;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;

public class ReceiveThread extends Thread {
    private final Socket currentSocket;
    private final Server server;
    private String account;

    private Connection con;

    public ReceiveThread(Socket socket, Server server, Connection con) {
        this.currentSocket = socket;
        this.server = server;
        this.con = con;
    }

    @Override
    public void run() {
        try {

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));

            String message;

            while (true) {
                if (!currentSocket.isClosed()) {
                    if ((message = bufferedReader.readLine()) != null) {
                        Message unpackedMessage = (Message) deserialize(message);
                        String from = unpackedMessage.fromWho().strip();
                        String to = unpackedMessage.sendToWho();
                        String str = unpackedMessage.message().strip();
                        Date date = unpackedMessage.date();
                        if (to == null) {
                            // 特殊通信
                            if (str.equals("[HELLO]")) {
                                // 服务端与客户端握手，服务端需要给好友的客户端更新好友栏的用户状态
                                account = from.strip();
                                server.AddConnectSocket(account, currentSocket);
                                UpdateNameBarState(message);
                                System.out.println(date + " [" + from + "] Login in.");
                                OutputUserList();
                            } else if (str.equals("[BYE]")) {
                                // 服务端与客户端挥手，服务端需要发送结束消息给客户端
                                PrintWriter serverMessageSendBack =
                                        new PrintWriter(new OutputStreamWriter(currentSocket.getOutputStream()));
                                serverMessageSendBack.println(message);
                                // 服务端需要给好友的客户端更新好友栏的用户状态
                                UpdateNameBarState(message);
                                // 服务端结束当前socket，清理资源
                                serverMessageSendBack.close();
                                server.RemoveClosedSocket(account);
                                currentSocket.close();
                                System.out.println(date + " [" + from + "] Sign out.");
                                OutputUserList();
                                break;
                            }
                        } else {
                            if (to.charAt(0) == 'G') {
                                // 群聊
                                to = to.substring(1);
                                Statement stm = con.createStatement();
                                ResultSet rs = stm.executeQuery("select joiner from groups where groupnum = '" + to + "';");
                                while (rs.next()) {
                                    if (!rs.getString(1).strip().equals(account)) {
                                        if (server.ContainPrepareSocket(rs.getString(1).strip())) {
                                            Socket socket = server.GetPrepareSocket(rs.getString(1).strip());
                                            PrintWriter tempPW = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                                            tempPW.println(message);
                                            tempPW.flush();
                                        }
                                    }
                                }
                            } else if (to.charAt(0) == 'F') {
                                // 发送文件
                                to = to.substring(1);
                                PrintWriter sendWriter = new PrintWriter(new OutputStreamWriter(server.GetPrepareSocket(to).getOutputStream()));
                                sendWriter.println(message);
                                sendWriter.flush();
                            } else {
                                // 单人聊天
                                if (server.ContainPrepareSocket(to)) {
                                    // 当前用户确实在线，直接发送
                                    PrintWriter sendWriter = new PrintWriter(new OutputStreamWriter(server.GetPrepareSocket(to).getOutputStream()));
                                    sendWriter.println(message);
                                    sendWriter.flush();
                                } else {
                                    // 当前用户确实离线，消息存入数据库，下次登陆抛给客户端处理（已废弃，聊天记录保存到本地）
                                }
                            }
                        }
                    }
                } else break;

            }
        } catch (Exception e) {
            // 用户意外离线，修改数据库
            System.out.println("[Error from Client] " + account + "leave unexpected");
            Message bye = new Message(account, null, "[BYE]", new Date());
            try {
                Statement stm = con.createStatement();
                stm.executeUpdate("update UserTable set online = false where account = '" + account + "';");
                stm.close();
                UpdateNameBarState(serialize(bye));
                server.RemoveClosedSocket(account);
                currentSocket.close();
            } catch (IOException | SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void UpdateNameBarState(String message) throws SQLException, IOException {
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("select friendaccount from friends where useraccount = '" + account + "';");
        while (rs.next()) {
            if (!rs.getString(1).strip().equals(account)
                    && server.ContainPrepareSocket(rs.getString(1).strip())) {
                Socket tempSocket = server.GetPrepareSocket(rs.getString(1).strip());
                PrintWriter tempPW = new PrintWriter(new OutputStreamWriter(tempSocket.getOutputStream()));
                tempPW.println(message);
                tempPW.flush();
            }
        }
        stm.close();
    }

    private void OutputUserList() {
        System.out.println("******************Online******************");
        server.clientMapSockets.forEach((s, socket) -> System.out.println("[" + s + "] " + socket.getInetAddress()));
        System.out.println("******************************************");
    }

    public static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static Object deserialize(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }


}
