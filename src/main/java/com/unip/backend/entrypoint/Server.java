package com.unip.backend.entrypoint;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.unip.backend.model.ChatRoom;
import com.unip.backend.model.User;
import com.unip.shared.enums.Common;

public class Server {
    private Integer port;
    private ServerSocket serverSocket;
    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    public Server(Integer port) {
        this.port = port;

        ChatRoom chatRoom = new ChatRoom("Todos");
        chatRooms.putIfAbsent(Common.SERVER_ALL.getValue(), chatRoom);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress());

            new Thread(new ClientHandler(clientSocket, users, chatRooms)).start();
        }
    }

    public static void main(String[] args) {
        int port = 12345; // Porta padrão

        try {
            Server server = new Server(port);
            server.start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
