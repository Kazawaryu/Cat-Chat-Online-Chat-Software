package com.ghosnp.catchat.backendAssembly;

import com.ghosnp.catchat.backend.ClientWriter;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class ClientCloseThread extends Thread {

    public void start(Stage stage, Connection con, String account, ClientWriter cw) {
        stage.setOnCloseRequest(event -> {
            StopSocketAndSQL(con, account, cw);
        });
    }


    public void CloseApplication(Connection con, String account, ClientWriter cw) {
        StopSocketAndSQL(con, account, cw);
    }

    private void StopSocketAndSQL(Connection con, String account, ClientWriter cw) {
        try {
            cw.stopClient();
            Statement stm = con.createStatement();
            stm.executeUpdate("update UserTable set online = false where account = '" + account + "';");
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
}
