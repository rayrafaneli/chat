package com.unip.backend.model;

import java.io.PrintWriter;
import java.net.Socket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String username;
    private Socket socket;
    private PrintWriter out;
}
