package com.unip.frontend.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

import com.unip.shared.MessageMapper;

public class ReadThread extends Thread {
    private BufferedReader reader;

    public ReadThread(Socket socket) {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Erro ao iniciar leitura: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String json = reader.readLine();
                if (Objects.isNull(json)) {
                    System.out.println("Servidor desconectou.");
                    break;
                }

                System.out.println(MessageMapper.fromJson(json));
            } catch (Exception e) {
                System.out.println("Erro ao ler mensagem: " + e.getMessage());
                break;
            }
        }
    }
}
