package com.wxl.commons.excel;

import java.io.*;

/**
 * Create by wuxingle on 2020/11/03
 * 测试
 */
public class TestHelper {

    private static final String PATH_PREFIX = "src/test/resources/";

    public static InputStream getFileInputStream(String name) throws FileNotFoundException {
        File file = new File(PATH_PREFIX + name);

        return new FileInputStream(file);
    }

    public static OutputStream getFileOutputStream(String name) throws FileNotFoundException {
        File file = new File(PATH_PREFIX + name);

        return new FileOutputStream(file);
    }

}
