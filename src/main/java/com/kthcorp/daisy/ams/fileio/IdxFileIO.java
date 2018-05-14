package com.kthcorp.daisy.ams.fileio;

import com.kthcorp.daisy.ams.fao.RemoteFileInfo;
import com.kthcorp.daisy.ams.properties.AmsMetaProperties;
import com.kthcorp.daisy.ams.util.CollectorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Slf4j
public class IdxFileIO extends BaseFileIO {

    IdxFileIO(Map<String, Object> config, AmsMetaProperties amsMetaProperties) {
        super(config, amsMetaProperties);
    }

    @Override
    public List<FileIOInfo> getReadFileList(List<RemoteFileInfo> idxFiles) throws Exception {
        log.debug("getReadLineFile");
        log.debug("config : {}", config);

        String line = null;

        List<String> paths = createPath(path, null, pathAttributes, 0);

        log.debug("paths : {}", paths);

        List<FileIOInfo> idxFileList = null;

        for (String globPath : paths) {
            for (RemoteFileInfo idxFile : idxFiles) {
                log.debug("read file path : {}", globPath);
                // yyyyMMdd 설정
                idxFile.setYyyyMMdd(idxFile.getFileName().split("\\.")[0]);
                log.debug("yyyyMMdd : {}", idxFile.getYyyyMMdd());
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(idxFile.getAbsolutePath()), textEncoding));
                idxFileList = new ArrayList<>();
                FileIOInfo fileIOInfo = null;
                try {
                    while ((line = in.readLine()) != null) {
                        fileIOInfo = new FileIOInfo();
                        fileIOInfo.setYyyyMMdd(idxFile.getYyyyMMdd());
                        fileIOInfo.setFileName(idxFile.getFileName());
                        fileIOInfo.setAbsolutePath(idxFile.getAbsolutePath());
                        if (line.toUpperCase().endsWith(".MP4")) {
                            fileIOInfo.setRecFilePath(line);
                        }
                        if (line.toUpperCase().endsWith(".JPG")) {
                            fileIOInfo.setRecThumbFilePath(line);
                        }
                        idxFileList.add(fileIOInfo);
                    }
                } finally {
                    CollectorUtil.quietlyClose(in);
                }
            }
        }
        return idxFileList;
    }

    private List<String> createPath(String rootPath, Map<String, String> header, List<Map<String, Object>> subAttrs, int depthIdx) throws Exception{
        if(header == null) {
            header = new HashMap();
        }

        Set<String> paths = new HashSet<>();
        if (subAttrs != null && subAttrs.size() > depthIdx) {
            Map<String, Object> attr = (Map) subAttrs.get(depthIdx);
            String type = (String) attr.get(amsMetaProperties.getAmsMeta().get("common").get("type"));

            if(type.startsWith((String) amsMetaProperties.getAmsMeta().get("common").get("date"))) {

                log.debug("attr --> {}", attr);
                SimpleDateFormat sdf = new SimpleDateFormat((String) attr.get(amsMetaProperties.getAmsMeta().get("common").get("date-pattern")));
                String scanRange = (String) attr.get(amsMetaProperties.getAmsMeta().get("common").get("scan-range"));
                if (scanRange.contains("h")) {
                    throw new IllegalArgumentException("'h' char not support");
                }

                int dateRange = 0;

                if (scanRange.contains("d")) {
                    dateRange = Integer.parseInt(scanRange.substring(0, scanRange.indexOf("d")));
                }

                for (int i = 0; i >= dateRange; i--) {
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.add(Calendar.DATE, i);
                    header.put((String) amsMetaProperties.getAmsMeta().get("common").get("date"), sdf.format(calendar.getTime()));
                    paths.addAll(createPath(rootPath, header, subAttrs, depthIdx + 1));
                }
            } else if(type.startsWith("none")){
                paths.add(rootPath);
            }
        } else {
            StrSubstitutor sub = new StrSubstitutor(header);
            paths.add(sub.replace(rootPath));
        }
        List<String> result = new ArrayList<>(paths);
        Collections.sort(result);
        return result;
    }
}
