package com.logmate.bootstrap.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationRequestDTO {

  String agentId;

  public AuthenticationRequestDTO(AuthToken authToken) {
    this.agentId = authToken.agentId();
  }
}
