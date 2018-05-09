package com.kthcorp.daisy.ams.executor;

import com.kthcorp.daisy.ams.fao.SourceHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Slf4j
public abstract class BaseExecutor implements CommonExecutor {

    protected ApplicationContext context;
    final SourceHandler sourceHandler;
    private static final String SOURCE_CONFIG = "sourceConfig";

    BaseExecutor(ApplicationContext context, Map<String, Object> config) {
        this.context = context;
        SourceHandler sourceHandler = context.getBean(SourceHandler.class, config.get(SOURCE_CONFIG));
        this.sourceHandler = sourceHandler;
    }

//    @Async
//    public CompletableFuture<List<AmsUser>> executeTask1(String msg) throws Exception {
//
////        String result = null;
//
//        log.info("Looking up {}", msg);
//        List<AmsUser> result = amsUserMapper.findByUserName(msg);
//
//        // business logic flow
//        // 1. 아메바 .idx 파일 존재 체크
//
//        // 1-1. .idx 가 없을 경우 프로세스 종료, 매분 또는 일정 주기마다 .idx 가 있는지 체크 (sping boot 의 schedule 또는 cron 사용)
//        // .idx 가 있을 경우는 2~6번 로직을 수행 후 주기마다 체크 하지 않도록 zookeeper index 저장
//
//        // 2. 아메바 녹화완료된 .idx 파일 load, 데이터 db 저장
//
//        // 대안 3. 상위 30개 채널 쿼리 get + mss 프로그램 epg + 선천 광고 epg 매핑, 프로그램 사이사이 시간에 광고 epg 생성 되게 쿼리 생성
//        // 대안 3. 선천에서 보내준 광고 편성표 파일 데이터 db 저장
//
//        // 4. 긴급 광고 편성표 데이터가 있는지 체크
//
//        // 5. 2번 녹화완료된 데이터와 3번 쿼리 데이터 매핑해서 녹화되지 않은 데이터 db 저장
//
//        // 6. 5번 데이터 + 4번 데이터 매핑, 4번 데이터는 최상위로 편성될 수 있게 생성
//
//        // 7. skylife 에서 제공하는 광고 파일 저장
//
//        // Artificial delay of 1s for demonstration purposes
//        Thread.sleep(1000L);
//        return CompletableFuture.completedFuture(result);
//    }

    abstract List<ExecuteFileInfo> getExecuteFileInfos() throws Exception;

    @Async
    public CompletableFuture<String> executeTask() throws Exception {
        String result = null;

        log.info("Looking up {}", "executeTask");

        List<ExecuteFileInfo> executeFileInfos = getExecuteFileInfos();

        // business logic flow
        // 1. 아메바 .idx 파일 존재 체크

        // 1-1. .idx 가 없을 경우 프로세스 종료, 매분 또는 일정 주기마다 .idx 가 있는지 체크 (sping boot 의 schedule 또는 cron 사용)
        // .idx 가 있을 경우는 2~6번 로직을 수행 후 주기마다 체크 하지 않도록 zookeeper index 저장

        // 2. 아메바 녹화완료된 .idx 파일 load, 데이터 db 저장

        // 대안 3. 상위 30개 채널 쿼리 get + mss 프로그램 epg + 선천 광고 epg 매핑, 프로그램 사이사이 시간에 광고 epg 생성 되게 쿼리 생성
        // 대안 3. 선천에서 보내준 광고 편성표 파일 데이터 db 저장

        // 4. 긴급 광고 편성표 데이터가 있는지 체크

        // 5. 2번 녹화완료된 데이터와 3번 쿼리 데이터 매핑해서 녹화되지 않은 데이터 db 저장

        // 6. 5번 데이터 + 4번 데이터 매핑, 4번 데이터는 최상위로 편성될 수 있게 생성

        // 7. skylife 에서 제공하는 광고 파일 저장

        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(result);
    }
}
