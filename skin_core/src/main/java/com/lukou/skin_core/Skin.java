package com.lukou.skin_core;

import java.io.File;

public class Skin {

    /**
     * 文件校验md5值
     */
    public String md5 = "";
    /**
     * 下载地址
     */
    public String url = "";
    /**
     * 皮肤名, 唯一区别皮肤的字段, 通过修改名字升级
     */
    public String name = "";
    /**
     * 下载完成后缓存地址
     */
    public String path = "";

    public File file;

    public Skin(String md5, String name, String url) {
        this.md5 = md5;
        this.name = name;
        this.url = url;
    }

    public File getSkinFile(File dirPath) {
        if (null == file) {
            file = new File(dirPath, name);
        }
        path = file.getAbsolutePath();
        return file;
    }
}
