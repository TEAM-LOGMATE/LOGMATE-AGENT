package com.logmate.init.argument;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationArgumentHandler {

  public void handle(String[] args) {
    validate(args);
findAgentId(args).get()
  }

  public void validate(String[] args) {
     List<String> findResult =  Arrays.stream(args)
        .filter(arg -> arg.startsWith("--agentId="))
        .toList();

     if (findResult.isEmpty()) {
       log.error("[ApplicationArgumentChecker] Missing required argument: --agentId=value");
       throw new IllegalArgumentException("Missing required argument: --agentId=value");
     }

     if (findResult.size() > 1) {
       log.error("[ApplicationArgumentChecker] Duplicate argument detected: --agentId=value ({} occurrences)", findResult.size());
       throw new IllegalArgumentException("Invalid argument: --agentId=value (duplicate)");
     }
  }

  private Optional<String> findAgentId(String[] args) {
    // args 배열에서 --agentId=value 형태의 인자를 찾음
    return Arrays.stream(args)
        .filter(arg -> arg.startsWith("--agentId="))
        .findFirst()
        .get()
  }


}
