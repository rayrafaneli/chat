package com.unip;

import java.util.Objects;
import java.util.Scanner;

import com.unip.backend.entrypoint.Server;
import com.unip.frontend.console.Client;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String input = "";

            while (!Objects.equals(input, "exit")) {
                try {
                    System.out.println("[S]ervidor ou [C]liente?");
                    System.out.print("> ");
                    input = scanner.nextLine();

                    if (Objects.nonNull(input)) {
                        if (Objects.equals(input.toUpperCase(), "C")) {
                            Client.main(args);
                        } else if (Objects.equals(input.toUpperCase(), "S")) {
                            Server.main(args);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("\n\n\n\nHouve um erro: " + e.getMessage());
                }
            }
        }
    }
}
