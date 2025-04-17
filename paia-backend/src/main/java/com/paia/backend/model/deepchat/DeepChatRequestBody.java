package com.paia.backend.model.deepchat;

import lombok.Data;

@Data
public class DeepChatRequestBody {
    private DeepChatMessageContent[] messages;
    private String model;
    private Boolean stream;

    private Long cartId;
}
