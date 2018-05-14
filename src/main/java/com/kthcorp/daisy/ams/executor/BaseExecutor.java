package com.kthcorp.daisy.ams.executor;

import com.kthcorp.daisy.ams.fao.SourceHandler;
import com.kthcorp.daisy.ams.fileio.FileIO;
import com.kthcorp.daisy.ams.indexstore.IndexStore;
import com.kthcorp.daisy.ams.repository.RecInfoMapper;
import com.kthcorp.daisy.ams.repository.entity.RecFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Slf4j
public abstract class BaseExecutor implements CommonExecutor {

    protected ApplicationContext context;
    protected IndexStore indexStore;
    final SourceHandler sourceHandler;
    final FileIO fileIO;
    private static final String INDEX_CONFIG = "indexConfig";
    private static final String SOURCE_CONFIG = "sourceConfig";
    private static final String FILE_IO_CONFIG = "fileIOConfig";

    BaseExecutor(ApplicationContext context, Map<String, Object> config) {
        this.context = context;
        IndexStore indexStore = context.getBean(IndexStore.class, config.get(INDEX_CONFIG));
        this.indexStore = indexStore;
        SourceHandler sourceHandler = context.getBean(SourceHandler.class, config.get(SOURCE_CONFIG));
        this.sourceHandler = sourceHandler;
        FileIO fileIO = context.getBean(FileIO.class, config.get(FILE_IO_CONFIG));
        this.fileIO = fileIO;
    }

    abstract List<ExecuteFileInfo> getExecuteFileInfos() throws Exception;

    abstract void setIndex(ExecuteFileInfo executeFileInfo) throws Exception;

    @Autowired
    RecInfoMapper recInfoMapper;

    @Transactional
    @Async
    public CompletableFuture<String> executeTask() throws Exception {
        String result = null;

        log.debug("Looking up {}", "executeTask");

        List<ExecuteFileInfo> executeFileInfos = getExecuteFileInfos();

        Collections.sort(executeFileInfos, Comparator.comparing(ExecuteFileInfo::getSourceFile));

        long totalTaskCount = executeFileInfos.stream().filter(x -> !x.isFinished()).count();
        log.info("Task count -> {}", totalTaskCount);
        for (ExecuteFileInfo executeFileInfo : executeFileInfos) {

            if (!executeFileInfo.isFinished()) {

                try {
                    log.debug("RecFile: {}", executeFileInfo.getSourceFile().getFileName());
                    String[] splitPath = executeFileInfo.getSourceFile().getFileName().split("\\.");
                    String[] splits = splitPath[0].split("_", 5);
                    // 20180508101134_201804135_180201CHAM5_17_0122.MP4
                    RecFileInfo recFileInfo = null;
                    if (splits.length == 5) {
                        for (int i = 0; i < splitPath.length - 1; i++) {
                            recFileInfo = new RecFileInfo();
                            recFileInfo.setStartDt(splits[0]);
                            recFileInfo.setAplnFormId(splits[1]);
                            recFileInfo.setAdNo(splits[2]);
                            recFileInfo.setOtvChNo(splits[3]);
                            recFileInfo.setChId(splits[4]);
                            recFileInfo.setYyyyMMdd(executeFileInfo.getSourceFile().getYyyyMMdd());
                            recFileInfo.setRecFilePath(executeFileInfo.getSourceFile().getAbsolutePath());
                            recFileInfo.setRecThumbFilePath(executeFileInfo.getSourceFile().getThumbAbsolutePath());
                        }
                        try {
                            recInfoMapper.insertRecFileInfo(recFileInfo);
                        } catch (Exception e) {
                            throw e;
                        }
                    } else {
                        log.info("The idx file line split count is not 5. recFilePath -> {}", executeFileInfo.getSourceFile().getAbsolutePath());
                    }
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    executeFileInfo.setFinished(true);
                }
                executeFileInfo.setSuccess(true);
                setIndex(executeFileInfo);
            }

        }

        // business logic flow
        // 1. 아메바 .idx 파일 존재 체크
        // 1-1. .mp4, .jpg 파일 존재 체크
        // 1-2. 아메바 녹화완료된 .idx 파일 load, 데이터 db 저장

        // 1-1. .idx 가 없을 경우 프로세스 종료, 매분 또는 일정 주기마다 .idx 가 있는지 체크 (sping boot 의 schedule 또는 cron 사용)
        // .idx 가 있을 경우는 2~6번 로직을 수행 후 주기마다 체크 하지 않도록 zookeeper index 저장



        // 2. 상위 30개 채널 쿼리 get + mss 프로그램 epg + 선천 광고 epg 매핑,
        // 프로그램 종료시간 -15분을 start_dt, 15분후를 end_dt 로 기준 정함, 광고 epg 생성 되게 쿼리 생성

        // 4. 긴급 광고 편성표 데이터가 있는지 체크

        // 5. 2번 녹화완료된 데이터와 3번 쿼리 데이터 매핑해서 녹화되지 않은 데이터 db 저장

        // 6. 5번 데이터 + 4번 데이터 매핑, 4번 데이터는 최상위로 편성될 수 있게 생성

        // 7. skylife 에서 제공하는 광고 파일 저장

        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(result);
    }
}
