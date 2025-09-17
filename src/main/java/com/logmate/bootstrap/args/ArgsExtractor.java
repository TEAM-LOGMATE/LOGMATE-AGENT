package com.logmate.bootstrap.args;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArgsExtractor {

  private static final String ARG_KEY = "agentId";
  private static final String ARG_PREFIX = "--" + ARG_KEY + "=";

  public AgentArguments extract(String[] args) {
    List<String> matches = Arrays.stream(args)
        .filter(arg -> arg.startsWith(ARG_PREFIX))
        .toList();

    validate(matches);

    String agentId = matches.get(0).substring(ARG_PREFIX.length());
    log.info("[ArgsExtractor] Resolved agentId={}", agentId);

    return new AgentArguments(agentId);
  }

  private void validate(List<String> matches) {
    if (matches.isEmpty()) {
      log.error("[ArgsExtractor] Missing required argument: {}", ARG_PREFIX + "value");
      throw new IllegalArgumentException("Missing required argument: " + ARG_PREFIX + "value");
    }

    if (matches.size() > 1) {
      log.error("[ArgsExtractor] Duplicate argument detected: {} ({} occurrences)",
          ARG_PREFIX + "value", matches.size());
      throw new IllegalArgumentException("Invalid argument: duplicate " + ARG_PREFIX + "value");
    }
  }
}

