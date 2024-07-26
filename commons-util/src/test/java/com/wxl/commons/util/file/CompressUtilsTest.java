package com.wxl.commons.util.file;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Create by wuxingle on 2020/11/10
 * 文件压缩解压测试
 */
public class CompressUtilsTest {

    @Test
    public void testZip() throws IOException {
        File outFile = new File("src/test/resources/file/t.zip");
        File f1 = new File("src/test/resources/file/t1.log");
        File f2 = new File("src/test/resources/file/t2.log");
        File f3 = new File("src/test/resources/file/t3");
        CompressUtils.zip(outFile, f1, f2, f3);

        CompressUtils.unzip(outFile, "src/test/resources/file/t4/", false);
    }

    @Test
    public void testGzip() throws IOException {
        File file = new File("pom.xml");
        System.out.println(file.exists());

        byte[] bytes = CompressUtils.gzip(file);
        System.out.println(bytes.length);
        System.out.println(Base64.getEncoder().encodeToString(bytes));

        byte[] ungzip = CompressUtils.ungzip(bytes);
        System.out.println(new String(ungzip));
    }
}