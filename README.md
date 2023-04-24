# Cat Chat - Online Chat Software

If you like this project, please give me a ⭐, it does mean a lot for me🙏.

Now official version 1.1 has been released at Monday, April 24, 2023 (GMT+8).

**Note** The CatChat is the client, and the MeoChat is the server, using Maven and Spring frameworks respectively. You may need to pay attention when deploying locally.

## 0.	Software Introduction

Cat Chat is an online chat software, a reverses project for QQ Chat. The client and server are written in Java, JDBC connects to the Postgre database, JavaFX writes the frontend interface, and CSS is used for interface beautification. Cat Chat supports personal chat and group chat, and the front-end interface is completely written in JavaFX (FX8), providing exquisite front-end interfaces and expanded displays (user online status, message sending and receiving status, dynamic components, flat bubbles), you can hardly notice any traces of JavaFX. Of course, Cat Chat still has some dynamic displays that need to be beautified, such as the dynamic switching function of the friend bar and group chat bar, but for now, I think Cat Chat has reached a considerable level of flatness and simplicity.



## 1.	Interface Introduction

### 1.1	login interface

To use Cat Chat, you must first log in to your account, enter your user account and password on the login page for comparison, they will be verified with the database (this part of the code might has security risks, such as directly obtaining the real password of a certain account in the database). After successful verification, you will enter the chat interface. I have applied some dynamic components based only on JavaFX, such as dynamic buttons with underlines and dynamic buttons with automatic filling, which are controlled and beautified by CSS.

<img src="reportImages\Login1.1.png" style="zoom: 40%;" />


### 1.2	Main interface

As you can see, Cat Chat adopts a mainstream flat interface design, with the user area in the upper left, the information area in the lower left, and the chat area on the right. For the user area, you can switch the display of the information area (friends or groups) by pressing different buttons. For the information area, I rewrote some code to achieve dynamic loading of friend login status. The green circle represents online and the red circle represents offline, so that users can intuitively understand their friends’ situation. For the chat area, the code realizes bubble text length adaptation to provide users with a good reading experience. The input in the lower right corner also limits the maximum text input to prevent some malicious injection. Finally, all interfaces and front-end components have been processed or rewritten to provide a good user experience.

<img src="reportImages\chatfriend1.1.png" style="zoom:67%;" />


For the event of receiving a friend message or group chat message, Cat Chat will set the shadow of the tab that receives the message to red to remind you that there are unread messages.

<img src="reportImages\chat group1.1.png" style="zoom:67%;" />


### 1.3	Error prompt interface

For sudden crashes of the client and server and other situations, Cat Chat has handled various errors to achieve internal consistency and complete program operation. Local serialization of chat data can be performed here.

<img src="G:\GitRepository\Cat-Chat-Online-Chat-Software\reportImages\exception.png" style="zoom: 67%;" />




## 2.	Code Details

### 2.1	Project Structure
```
CatChat:.
├─.mvn
│  └─wrapper
├─lib
├─report
│  └─reportImages
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
│      │              ├─node_characterai
│      │              └─testAssembly
│      └─resources
│          ├─AvatarIcon
│          ├─com
│          │  └─ghosnp
│          │      └─catchat
│          ├─css
│          ├─images
│          └─node_characterai
└─target
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

### 2.5 Local User Folder

Cat Chat adopts a localized data storage method, stores the user's chat records locally in json format, and stores the files received by the user in the corresponding folder.

After the user logs in, the past chat records can be displayed by clicking the button on the corresponding chat page.

In the case of local file loss, the program will report an error and automatically repair the storage structure. As a chat software that strives to achieve high security, Cat Chat will not store any user chat records on the server side, and cannot restore past chat records.

### 2.6 Chat with GPT Model

Due to the privacy agreement, Cat Chat will not collect any user data (except account information), nor will it provide any private data or interface. Therefore, in the future, Cat Chat will use the more open chat model provided by Character AI to chat with users. Due to network problems in some countries and regions, this feature may not work successfully on your device.

The implementation of this part of the function mainly depends on the unofficial API, which is written in JavaScript and called inline through Java. **To run this function, you may need to configure the local node environment.**

<img src="reportImages\gpt.png" style="zoom: 67%;" />



## 3.	Work and Schedule

**This project will be open source around April 25th.**

For version 1.1, all the expected functions of version 1.0 are basically realized. Version 1.2 will provide users with the ability to modify personal information; add video chat function, and deploy a simple face recognition model I completed last year, providing emoticons Anonymous video chat features that cover users' faces.


- [x] Dynamic Components and Flat UI
- [x] Stable connection and safe exit in abnormal state
- [x] Emoticons and file transfer
- [x] Dynamic user information display switching
- [x] ChatGPT model
- [ ] Open customized user information modification
- [ ] Java CV and CNN Face detection model deploy
