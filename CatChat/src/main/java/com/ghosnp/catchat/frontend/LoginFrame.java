package com.ghosnp.catchat.frontend;

import com.ghosnp.catchat.ClientApplication;
import com.leewyatt.rxcontrols.controls.RXAvatar;
import com.leewyatt.rxcontrols.controls.RXLineButton;
import com.leewyatt.rxcontrols.controls.RXPasswordField;
import com.leewyatt.rxcontrols.controls.RXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class LoginFrame {

    @FXML
    private Button CloseButton;

    @FXML
    private RXTextField InvitationCode;

    @FXML
    private RXTextField LoginAccount;

    @FXML
    private RXAvatar LoginAvator;

    @FXML
    private RXPasswordField LoginPassword;

    @FXML
    private RXTextField RegAccount;

    @FXML
    private RXTextField RegName;

    @FXML
    private RXPasswordField RegPassword;
    @FXML
    private Pane RegisterPane;

    @FXML
    private RXLineButton UserName;

    @FXML
    private Pane pane;


    private static final String url = "jdbc:postgresql://localhost:5432/postgres"; //所要连接的数据库地址
    private static final String user = "postgres"; //PgSQL登录名
    private static final String pass = "472362"; //数据库密码
    private static Connection con;

    private Stage stage;

    private double dragX;
    private double dragY;

    public LoginFrame() {
        // 与DB建立连接
        try {
            //注册JDBC驱动
            Class.forName("org.postgresql.Driver");
            //建立连接
            con = DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[Client Error]Connect to DB false.");
        }

    }

    public void showInterface() {
        try {
            // 检查本地文件完整性
            File mainFolder = new File("D:/CatChatUser/");
            if (!mainFolder.getParentFile().exists()) {
                mainFolder.getParentFile().mkdirs();
            }
            File lastInfo = new File("D:/CatChatUser/last.txt");
            int avatar = 3000;
            String name = "Cat Chat";
            String uid = "";
            if (!lastInfo.exists()) {
                lastInfo.createNewFile();
                BufferedWriter bw = new BufferedWriter(new PrintWriter(lastInfo));
                bw.write("3000\n");
                bw.write("Cat Chat\n");
                bw.write("");
                bw.close();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(lastInfo));
                if (br.ready()) {
                    avatar = Integer.parseInt(br.readLine());
                }
                if (br.ready()) {
                    name = br.readLine().strip();
                }
                if (br.ready()) {
                    uid = br.readLine().strip();
                }
                br.close();
            }
            pane.setStyle("-fx-background-color: rgba(255,255,255,0) ");
            UserName.setText(name);
            LoginAccount.setText(uid);
            String avatarStr = "/AvatarIcon/" + avatar + ".png";
            LoginAvator.setImage(new Image(Objects.requireNonNull(getClass().getResource(avatarStr)).toExternalForm()));
        } catch (IOException ignored) {
        }
    }

    @FXML
    void ClickLoginButton(MouseEvent event) throws SQLException {
        String account = LoginAccount.getText();
        String password = LoginPassword.getText();
        if (Objects.equals(account, "") || Objects.equals(password, "")) {
            System.out.println("[Client 500]No account or password input!");
        } else {
            boolean getAccount = false;
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select * from UserTable where account = '" + account + "';");
            while (rs.next()) {
                getAccount = true;
                if (rs.getString(3).strip().equals(password)) {
                    if (!rs.getBoolean(4)) {
                        Stage stage = (Stage) CloseButton.getScene().getWindow();
                        stage.close();
                        OpenClientMainFrame();
                    } else {
                        System.out.println("[Client 200]User has already login in.");
                    }
                } else {
                    System.out.println("[Client 500]Wrong account or password");
                }
            }
            stm.close();
            if (!getAccount) {
                System.out.println("[Client 300]No such account, please register first.");
            }
        }
    }

    private void OpenClientMainFrame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("ClientMainFrame.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("CatChat");
            stage.setScene(scene);
            stage.setResizable(false);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            ClientMainFrame clmf = fxmlLoader.getController();
            clmf.StartApp(LoginAccount.getText(), con, stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - this.dragX);
        stage.setY(event.getScreenY() - this.dragY);
    }

    @FXML
    void handleMousePressed(MouseEvent event) {
        if (stage == null) {
            this.stage = (Stage) CloseButton.getScene().getWindow();
        }
        this.dragX = event.getScreenX() - stage.getX();
        this.dragY = event.getScreenY() - stage.getY();
    }

    @FXML
    void CloseMainStage(MouseEvent event) throws SQLException {
        con.close();
        System.exit(0);
    }

    @FXML
    void MinimizeMainStage(MouseEvent event) {
        if (stage == null) {
            this.stage = (Stage) CloseButton.getScene().getWindow();
        }
        stage.setIconified(true);
    }

    @FXML
    void ClickRegisterButton(MouseEvent event) {
        RegisterPane.setVisible(true);
    }

    @FXML
    void CloseRegisterStage(MouseEvent event) {
        RegisterPane.setVisible(false);
    }

    @FXML
    void ClickRGAccountButton(MouseEvent event) throws SQLException {
        int passNum = 0;

        String regAccount = RegAccount.getText().strip();
        if (regAccount.length() <= 10 && !regAccount.equals("")) {
            passNum++;
        } else {
            RegAccount.setText("Invalid Account");
        }

        String regPassword = RegPassword.getText().strip();
        if (regPassword.length() <= 20 && !regPassword.equals("")) {
            passNum++;
        }

        String regName = RegName.getText().strip();
        if (regName.length() <= 12 && !regName.equals("")) {
            passNum++;
        } else {
            RegName.setText("Invalid Name");
        }

        String cdKey = InvitationCode.getText();
        String[] keyPara = cdKey.split("-");
        if (keyPara.length == 3) {
            File avatarIcon = new File(String.valueOf(getClass().getResource("/AvatarIcon/" + keyPara[0] + ".png")).substring(6));
            if (avatarIcon.exists()) {
                if (keyPara[1].equals("123456") && (keyPara[2].equals("HK") || keyPara[2].equals("US"))) {
                    passNum++;
                }
            } else {
                InvitationCode.setText("Invalid CdKey");
            }
        } else {
            InvitationCode.setText("Invalid CdKey");
        }

        if (passNum == 4) {
            try {
                Statement regs = con.createStatement();
                ResultSet rsHas = regs.executeQuery(
                        "select count(*) from usertable where account = '" + regAccount + "';");
                rsHas.next();
                if (rsHas.getInt(1) == 0) {
                    regs.executeUpdate(
                            "insert into usertable(account, password) values ('" + regAccount
                                    + "','" + regPassword + "')");
                    regs.executeUpdate(
                            "insert into userinfo(account, name, sign, avatar) values ('"
                                    + regAccount + "','" + regName + "','','" + keyPara[0] + "');");
                    RegisterPane.setVisible(false);
                    LoginAccount.setText(regAccount);
                    LoginPassword.setText(regPassword);
                }
            } catch (SQLException e) {
                ConnectErrorHandle();
            }
        }
    }

    public void ConnectErrorHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("ExceptionHandler.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("CatChat");
            stage.setScene(scene);
            stage.setResizable(false);
            ExceptionHandler exceptionHandler = fxmlLoader.getController();
            exceptionHandler.setWindow(stage);
            scene.setFill(Color.TRANSPARENT);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
