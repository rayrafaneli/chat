package com.unip.backend.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class ChatRoom {
    private String name;
    private Set<User> users = new HashSet<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public boolean hasUser(User user) {
        return users.contains(user);
    }
}
