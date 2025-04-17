package com.paia.backend.model.deepchat;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DeepChatErrorResponse {
  private final String error;
}
