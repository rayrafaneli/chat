package com.unip.shared;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unip.shared.enums.Action;
import com.unip.shared.enums.MessageType;

public class MessageMapper {
    private MessageMapper() {
    }

    private static List<Action> actionsWithTo = List.of(Action.ROOM, Action.PRIVATE);

    public static String toJson(Message message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            System.out.println("Ocorreu um erro ao converter a mensagem para json.");
            System.out.println("Erro: " + e.getMessage());

            return "";
        }
    }

    public static Message fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.readValue(json, Message.class);
        } catch (Exception e) {
            System.out.println("Erro ao converter json para mensagem.");
            System.out.println("Erro: " + e.getMessage());

            return Message.builder().build();
        }
    }

    public static Message to(String content) {
        Action action = null;
        String to = null;

        if (Objects.isNull(content)) {
            content = "";
        }

        if (content.startsWith("/")) {
            content = content.substring(1);

            String[] parts = content.split(" ", 2);
            content = parts.length > 1 ? parts[1] : "";

            try {
                action = Action.valueOf(parts[0].toUpperCase());

                if (actionsWithTo.contains(action)) {
                    parts = content.split(" ", 2);
                    to = parts[0];

                    if (parts.length > 1) {
                        content = parts[1];
                    }
                }
            } catch (Exception e) {
            }
        }

        return Message.builder()
                .to(to)
                .messageType(MessageType.MESSAGE)
                .action(action)
                .content(content)
                .build();
    }
}
