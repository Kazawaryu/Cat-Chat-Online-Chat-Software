package com.ghosnp.meochat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    ServerSocket serverSocket;
    private int port = 22019;
    public ConcurrentMap<String, Socket> clientMapSockets = new ConcurrentHashMap<>();
    private static final String url = "jdbc:postgresql://localhost:5432/postgres"; //所要连接的数据库地址
    private static final String user = "postgres"; //PgSQL登录名
    private static final String pass = "472362"; //数据库密码
    private static Connection con;

    @SuppressWarnings("InfiniteLoopStatement")
    public Server() {
        try {
            serverSocket = new ServerSocket(port);

            try {
                //注册JDBC驱动
                Class.forName("org.postgresql.Driver");
                //建立连接
                con = DriverManager.getConnection(url, user, pass);
                // Here used to test
                Statement statement = con.createStatement();
                statement.executeUpdate("update usertable set online = false where id > 0;");
                statement.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            while (true) {
                Socket socket = serverSocket.accept();
                ReceiveThread thread = new ReceiveThread(socket, this, con);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void AddConnectSocket(String account, Socket socket) {
        clientMapSockets.put(account, socket);
    }

    public void RemoveClosedSocket(String account) {
        clientMapSockets.remove(account);
    }

    public Socket GetPrepareSocket(String account) {
        return clientMapSockets.get(account);
    }

    public boolean ContainPrepareSocket(String account) {
        return clientMapSockets.containsKey(account);
    }

}
