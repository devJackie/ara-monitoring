package com.kthcorp.daisy.ams.fao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Comparator;

/**
 * Created by devjackie on 2018. 5. 8..
 */
@Data
@ToString
@EqualsAndHashCode
public class RemoteFileInfo implements Comparator<RemoteFileInfo>, Comparable<RemoteFileInfo> {

    private String filename = "";
    private long modifyTime = 0L;
    private long size = 0L;
    private String path = "";
    private String absolutePath = "";
    private String parent = "";

    public String getName() {
        return filename;
    }

    public String getParent() {
        return parent;
    }

    public String getPath() {
        return path;
    }

    public long lastModified() {
        return modifyTime;
    }

    public long length() {
        return size;
    }

    @Override
    public int compare(RemoteFileInfo o1, RemoteFileInfo o2) {
        return o1.absolutePath.compareTo(o2.absolutePath);
    }

    @Override
    public int compareTo(RemoteFileInfo o) {
        return this.absolutePath.compareTo(o.absolutePath);
    }

}
