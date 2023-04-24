package com.ghosnp.catchat.frontend;

import com.ghosnp.catchat.ClientApplication;
import com.ghosnp.catchat.backend.ClientReader;
import com.ghosnp.catchat.backend.ClientWriter;
import com.ghosnp.catchat.backend.UserStatus;
import com.ghosnp.catchat.backendAssembly.ClientCloseThread;
import com.ghosnp.catchat.backendAssembly.ListenerHandle;
import com.ghosnp.catchat.frontendAssembly.MessagePaneFrontend;
import com.ghosnp.catchat.frontendAssembly.NameBarFrontend;
import com.leewyatt.rxcontrols.controls.RXAvatar;
import com.leewyatt.rxcontrols.controls.RXFillButton;
import com.leewyatt.rxcontrols.controls.RXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class ClientMainFrame {

    @FXML
    private Pane pane;
    @FXML
    private ScrollPane ScrollMessagePane;
    @FXML
    private FlowPane FlowInfoPane;
    @FXML
    private RXAvatar AvatorUser;
    @FXML
    private TextArea InputFile;

    @FXML
    private Circle StageUser;
    @FXML
    private Text TextSign;
    @FXML
    private RXTextField SearchText;
    @FXML
    private Text TextUserName;
    @FXML
    private FlowPane FlowPaneForUsers;
    @FXML
    private Pane MainPane;
    @FXML
    private ScrollPane ScrollPaneForUsers;
    @FXML
    private RXFillButton ButtonSendMessage;

    @FXML
    private Text ChattingSign;

    @FXML
    private Text ChattingUser;
    @FXML
    private Text ChattingAccount;

    private String account;
    private Connection con;
    private final ClientCloseThread closeThread;

    private Stage mainStage;
    private ClientWriter clientWriter;

    private String avatar;
    private NameBarFrontend nameBarFrontend;
    private MessagePaneFrontend messagePaneFrontend;

    private double dragX;
    private double dragY;

    private UserStatus userStatus = UserStatus.Idle;

    @FXML
    void SendMessage(MouseEvent event) {
        String s = InputFile.getText().strip();
        InputFile.setText("");
        if (!s.equals("") && messagePaneFrontend.IsAvailable2SendMessage()) {
            String sendTo = messagePaneFrontend.GetCurrentPaneSendToAccount();
            clientWriter.sendMessage(s, account, sendTo);
            ShowMessageFromClient(s, avatar, sendTo);
            if (sendTo.charAt(0) == 'G') {
                nameBarFrontend.TopMessage2Group(sendTo);
            } else {
                nameBarFrontend.TopMessage2User(sendTo);
            }
        }
    }

    @FXML
    void ChangeNameBar2Groups(MouseEvent event) {
        nameBarFrontend.ChangeNameBar2Groups();
    }

    @FXML
    void ChangeNameBar2Search(MouseEvent event) {
        nameBarFrontend.ChangeNameBar2Search();
    }

    @FXML
    void ChangeNameBar2Users(MouseEvent event) {
        nameBarFrontend.ChangeNameBar2Users();
    }

    public ClientMainFrame() {
        closeThread = new ClientCloseThread();
    }

    public void StartApp(String account, Connection con, Stage stage) {
        this.account = account;
        this.con = con;
        this.mainStage = stage;
        fixConstructor();
    }

    private void fixConstructor() {
        try {
            Statement stm = con.createStatement();
            stm.executeUpdate("update UserTable set online = true where account = '" + account + "';");
            ResultSet rs = stm.executeQuery("select * from UserInfo where account = '" + account + "';");
            pane.setStyle("-fx-background-color: rgba(255,255,255,0) ");

            if (rs.next()) {
                String icon = rs.getString(5).strip();
                avatar = "/AvatarIcon/" + icon + ".png";
                AvatorUser.setImage(new Image(Objects.requireNonNull(getClass().getResource(avatar)).toExternalForm()));
                TextUserName.setText(rs.getString(3).strip());
                String sign = rs.getString(4).strip();
                sign = sign.substring(0, Math.min(sign.length(), 20));
                if (sign.length() > 7) {
                    sign = sign.substring(0, 7) + "...";
                }
                TextSign.setText(sign);
                StageUser.setFill(Color.valueOf("#1FFF69"));
                stm.close();

                //开启与服务器的通信
                Socket socket = new Socket("127.0.0.1", 22019);
                clientWriter = new ClientWriter(socket, account, this);
                ClientReader clientReader = new ClientReader(socket);
                clientWriter.start();
                clientReader.start();
                clientReader.setClientMainFrame(this);

                // 开启监听窗口是否结束的子线程
                mainStage = (Stage) AvatorUser.getScene().getWindow();
                closeThread.start(mainStage, con, account, clientWriter);

                //加载聊天组件
                InputFile.setTextFormatter(new TextFormatter<String>(change ->
                        change.getControlNewText().length() <= 255 ? change : null));

                //开启动态监听
                ListenerHandle<Number> listenerHandle = new ListenerHandle<>(FlowInfoPane.heightProperty(), new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        ScrollMessagePane.setVvalue(FlowInfoPane.getHeight() - 5);
                    }
                });
                listenerHandle.attach();

                //加载模块化组件
                nameBarFrontend = new NameBarFrontend(ScrollPaneForUsers, this);
                messagePaneFrontend = new MessagePaneFrontend(ScrollMessagePane, account);
                InitialUserFriend();

                //写入本次登录信息
                File lastInfo = new File("D:/CatChatUser/last.txt");
                if (!lastInfo.exists()) {
                    lastInfo.createNewFile();
                }
                BufferedWriter bw = new BufferedWriter(new PrintWriter(lastInfo));
                bw.write(icon + '\n');
                bw.write(TextUserName.getText().strip() + '\n');
                bw.write(account);
                bw.close();


                //查询和维护本地聊天记录及
                File uidFolder = new File("D:/CatChatUser/" + account + "/");
                if (!uidFolder.getParentFile().exists()) {
                    uidFolder.getParentFile().mkdirs();
                }
                File fileFolder = new File("D:/CatChatUser/" + account + "/" + "file/");
                if (!fileFolder.exists()) {
                    fileFolder.mkdirs();
                }
                File hisFolder = new File("D:/CatChatUser/" + account + "/" + "history/");
                if (!hisFolder.exists()) {
                    hisFolder.mkdirs();
                }
            }
        } catch (SQLException | IOException e) {
            exitLogin();
        }
    }

    private void exitLogin() {
        try {
            ConnectErrorHandle();
            File lastInfo = new File("D:/CatChatUser/last.txt");
            lastInfo.createNewFile();
            BufferedWriter bw = new BufferedWriter(new PrintWriter(lastInfo));
            bw.write("3000\n");
            bw.write("Cat Chat\n");
            bw.write("\n");
            bw.close();
        } catch (IOException ignored) {
        }
    }

    public void InitialUserFriend() {
        try {
            Statement statement = con.createStatement();
            ResultSet friends = statement.executeQuery(
                    "select friendaccount from friends where useraccount = '" + account + "';");
            PreparedStatement stmFriendsInfo = con.prepareStatement(
                    "select * from userinfo where account = ?;");
            PreparedStatement stmFriendsState = con.prepareStatement(
                    "select online from usertable where account = ?;");

            ResultSet friendsInfo;
            ResultSet friendsState;
            while (friends.next()) {
                stmFriendsInfo.setString(1, friends.getString(1));
                stmFriendsState.setString(1, friends.getString(1));
                stmFriendsInfo.addBatch();
                stmFriendsState.addBatch();
                friendsInfo = stmFriendsInfo.executeQuery();
                friendsState = stmFriendsState.executeQuery();
                friendsInfo.next();
                friendsState.next();
                nameBarFrontend.CreateUserNameBar(
                        friendsInfo.getString(2).strip(), friendsInfo.getString(5).strip(),
                        friendsInfo.getString(3).strip(), friendsInfo.getString(4).strip(),
                        friendsState.getBoolean(1));
                stmFriendsInfo.clearBatch();
                stmFriendsState.clearBatch();
            }

            stmFriendsInfo.close();
            stmFriendsState.close();

            //select groupnum from groups where joiner = '12011126';
            ResultSet groups = statement.executeQuery(
                    "select groupnum from groups where joiner = '" + account + "';");
            PreparedStatement groupsName = con.prepareStatement(
                    "select groupname from groupstable where groupnum = ?;");
            ResultSet groupName;
            while (groups.next()) {
                groupsName.setString(1, groups.getString(1).strip());
                groupsName.addBatch();
                groupName = groupsName.executeQuery();
                groupName.next();
                nameBarFrontend.CreateGroupNameBar(groups.getString(1), groupName.getString(1));
                groupsName.clearBatch();
            }
            groupsName.close();

            statement.close();
        } catch (SQLException e) {
            ConnectErrorHandle();
        }
    }

    public void ShowMessageFromServer(String account, String message) {
        try {
            Statement getAvatar = con.createStatement();
            ResultSet rs = getAvatar.executeQuery("select avatar from userinfo where account = '" + account + "';");
            rs.next();
            messagePaneFrontend.showMessageFromServer(account, message, rs.getString(1), false);
            nameBarFrontend.UpdateUnseenMessage(account);
        } catch (SQLException e) {
            ConnectErrorHandle();
        }
    }

    public void ShowMessageForGroup(String groupNum, String sender, String message) {
        try {
            Statement getAvatar = con.createStatement();
            ResultSet rs = getAvatar.executeQuery("select avatar from userinfo where account = '" + sender + "';");
            rs.next();
            messagePaneFrontend.showMessageFromServer(groupNum, message, rs.getString(1), false);
            nameBarFrontend.UpdateUnseenGroupMessage(groupNum);
            getAvatar.close();
        } catch (SQLException e) {
            ConnectErrorHandle();
        }
    }

    public void ShowMessageFromClient(String message, String avatar, String to) {
        messagePaneFrontend.showMessageFromClient(to, message, avatar, false);
    }

    public void ShowFileFromServer(String fileName, String account) {
        try {
            Statement getAvatar = con.createStatement();
            ResultSet rs = getAvatar.executeQuery(
                    "select avatar from userinfo where account = '" + account + "';");
            rs.next();
            messagePaneFrontend.ShowFileFromServer(fileName, rs.getString(1));
            nameBarFrontend.UpdateUnseenMessage(account);
            getAvatar.close();
        } catch (SQLException e) {
            ConnectErrorHandle();
        }
    }

    public void ShowFileFromClient(String fileName) {
        messagePaneFrontend.ShowFileFromClient(fileName, avatar);
    }

    public void ChangeMessagePane(String account, String name, String sign) {
        messagePaneFrontend.ChangeCurrentMessagePane(account.strip());
        SetChattingUserView(account.strip(), name.strip(), sign.strip());
    }

    public void ChangeGroupMessagePane(String groupNum, String groupName) {
        messagePaneFrontend.ChangeCurrentMessagePane("G" + groupNum.strip());
        ChattingUser.setText(groupName.strip());
        ChattingSign.setText(groupNum.strip());
    }

    private void SetChattingUserView(String account, String name, String sign) {
        ChattingUser.setText(name + " - " + account);
        ChattingSign.setText(sign);
    }

    public String GetAccount() {
        return account;
    }

    public void UpdateLoginOutState(String account) {
        nameBarFrontend.UpdateLoginOutState(account);
    }

    public void UpdateLoginInState(String account) {
        nameBarFrontend.UpdateLoginInState(account);
    }

    public void ConnectErrorHandle() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ClientApplication.class.getResource("ExceptionHandler.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = mainStage;
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


    @FXML
    void handleMouseDragged(MouseEvent event) {
        mainStage.setX(event.getScreenX() - this.dragX);
        mainStage.setY(event.getScreenY() - this.dragY);
    }

    @FXML
    void handleMousePressed(MouseEvent event) {
        this.dragX = event.getScreenX() - mainStage.getX();
        this.dragY = event.getScreenY() - mainStage.getY();
    }

    @FXML
    void CloseMainStage(MouseEvent event) {
        closeThread.CloseApplication(con, account, clientWriter);
    }


    @FXML
    void HelpMainStage(MouseEvent event) {
        messagePaneFrontend.OpenSettingInterface();
    }

    @FXML
    void MinimizeMainStage(MouseEvent event) {
        mainStage.setIconified(true);
    }


    @FXML
    void ShowHistory(MouseEvent event) {
        messagePaneFrontend.ShowHistory();
    }


    @FXML
    void ShowFileTransfer(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select file to transfer");
        File file = fc.showOpenDialog(mainStage);
        if (file != null) {
            File dir = new File(file.getParent());
            if (dir.isDirectory()) {
                fc.setInitialDirectory(dir);
            }
            userStatus = UserStatus.ReadToTransfer;
            if (messagePaneFrontend.IsAvailable2SendMessage()
                    && messagePaneFrontend.GetCurrentPaneSendToAccount().charAt(0) != 'G') {
                ShowFileFromClient(file.getName());
                clientWriter.TransferFile(file.getAbsolutePath(), messagePaneFrontend.GetCurrentPaneSendToAccount());
            }
        }
    }

}


