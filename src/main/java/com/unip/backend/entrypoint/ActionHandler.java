package com.unip.backend.entrypoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unip.backend.model.ChatRoom;
import com.unip.backend.model.User;
import com.unip.shared.Message;
import com.unip.shared.MessageMapper;
import com.unip.shared.enums.Action;
import com.unip.shared.enums.Common;
import com.unip.shared.enums.MessageType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ActionHandler {
    private final Socket socket;
    private final PrintWriter out;
    private final User user;
    private final Map<String, User> users;
    private final Map<String, ChatRoom> chatRooms;

    private void sendMessage(Action action, Boolean success, String content) {
        sendMessage(action, success, MessageType.CONFIG, content, out);
    }

    private void sendMessage(Action action, Boolean success, MessageType messageType, String content, PrintWriter out) {
        Message message = Message.builder()
                .from(user.getUsername())
                .action(action)
                .success(success)
                .messageType(messageType)
                .content(content)
                .build();

        sendMessage(message, out);
    }

    private void sendMessage(Message message, PrintWriter out) {
        String json = MessageMapper.toJson(message);

        out.println(json);
    }

    public void handleError() {
        sendMessage(Action.ERROR, false, "Ação não reconhecida.");
    }

    public void handleLogin(Message message) throws IOException {
        if (Objects.nonNull(user.getUsername())) {
            sendMessage(Action.LOGIN, false, "Usuário [" + user.getUsername() + "] já conectado.");
            return;
        }

        String username = message.getContent();

        if (Objects.isNull(username) || username.trim().isEmpty() || users.containsKey(username)) {
            sendMessage(Action.LOGIN, false, "Nome inválido ou já usado. Escolha outro.");
            return;
        }

        user.setUsername(username);
        users.putIfAbsent(username, user);
        sendMessage(Action.LOGIN, true, "Usuário conectado. Utilize /help para ajuda.");

        chatRooms.get(Common.SERVER_ALL.getValue()).addUser(user);
    }

    public void handleHelp() {
        sendMessage(Action.HELP, true, "Utilze /join ou /private");
    }

    public void handleJoin(Message message) {
        String roomName = message.getContent();
        ChatRoom chatRoom = new ChatRoom(roomName);

        chatRooms.putIfAbsent(roomName, chatRoom);
        chatRoom.addUser(user);

        sendMessage(Action.JOIN, true, "Você entrou na sala: " + roomName);
    }

    public void handleChatRoom(Message message) {
        String to = message.getTo();
        message.setFrom(user.getUsername());

        if (Objects.isNull(to) || !chatRooms.containsKey(to)) {
            sendMessage(Action.ROOM, false, "Sala inválida: " + to);
            return;
        }

        var chatRoom = chatRooms.get(to);

        if (!chatRoom.hasUser(user)) {
            sendMessage(Action.ROOM, false, "Usuário não está na sala: " + to);
            return;
        }

        chatRoom.getUsers().stream().forEach(userInChat -> {
            sendMessage(message, userInChat.getOut());
        });
    }

    public void handlePrivate(Message message) {
        String to = message.getTo();
        message.setFrom(user.getUsername());

        if (Objects.isNull(to) || !users.containsKey(to)) {
            sendMessage(Action.PRIVATE, false, "Usuário não encontrado: " + to);
            return;
        }

        var targetUser = users.get(to);

        sendMessage(message, user.getOut());
        sendMessage(message, targetUser.getOut());
    }

    public void handleDisconnect() {
        try {
            if (Objects.nonNull(user)) {
                users.remove(user.getUsername());

                for (ChatRoom room : chatRooms.values()) {
                    if (room.hasUser(user)) {
                        room.removeUser(user);
                    }
                }

                System.out.println("\n\nUsuário desconectado: " + user.getUsername());
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("\n\nErro ao fechar conexão: " + e.getMessage());
        }
    }

    public void handleList(Message message) {
        String content = message.getContent();
        List<String> chatNames = new ArrayList<>();

        if (Objects.isNull(content) || content.isEmpty()) {
            chatNames.addAll(getUsernames());
            chatNames.addAll(getChatRoomNames());
        } else if (Objects.equals(content, "users")) {
            chatNames.addAll(getUsernames());
        } else if (Objects.equals(content, "chatrooms")) {
            chatNames.addAll(getChatRoomNames());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            sendMessage(Action.LIST, true, objectMapper.writeValueAsString(chatNames));
        } catch (JsonProcessingException e) {
            sendMessage(Action.LIST, false, "Erro ao processar os chats");

            System.out.println("Erro ao processar os chats: " + e.getMessage());
        }
    }

    private List<String> getUsernames() {
        return users.values().stream().map(User::getUsername).toList();
    }

    private List<String> getChatRoomNames() {
        return chatRooms.values().stream().map(ChatRoom::getName).toList();
    }

    public void handleNotConnected() {
        sendMessage(Action.ERROR, false, "Para utilizar o sistema é necessário fazer o login.");
    }
}
