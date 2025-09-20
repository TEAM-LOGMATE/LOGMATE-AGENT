package com.logmate.tailer.impl;

import com.logmate.config.data.TailerConfig;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import com.logmate.processor.listener.LogEventListener;
import com.logmate.tailer.LogTailer;
import java.util.StringTokenizer;


/**
 * FileLogTailer는 지정된 로그 파일을 tailing하면서 새로운 로그가 발생할 때마다 LogEventListener를 통해 이벤트를 전달하는 클래스입니다.
 */
public class FileLogTailer implements LogTailer {

  private final TailerConfig config;
  //로그 수신 시 이벤트를 처리할 리스너
  private final LogEventListener listener;
  // 감시할 로그 파일
  private final File logFile;
  private final File metaDataFile;
  // 마지막으로 읽은 파일 위치 (바이트 기준)
  private long lastKnownPosition;

  /**
   * Tailer 설정과 이벤트 리스너를 받아 초기화
   *
   * @param config 테일러 설정
   * @param listener 로그 이벤트 리스너
   */
  public FileLogTailer(TailerConfig config, LogEventListener listener, Integer thNum) {
    this.config = config;
    this.listener = listener;
    this.logFile = new File(config.getFilePath());

    File metaDataFile = new File(config.getMetaDataFilePathPrefix() + thNum + ".txt");
    File parentDir = metaDataFile.getParentFile();

    if (parentDir != null && !parentDir.exists()) {
      parentDir.mkdirs();
    }

    try {
      // 메타데이터 파일이 없다면 생성
      if (metaDataFile.createNewFile()) {
        this.metaDataFile = metaDataFile;
        savePositionToFile(0); //파일에 포지션을 0으로 설정
        this.lastKnownPosition = 0;
      }
      else {
        this.metaDataFile = metaDataFile;
        this.lastKnownPosition = loadPositionFromFile(); // 메타데이터 파일이 존재한다면 포지션 가져오기
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Position 정보를 메타데이터 파일에 추가하는 메소드
   *
   * @param position  메타데이터 파일에 추가할 포지션 정보
   */
  private void savePositionToFile(long position) {
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(metaDataFile))) {
      bufferedWriter.write(logFile.getPath() + ":" + position);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 메타데이터 파일에서 포지션 정보를 불러오는 메소드
   *
   */
  private long loadPositionFromFile() {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(metaDataFile))) {
      StringTokenizer st = new StringTokenizer(bufferedReader.readLine(), ":");
      st.nextToken();
      return Long.parseLong(st.nextToken());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 로그 tailing 루프를 실행
   */
  @Override
  public void run() {
    try (RandomAccessFile file = new RandomAccessFile(logFile, "r")) {
      while (true) {
        long fileLength = logFile.length(); // 현재 로그 파일의 크기 확인

        // 로그 파일이 줄어들었으면, 처음부터 다시 읽도록 포지션을 초기화.
        if (fileLength < lastKnownPosition) {
          lastKnownPosition = 0; // 로그 파일이 롤링됨
        }

        // 새로운 로그가 추가되었을 경우
        if (fileLength > lastKnownPosition) {
          file.seek(lastKnownPosition);
          byte[] buffer = new byte[(int) (fileLength - lastKnownPosition)];
          file.readFully(buffer);

          // UTF-8로 디코딩 후 라인 단위로 처리
          String newContent = new String(buffer, "UTF-8");
          String[] lines = newContent.split("\\r?\\n");
          listener.onLogReceive(lines);
          lastKnownPosition = file.getFilePointer();
        }

        //fileLength == lastKnownPosition 이라면 새로운
        //로그가 추가되지 않은 상황이므로 아무것도 실행하지 않는다.
        
        savePositionToFile(lastKnownPosition); // 변경된 포지션 정보 메타데이터 파일에 저장
        Thread.sleep(config.getReadIntervalMs()); // 1초 간격으로 체크
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
