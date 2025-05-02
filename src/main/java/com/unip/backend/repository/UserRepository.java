package com.unip.backend.repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserRepository {
    private static final String FILE_PATH = "users.json";
    private final ObjectMapper objectMapper;

    public UserRepository() {
        this.objectMapper = new ObjectMapper();
    }

    public void save(String name) {
        try {
            objectMapper.writeValue(new File(FILE_PATH), name);
        } catch (Exception e) {
            System.err.println("Error writing users to file: " + e.getMessage());
        }
    }

    public String find() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return "";
            }

            String jsonContent = Files.readString(Paths.get(FILE_PATH));
            System.out.println(jsonContent);

            return jsonContent;
        } catch (Exception e) {
            return "Erro";
        }
    }
}
