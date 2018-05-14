package com.kthcorp.daisy.ams.executor;

import com.kthcorp.daisy.ams.fao.RemoteFileInfo;
import com.kthcorp.daisy.ams.fileio.FileIOInfo;
import com.kthcorp.daisy.ams.repository.RecInfoMapper;
import com.kthcorp.daisy.ams.repository.entity.RecFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Slf4j
public class StoredRecInfoExecutor extends BaseExecutor {

    StoredRecInfoExecutor(ApplicationContext context, Map<String, Object> config) throws Exception {
        super(context, config);
    }

    List<ExecuteFileInfo> executeFileInfos = new ArrayList<>();

    List<ExecuteFileInfo> getExecuteFileInfos() throws Exception {
        log.debug("Looking up {}", "getExecuteFileInfos");

        int logIdx = 1;
        List<String> indexStr = indexStore.getIndexAsList();
        List<RemoteFileInfo> remoteFiles = sourceHandler.getRemoteFiles();

        Set<String> finFiles = new HashSet<>();
        List<RemoteFileInfo> idxFiles = new ArrayList<>();
        List<RemoteFileInfo> recFiles = new ArrayList<>();
//        Set<String> thumbFiles = new HashSet<>();
        List<RemoteFileInfo> thumbFiles = new ArrayList<>();

        for (RemoteFileInfo remoteFile : remoteFiles) {
            if (remoteFile.getFileName().toUpperCase().endsWith(".FIN")) {
                finFiles.add(remoteFile.getFileName().substring(0, remoteFile.getFileName().indexOf(".")));
            } else if (remoteFile.getFileName().toUpperCase().endsWith(".IDX")) {
                idxFiles.add(remoteFile);
            } else if (remoteFile.getFileName().toUpperCase().endsWith(".MP4")) {
                recFiles.add(remoteFile);
            } else if (remoteFile.getFileName().toUpperCase().endsWith(".JPG")) {
//                thumbFiles.add(remoteFile.getFileName().substring(0, remoteFile.getFileName().indexOf(".")));
                thumbFiles.add(remoteFile);
            }
        }

        // .FIN 파일이 있는지 체크
        List<RemoteFileInfo> finCheckFiles = idxFiles.stream().filter(f ->
                !finFiles.isEmpty()).collect(Collectors.toList());

        List<FileIOInfo> fileIOFiles;
        List<RemoteFileInfo> resultRecFiles = new ArrayList<>();
        if (finCheckFiles != null && finCheckFiles.size() > 0) {
            // thumb path set
            recFiles.forEach(x -> {
                thumbFiles.forEach(y ->
                    {
                        if (FilenameUtils.getBaseName(x.getFileName()).equals(FilenameUtils.getBaseName(y.getFileName()))) {
                            x.setThumbAbsolutePath(y.getAbsolutePath());
                        }
                    }
                );
            });

            resultRecFiles = recFiles.stream().map(x -> {
                RemoteFileInfo remoteFileInfo = new RemoteFileInfo();
                idxFiles.stream().forEach(y -> {
                    remoteFileInfo.setYyyyMMdd(FilenameUtils.getBaseName(y.getFileName()));
                    remoteFileInfo.setFileName(x.getFileName());
                    remoteFileInfo.setModifyTime(x.getModifyTime());
                    remoteFileInfo.setSize(x.getSize());
                    remoteFileInfo.setPath(x.getPath());
                    remoteFileInfo.setAbsolutePath(x.getAbsolutePath());
                    remoteFileInfo.setThumbAbsolutePath(x.getThumbAbsolutePath());
                    remoteFileInfo.setParent(x.getParent());
                });
                return remoteFileInfo;
                    }).collect(Collectors.toList());

            resultRecFiles.forEach(x -> {
                log.debug("{}", x);
            });
//            resultRecFiles = recFiles.stream().filter(f ->
//                    thumbFiles.contains(f.getFileName().substring(0, f.getFileName().indexOf(".")))
//            ).collect(Collectors.toList());

//            recFiles.forEach(x -> {
//                log.debug("RecFile: {}", x.getFileName());
//                String[] splitPath = x.getFileName().split("\\.");
//                String[] splits = splitPath[0].split("_", 5);
//                // 20180508101134_201804135_180201CHAM5_17_0122.MP4
//                RecFileInfo recFileInfo = null;
//                if (splits.length == 5) {
//                    for (int i = 0; i < splitPath.length - 1; i++) {
//                        recFileInfo = new RecFileInfo();
//                        recFileInfo.setStartDt(splits[0]);
//                        recFileInfo.setAplnFormId(splits[1]);
//                        recFileInfo.setAdNo(splits[2]);
//                        recFileInfo.setOtvChNo(splits[3]);
//                        recFileInfo.setChId(splits[4]);
//                        recFileInfo.setYyyyMMdd(x.getYyyyMMdd());
//                        recFileInfo.setRecFilePath(x.getAbsolutePath());
//                        recFileInfo.setRecThumbFilePath(x.getThumbAbsolutePath());
//                    }
//                    try {
//                        recInfoMapper.insertRecFileInfo(recFileInfo);
//                    } catch (Exception e) {
//                        log.error("{}", e);
//                    }
//                } else {
//                    log.info("The idx file line split count is not 5. recFilePath -> {}", x.getAbsolutePath());
//                }
//            });


//            // .IDX 의 녹화파일 목록
//            fileIOFiles = fileIO.getReadFileList(idxFiles);
//
//            fileIOFiles.forEach(x -> {
//                log.debug("{}", x);
//            });
//
//            fileIOFiles.forEach(x -> {
//                log.debug("RecFile: {}", x.getRecFilePath());
//                log.debug("RecThumbFile: {}", x.getRecThumbFilePath());
//                String[] splitPath = x.getRecFilePath().split("\\.");
//                String[] splits = splitPath[0].split("_", 5);
//                // 20180508101134_201804135_180201CHAM5_17_0122.MP4
//                RecFileInfo recFileInfo = null;
//                if (splits.length == 5) {
//                    for (int i = 0; i < splitPath.length - 1; i++) {
//                        recFileInfo = new RecFileInfo();
//                        recFileInfo.setStartDt(splits[0]);
//                        recFileInfo.setAplnFormId(splits[1]);
//                        recFileInfo.setAdNo(splits[2]);
//                        recFileInfo.setOtvChNo(splits[3]);
//                        recFileInfo.setChId(splits[4]);
//                        recFileInfo.setYyyyMMdd(x.getYyyyMMdd());
//                        recFileInfo.setRecFilePath(x.getRecFilePath());
//                        recFileInfo.setRecThumbFilePath(x.getRecThumbFilePath());
//                    }
//                    try {
//                        recInfoMapper.insertRecFileInfo(recFileInfo);
//                    } catch (Exception e) {
//                        log.error("{}", e);
//                    }
//                } else {
//                    log.info("The idx file line split count is not 5. recFilePath -> {}", x.getRecFilePath());
//                }
//        });
        } else {
            log.info("The .FIN file is not found");
        }

    executeFileInfos = resultRecFiles.stream().map(x -> {
        ExecuteFileInfo executeFileInfo = new ExecuteFileInfo();
        executeFileInfo.setSourceFile(x);
        executeFileInfo.setFinished(indexStr.contains(x.getAbsolutePath()));
        executeFileInfo.setSuccess(indexStr.contains(x.getAbsolutePath()));
        return executeFileInfo;
    }).collect(Collectors.toList());

        log.info("executeFileInfos : {}", executeFileInfos);
        log.info("1-{}. Remote file`s index checked. ", logIdx++);

        return executeFileInfos;
}

    @Override
    void setIndex(ExecuteFileInfo executeFileInfo) throws Exception {
        List<String> indexes = executeFileInfos.parallelStream().filter(x -> x.isFinished() && x.isSuccess()).map(t1 -> t1.getSourceFile().getAbsolutePath()).collect(Collectors.toList());
        indexStore.setIndex(indexes);
        log.debug("setIndex {}", indexes);
    }
}
