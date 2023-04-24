package com.ghosnp.meochat;

import com.ghosnp.meochat.server.Server;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MeoChatApplication {

    public MeoChatApplication() throws IOException {
        Server server = new Server();
    }

    public static void main(String[] args) {
        SpringApplication.run(MeoChatApplication.class, args);
    }


}
