package com.unip.shared;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.unip.shared.enums.Action;
import com.unip.shared.enums.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Message implements Serializable {
    private String from;
    private String to;

    private MessageType messageType;

    private String content;

    @Builder.Default
    private LocalDateTime localDateTime = LocalDateTime.now();
    private Action action;

    private Boolean success;
}
