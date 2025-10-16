package com.logmate.config.puller;

import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.PullerConfig;
import com.logmate.config.data.TailerConfig;
import com.logmate.config.data.pipeline.ExporterConfig;
import com.logmate.config.data.pipeline.FallbackStorageConfig;
import com.logmate.config.data.pipeline.FilterConfig;
import com.logmate.config.data.pipeline.LogPipelineConfig;
import com.logmate.config.data.pipeline.MultilineConfig;
import com.logmate.config.data.pipeline.ParserConfig;
import com.logmate.config.data.pipeline.ParserConfig.ParserDetailConfig;
import com.logmate.config.puller.dto.ConfigDTO.AgentConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto.ExporterConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto.FilterConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto.MultilineConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto.ParserConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto.ParserConfigDto.ParserConfigDetailDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto.TailerConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.PullerConfigDto;
import com.logmate.processor.parser.LogType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigConverter {

  public AgentConfig convert(AgentConfigDto dto) {
    return new AgentConfig(
        dto.getAgentId(),
        dto.getAccessToken(),
        dto.getEtag()
    );
  }

  public PullerConfig convert(PullerConfigDto dto) {
    return new PullerConfig(
        dto.getPullURL(),
        dto.getIntervalSec(),
        dto.getEtag()
    );
  }

  public TailerConfig convert(TailerConfigDto dto) {
    return new TailerConfig(
        dto.getFilePath(),
        dto.getReadIntervalMs(),
        dto.getMetaDataFilePathPrefix()
    );
  }

  public MultilineConfig convert(MultilineConfigDto dto) {
    return new MultilineConfig(
        Objects.requireNonNullElse(dto.getEnabled(), false),
        Objects.requireNonNullElse(dto.getMaxLines(), 0)
    );
  }

  public ExporterConfig convert(ExporterConfigDto dto) {
    return new ExporterConfig(
        dto.getPushURL(),
        dto.getCompressEnabled(),
        dto.getRetryIntervalSec(),
        dto.getMaxRetryCount()
    );
  }

  public FallbackStorageConfig convert(ParserConfigDto parserConfigDto, TailerConfigDto tailerConfigDto) {
    return new FallbackStorageConfig(
        tailerConfigDto.getFilePath() + "_fallback.jsonl",
        LogType.valueOf(parserConfigDto.getType().toUpperCase())
    );
  }

  public ParserConfig convert(ParserConfigDto dto) {
    ParserConfigDetailDto newDetailConfig = dto.getConfig();
    ParserDetailConfig detailConfig = new ParserDetailConfig(newDetailConfig.getTimezone());
    return new ParserConfig(dto.getType(), detailConfig);
  }

  public FilterConfig convert(FilterConfigDto dto) {
    // 키워드: lower-case
    Set<String> allowedKeywords = Optional.ofNullable(dto.getAllowedKeywords())
        .orElseGet(Set::of)
        .stream()
        .map(key -> key.trim().toLowerCase())
        .collect(Collectors.toSet());

    // 레벨: upper-case
    Set<String> allowedLevels = Optional.ofNullable(dto.getAllowedLevels())
        .orElseGet(Set::of)
        .stream()
        .map(level -> level.trim().toUpperCase())
        .collect(Collectors.toSet());

    // 메서드: upper-case
    Set<String> allowedMethods = Optional.ofNullable(dto.getAllowedMethods())
        .orElseGet(Set::of)
        .stream()
        .map(method -> method.trim().toUpperCase())
        .collect(Collectors.toSet());

    return new FilterConfig(
        allowedLevels,
        allowedKeywords,
        allowedMethods
    );
  }


  public LogPipelineConfig convert(LogPipelineConfigDto dto) {
    return new LogPipelineConfig(
        dto.getEtag(),
        dto.getThNum(),
        convert(dto.getTailer()),
        convert(dto.getMultiline()),
        convert(dto.getExporter()),
        convert(dto.getParser()),
        convert(dto.getFilter()),
        convert(dto.getParser(), dto.getTailer())
    );
  }

  public List<LogPipelineConfig> convert(List<LogPipelineConfigDto> dtos) {
    return dtos.stream()
        .map(this::convert)
        .toList();
  }
}
