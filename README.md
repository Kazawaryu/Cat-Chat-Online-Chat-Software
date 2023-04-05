# Cat Chat - Online Chat Software

If you like this project, please give me a ⭐, it does mean a lot for me🙏.



## 0.	Software Introduction

Cat Chat is an online chat software a reverses project for QQ Chat and uses some Mahjong Soul materials. The client and server are written in Java, JDBC connects to the Postgre database, JavaFX writes the frontend interface, and CSS is used for interface beautification. Cat Chat supports personal chat and group chat, and the front-end interface is completely written in JavaFX (FX8), providing exquisite front-end interfaces and expanded displays (user online status, message sending and receiving status, dynamic components, flat bubbles), you can hardly notice any traces of JavaFX. Of course, Cat Chat still has some dynamic displays that need to be beautified, such as the dynamic switching function of the friend bar and group chat bar, but for now, I think Cat Chat has reached a considerable level of flatness and simplicity.



## 1.	Interface Introduction

### 1.1	login interface

To use Cat Chat, you must first log in to your account and enter your user account and password on the login page for comparison and verification with the database (of course, this part of the code has security risks, such as directly obtaining the real password of a certain account in the database. You may have better ideas and code). After successful verification, you will enter the chat interface. I have applied some dynamic components based only on JavaFX, such as dynamic buttons with underlines and dynamic buttons with automatic filling, which are controlled and beautified by CSS.

<div align="center">
  <img src="https://github.com/Kazawaryu/Cat-Chat-Online-Chat-Software/blob/main/reportImages/Login.gif" style="zoom: 50%;" />
</div>

### 1.2	Main interface

As you can see, Cat Chat adopts a mainstream flat interface design, with the user area in the upper left, the information area in the lower left, and the chat area on the right. For the user area, you can switch the display of the information area (friends or groups) by pressing different buttons. For the information area, I rewrote some code to achieve dynamic loading of friend login status. The green circle represents online and the red circle represents offline, so that users can intuitively understand their friends’ situation. For the chat area, the code realizes bubble text length adaptation to provide users with a good reading experience. The input in the lower right corner also limits the maximum text input to prevent some malicious injection. Finally, all interfaces and front-end components have been processed or rewritten to provide a good user experience.

<div align="center">
  <img src="https://github.com/Kazawaryu/Cat-Chat-Online-Chat-Software/blob/main/reportImages/chat2friend.png" style="zoom: 60%;" />
</div>

For the event of receiving a friend message or group chat message, Cat Chat will set the shadow of the tab that receives the message to red to remind you that there are unread messages.

<div align="center">
  <img src="https://github.com/Kazawaryu/Cat-Chat-Online-Chat-Software/blob/main/reportImages/chat2group.png ">
</div>

### 1.3	Error prompt interface

For sudden crashes of the client and server and other situations, Cat Chat has handled various errors to achieve internal consistency and complete program operation. Local serialization of chat data can be performed here.

<div align="center">
  <img src="https://github.com/Kazawaryu/Cat-Chat-Online-Chat-Software/blob/main/reportImages/exception.png" />
</div>

## 2.	Code Details

### 2.1	Project Structure
```
Cat_Chat
├─.mvn
│  └─wrapper
├─lib
├─src
│  └─main
│      ├─java
│      │  └─com
│      │      └─ghosnp
│      │          └─catchat
│      │              ├─backend
│      │              ├─backendAssembly
│      │              ├─frontend
│      │              ├─frontendAssembly
│      │              └─testAssembly
    │  │      └─catchat
    │  │          ├─backend
    │  │          ├─backendAssembly
    │  │          ├─frontend
    │  │          ├─frontendAssembly
    │  │          └─testAssembly
    │  ├─css
    │  └─images
    └─generated-sources
        └─annotations
```


### 2.2	Listener handle

In actual program operation, there is only one Scroll Pane and multiple Flow Panes that need to be updated. Whenever the user receives a new message, the corresponding Flow Pane needs to be updated with the information. During this process, it may also be necessary to place the Scroll Pane at the bottom, depending on whether the displayed Flow Pane is the Flow Pane that received the information. Abstractly speaking, this is a process of repeatedly activating and canceling the length monitoring status of multiple Flow Panes. Therefore, I abstracted a `ListenerHandles` class here to flexibly implement the activation and cancellation of monitoring.

```java
public interface ListenerHandles {
    void attach();
    void detach();
}
```



### 2.3	Serialize message

For each message received and sent between the client and server, Cat Chat encapsulates the message as a `Message` record class and uses base64 serialization and deserialization to communicate via the TCP protocol (here, RSA and other end-to-end encryption algorithms can be used to improve the security of information transmission). After unpacking the message, the server and client will perform different operations according to the corresponding scenarios to meet project requirements.

```java
public record Message(String fromWho, String sendToWho, String message, Date date) implements Serializable {
    public String toString() {
        return "[" + fromWho + "] send [" + message + "] to [" + sendToWho + "] at [" + date.toString() + "]";
    } 
}
```



### 2.4	Multi-thread

It is obvious that the client and server need to establish reliable communication. Cat Chat uses a classic handshake and wave method similar to TCP, and uses sockets to establish and disconnect connections. Due to the language characteristics of Java, when a client wants to communicate with the server, the client needs to start two sub-threads `ClientReader` and `ClientWriter` for non-continuous blocking communication, and the client needs to start a `ReceiveThread` to listen for messages from the client and respond immediately. It is not difficult to find that when a large number of clients communicate with the server at the same time, the server will be under greater pressure. Therefore, socket connection pools or thread pools can be used to allocate space in advance. Due to development time factors, I only use Hash Map for storage here, which may be optimized later.



## 3.	Work and Schedule

Due to non-disclosure agreement, I will not provide online experience and open registration at present, and will not open source this project before April 28th. As of now (April 7), Cat Chat has realized all the basic functions of a lightweight online chat software, and may further optimize and improve the following parts in the future.

- [x] Dynamic Components and Flat UI
- [x] Stable connection and safe exit in abnormal state
- [ ] Emoticons and file transfer
- [ ] Dynamic user information display switching
- [ ] Server thread pool and Socket pool
- [ ] Communication Encryption Algorithm Like RSA Algorithm
- [ ] Open customized user information modification
