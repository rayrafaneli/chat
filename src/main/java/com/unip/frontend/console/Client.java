package com.unip.frontend.console;

import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private Scanner scanner;


    public Client() {
        try {
            scanner = new Scanner(System.in);

            System.out.print("Digite o IP do servidor (pressione Enter para localhost): ");
            String serverIP = scanner.nextLine();

            if (serverIP.isEmpty()) {
                serverIP = "localhost";
            }

            socket = new Socket(serverIP, 12345);

            System.out.println("Conectado ao servidor em " + serverIP + ":12345");
            System.out.println("Informe seu usuário com /login usuario");

            // Começa a escutar e enviar mensagens
            new ReadThread(socket).start();
            new WriteThread(socket).start();

        } catch (Exception e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
