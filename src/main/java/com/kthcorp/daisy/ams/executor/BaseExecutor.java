package com.kthcorp.daisy.ams.executor;

import com.kthcorp.daisy.ams.fao.SourceHandler;
import com.kthcorp.daisy.ams.fileio.FileIO;
import com.kthcorp.daisy.ams.indexstore.IndexStore;
import com.kthcorp.daisy.ams.repository.RecInfoMapper;
import com.kthcorp.daisy.ams.repository.entity.RecFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    public CompletableFuture<String> execute() throws Exception {
        String result = null;

        log.debug("Looking up {}", "execute");

        List<ExecuteFileInfo> executeFileInfos = getExecuteFileInfos();

        Collections.sort(executeFileInfos, Comparator.comparing(ExecuteFileInfo::getSourceFile));

        long totalTaskCount = executeFileInfos.stream().filter(x -> !x.isFinished()).count();
        log.info("Task count -> {}", totalTaskCount);
        for (ExecuteFileInfo executeFileInfo : executeFileInfos) {

            if (!executeFileInfo.isFinished()) {

                try {
                    log.debug("RecFile: {}", executeFileInfo.getSourceFile().getFileName());
                    executeTask(executeFileInfo);
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    executeFileInfo.setFinished(true);
                }
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

    public void executeTask(ExecuteFileInfo executeFileInfo) throws Exception {
        // 20180517053610_20180517_0109_32#201804019-180416GSPB30_201804032-180420RDLN15_201804028-180417TMSD30.MP4
        String[] fileArray = FilenameUtils.getBaseName(executeFileInfo.getSourceFile().getFileName()).split("#");
        if (fileArray.length > 1) {
            // firFileInfo : [20180517053610, 20180517, 0109, 32]
            // secFileInfo : [201804019-180416GSPB30, 201804032-180420RDLN15, 201804028-180417TMSD30]
            List<String> firFileInfo = new ArrayList<>(Arrays.asList(fileArray[0].split("_", 4)));
            List<String> secFileInfo = new ArrayList<>(Arrays.asList(fileArray[1].split("_")));
            log.debug("firFileInfo : {}", firFileInfo);
            log.debug("secFileInfo : {}", secFileInfo);

            List<Map<String, Object>> resultRecInfoFiles = new ArrayList<>();

            for (int i = 0; i < fileArray.length - 1; i++) {
                for (int j = 0; j < secFileInfo.size(); j++) {
                    Map<String, Object> tMap1 = new LinkedHashMap<>();
                    String temp1 = fileArray[0];
                    StringTokenizer tokenizer1 = new StringTokenizer(temp1, "_");
                    while (tokenizer1.hasMoreTokens()) {
                        tMap1.put("startDt", tokenizer1.nextToken());
                        tMap1.put("brdcstDt", tokenizer1.nextToken());
                        tMap1.put("chId", tokenizer1.nextToken());
                        tMap1.put("chNo", tokenizer1.nextToken());
                    }
                    String temp2 = secFileInfo.get(j);
                    StringTokenizer tokenizer2 = new StringTokenizer(temp2, "-");
                    while (tokenizer2.hasMoreTokens()) {
                        tMap1.put("aplnFormId", tokenizer2.nextToken());
                        tMap1.put("adId", tokenizer2.nextToken());
                    }
                    resultRecInfoFiles.add(tMap1);
                }
            }

            resultRecInfoFiles.stream().forEach(x -> {
                RecFileInfo recFileInfo = new RecFileInfo();
                recFileInfo.setStartDt((String) x.get("startDt"));
                recFileInfo.setAplnFormId((String) x.get("aplnFormId"));
                recFileInfo.setAdId((String) x.get("adId"));
                recFileInfo.setChId((String) x.get("chId"));
                recFileInfo.setOtvChNo((String) x.get("chNo"));
                recFileInfo.setBrdcstDt((String) x.get("brdcstDt"));
                recFileInfo.setYyyyMMdd(executeFileInfo.getSourceFile().getYyyyMMdd());
                recFileInfo.setRecFilePath(executeFileInfo.getSourceFile().getAbsolutePath());
                recFileInfo.setRecThumbFilePath(executeFileInfo.getSourceFile().getThumbAbsolutePath());

                recInfoMapper.insertRecFileInfo(recFileInfo);
                executeFileInfo.setSuccess(true);

            });


        } else {
            log.info("The idx file line split count is not 5. recFilePath -> {}", executeFileInfo.getSourceFile().getAbsolutePath());
        }

//        String[] splitPath = executeFileInfo.getSourceFile().getFileName().split("\\.");
//        String[] splits = splitPath[0].split("_", 5);
//        // 20180508101134_201804135_180201CHAM5_17_0122.MP4
//        RecFileInfo recFileInfo = null;
//        if (splits.length == 5) {
//            for (int i = 0; i < splitPath.length - 1; i++) {
//                recFileInfo = new RecFileInfo();
//                recFileInfo.setStartDt(splits[0]);
//                recFileInfo.setAplnFormId(splits[1]);
//                recFileInfo.setAdNo(splits[2]);
//                recFileInfo.setOtvChNo(splits[3]);
//                recFileInfo.setChId(splits[4]);
//                recFileInfo.setYyyyMMdd(executeFileInfo.getSourceFile().getYyyyMMdd());
//                recFileInfo.setRecFilePath(executeFileInfo.getSourceFile().getAbsolutePath());
//                recFileInfo.setRecThumbFilePath(executeFileInfo.getSourceFile().getThumbAbsolutePath());
//            }
//            try {
//                recInfoMapper.insertRecFileInfo(recFileInfo);
//                executeFileInfo.setSuccess(true);
//            } catch (Exception e) {
//                throw e;
//            }
//        } else {
//            log.info("The idx file line split count is not 5. recFilePath -> {}", executeFileInfo.getSourceFile().getAbsolutePath());
//        }
    }
}
