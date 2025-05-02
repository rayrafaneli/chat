package com.unip.backend.entrypoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.unip.backend.model.ChatRoom;
import com.unip.backend.model.User;
import com.unip.shared.Message;
import com.unip.shared.MessageMapper;
import com.unip.shared.enums.Action;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private User user;
    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private ActionHandler actionHandler;

    // private UserRepository userRepository = new UserRepository();

    public ClientHandler(
            Socket socket,
            Map<String, User> users,
            Map<String, ChatRoom> chatRooms) {
        this.socket = socket;
        this.users = users;
        this.chatRooms = chatRooms;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            user = new User(null, socket, out);

            actionHandler = new ActionHandler(socket, out, user, users, chatRooms);

            String inputJson;
            while ((inputJson = in.readLine()) != null) {
                Message message = MessageMapper.fromJson(inputJson);

                if (Objects.isNull(message.getAction())) {
                    message.setAction(Action.ERROR);
                }

                if (!Action.LOGIN.equals(message.getAction())
                        && (Objects.isNull(user.getUsername())
                                || user.getUsername().isEmpty())) {
                    actionHandler.handleNotConnected();
                    continue;
                }

                switch (message.getAction()) {
                    case LOGIN:
                        actionHandler.handleLogin(message);
                        break;

                    case HELP:
                        actionHandler.handleHelp();
                        break;

                    case JOIN:
                        actionHandler.handleJoin(message);
                        break;

                    case ROOM:
                        actionHandler.handleChatRoom(message);
                        break;

                    case PRIVATE:
                        actionHandler.handlePrivate(message);
                        break;

                    case DISCONNECT:
                        actionHandler.handleDisconnect();
                        break;

                    case LIST:
                        actionHandler.handleList(message);
                        break;

                    default:
                        actionHandler.handleError();
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro de comunicação: " + e.getMessage());
        } finally {
            actionHandler.handleDisconnect();
        }
    }
}
