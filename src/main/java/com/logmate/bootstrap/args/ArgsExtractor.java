package com.logmate.bootstrap.args;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArgsExtractor {

  private static final List<String> REQUIRED_KEYS = List.of("agentId", "email", "password");
  private static final String PREFIX = "--";

  public AgentArguments extract(String[] args) {

    // args → Map<String, String> 형태로 변환
    Map<String, String> argMap = Arrays.stream(args)
        .filter(arg -> arg.startsWith(PREFIX))
        .map(arg -> arg.substring(2).split("=", 2))
        .filter(pair -> pair.length == 2)
        .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1], (a, b) -> b));


    String agentId = argMap.get("agentId");
    String email = argMap.get("email");
    String password = argMap.get("password");

    log.info("[ArgsExtractor] Resolved agentId={}, email={}, password=****", agentId, email);

    return new AgentArguments(agentId, email, password);
  }

  private void validate(Map<String, String> argMap) {
    // 필수 인자 검증
    for (String key : REQUIRED_KEYS) {
      if (argMap.get(key).isBlank()) {
        log.error("[ArgsExtractor] Missing required argument: --{}=<value>", key);
        throw new IllegalArgumentException("Missing required argument: --" + key + "=<value>");
      } else if (!argMap.containsKey(key)) {
        log.error("[ArgsExtractor] Unknown argument key: --{}=<value>", key);
        throw new IllegalArgumentException("Unknown argument key: --" + key + "=<value>");
      }
    }
  }
}

