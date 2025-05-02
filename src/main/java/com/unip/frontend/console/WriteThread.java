package com.unip.frontend.console;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

import com.unip.shared.Message;
import com.unip.shared.MessageMapper;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Scanner scanner;

    public WriteThread(Socket socket) {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
        } catch (Exception e) {
            System.out.println("Erro ao iniciar escrita: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String content = scanner.nextLine();

                if (Objects.nonNull(content) && !content.trim().isEmpty()) {
                    Message message = MessageMapper.to(content);
                    String json = MessageMapper.toJson(message);

                    writer.println(json);
                }
            } catch (Exception e) {
                System.out.println("Erro ao enviar mensagem: " + e.getMessage());
            }
        }
    }
}
