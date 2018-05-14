package com.kthcorp.daisy.ams.fileio;

import com.kthcorp.daisy.ams.fao.RemoteFileInfo;

import java.util.List;

/**
 * Created by devjackie on 2018. 5. 10..
 */
public interface FileIO {
    List<FileIOInfo> getReadFileList(List<RemoteFileInfo> idxFiles) throws Exception;
}
