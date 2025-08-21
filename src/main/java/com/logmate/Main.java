package com.logmate;

import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.puller.ConfigPullerRunManager;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    String agentId = null;

    // args 배열에서 --agentId=value 형태의 인자를 찾음
    Optional<String> agentIdArg = Arrays.stream(args)
        .filter(arg -> arg.startsWith("--agentId="))
        .findFirst();

    if (agentIdArg.isPresent()) {
      // --agentId= 뒤의 값을 추출
      agentId = agentIdArg.get().substring("--agentId=".length());
      log.info("Argument agentId: {}", agentId);
    } else {
      // 인자가 없을 경우 기본값 설정
      agentId = "agent-uuid1";
      log.warn("Argument agentId is not found. Using default agentId: {}", agentId);
    }

    AgentConfigHolder.get().setAgentId(agentId);
    ConfigPullerRunManager.start();
  }
}