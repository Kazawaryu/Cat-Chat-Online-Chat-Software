package com.ghosnp.meochat.server;

import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;

public class GetUserOnline extends Thread {
    private ConcurrentMap<String, Socket> onlineUser;

    public void setOnlineUser(ConcurrentMap<String, Socket> map) {
        onlineUser = map;
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                sleep(100);
                String op = in.next();
                if (op.equals("user")) {
                    onlineUser.forEach((s, socket) -> System.out.println(s));
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
