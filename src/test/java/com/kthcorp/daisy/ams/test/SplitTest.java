package com.kthcorp.daisy.ams.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Created by devjackie on 2018. 5. 13..
 */
@RunWith(SpringRunner.class)
@Slf4j
public class SplitTest {

    @Test
    public void splitTest() {

        String str = "20180428022500_17_0122&201804019-180416GSPB30_201804032-180420RDLN15_201804028-180417TMSD30.MP4";
//        String str = "20180428022500_17_0122&201804019-180416GSPB30_201804032.MP4";
        StringTokenizer tokenizer = new StringTokenizer(FilenameUtils.getBaseName(str), "&");

        while (tokenizer.hasMoreTokens()) {
            System.out.println(tokenizer.nextToken());
        }

        List<String> items = Arrays.asList(FilenameUtils.getBaseName(str).split("\\s*&\\s*"));
        log.debug("{}", items);

//        List<String> items1 = items.stream().map(x1 -> {
//            List<String> item = Arrays.asList(FilenameUtils.getBaseName(x1).split("\\s*_\\s*"));
//            return item;
//        });

        String[] fileArray = FilenameUtils.getBaseName(str).split("&");
        if (fileArray.length > 1) {
            List<String> firFileInfo = Arrays.asList(fileArray[0].split("_", 3));
            List<String> secFileInfo = Arrays.asList(fileArray[1].split("_"));

            log.debug("firFileInfo : {}", firFileInfo);
            log.debug("secFileInfo : {}", secFileInfo);

            // newArray : [[201804019, 180416GSPB30, 20180428022500, 17, 0122], [201804032, 180420RDLN15, 20180428022500, 17, 0122], [201804028, 180417TMSD30, 20180428022500, 17, 0122]]
            List<List<String>> resultRecInfos = secFileInfo.stream().map(x -> {
                List<String> list = new ArrayList<>();
                String[] adInfos = x.split("-");
                for (String adInfo : adInfos) {
                    list.add(adInfo);
                }
                firFileInfo.stream().forEach(s -> list.add(s));
                return list;
            }).collect(Collectors.toList());

            log.debug("resultRecInfos : {}", resultRecInfos);
        } else {
            log.info("The idx file line split count is not 5. recFilePath -> {}", str);
        }


    }

    @Test
    public void SplitFncTest() {
//        String path = "";
        String path = "20180508101134_201804135_180201CHAM5_17_0122.MP4";
        String[] splitPath = path.split("\\.");
//        List<String> splitArray = Arrays.asList(splitPath[0].split("_", 5));
        String[] splits = splitPath[0].split("_", 5);

        if (splits.length == 5) {
            for (int i = 0; i < splitPath.length - 1; i++) {
                log.debug(splits[0]);
                log.debug(splits[1]);
                log.debug(splits[2]);
                log.debug(splits[3]);
                log.debug(splits[4]);
            }
        } else {
            log.info("The idx file line split count is not 5. -> {}", path);
        }

    }
//    RecFileInfo recInfo;
//            for (int i = 0; i < splits.length; i++) {
//        recInfo = new RecFileInfo();
//        recInfo.setStartDt(splits[0]);
//        recInfo.setAplnFormId(splits[1]);
//        recInfo.setAdNo(splits[2]);
//        recInfo.setOtvChNo(splits[3]);
//        recInfo.setChId(splits[4]);
//    }
}
