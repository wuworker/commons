package com.wxl.commons.util.file;


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

/**
 * Created by wuxingle on 2018/05/28
 * 文件压缩解压缩(zip,gzip)
 * gzip压缩，调用close方法还会写入byte,所以如果需要byte,应该调用close后获取，否则会丢失数据
 */
public class CompressUtils {

    //----------------------------zip------------------------------------

    /**
     * 多文件压缩
     */
    public static byte[] zipToByte(File... files) throws IOException {
        if (files.length == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        zipAndClose(out, files);
        return out.toByteArray();
    }

    public static void zip(File zipFile, File... files) throws IOException {
        zipAndClose(new FileOutputStream(zipFile), files);
    }

    public static void zipAndClose(OutputStream out, File... files) throws IOException {
        try (ZipOutputStream zipOut = toZipOutputStream(out)) {
            zip(zipOut, files);
        }
    }

    public static void zip(OutputStream out, File... files) throws IOException {
        ZipOutputStream zipOut = toZipOutputStream(out);
        for (File file : files) {
            zipFile(zipOut, file, "");
        }
    }

    /**
     * 文件递归压缩
     */
    private static void zipFile(ZipOutputStream zipOut, File source, String base) throws IOException {
        if (source.isDirectory()) {
            File[] files = source.listFiles();
            if (files == null) {
                throw new IllegalStateException("file path is isInvalid:" + source);
            }
            zipOut.putNextEntry(new ZipEntry(base + source.getName() + "/"));
            for (File file : files) {
                zipFile(zipOut, file, base + source.getName() + "/");
            }
        } else {
            zipOut.putNextEntry(new ZipEntry(base + source.getName()));
            try (FileInputStream in = new FileInputStream(source)) {
                in.transferTo(zipOut);
            }
        }
    }


    /**
     * 从URL下载进行压缩
     */
    public static byte[] zipToByte(URL... urls) throws IOException {
        if (urls.length == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        zipAndClose(out, urls);
        return out.toByteArray();
    }

    public static void zip(File zipFile, URL... urls) throws IOException {
        zipAndClose(new FileOutputStream(zipFile), urls);
    }

    public static void zipAndClose(OutputStream out, URL... urls) throws IOException {
        try (ZipOutputStream zipOut = toZipOutputStream(out)) {
            zip(zipOut, urls);
        }
    }

    public static void zip(OutputStream out, URL... urls) throws IOException {
        ZipOutputStream zipOut = toZipOutputStream(out);
        for (URL url : urls) {
            String file = url.getPath();
            int index = file.lastIndexOf("/");
            String name = index != -1 && index < file.length() - 1 ? file.substring(index + 1) : file;
            zipOut.putNextEntry(new ZipEntry(name));

            try (InputStream in = url.openStream()) {
                in.transferTo(out);
            }
        }
    }

    /**
     * 解压成文件
     *
     * @param outPath 输出路径
     * @param replace 如果文件存在是否替换
     */
    public static void unzip(File zipFile, String outPath, boolean replace) throws IOException {
        unzipAndClose(new FileInputStream(zipFile), outPath, replace);
    }

    public static void unzip(URL url, String outPath, boolean replace) throws IOException {
        unzipAndClose(url.openStream(), outPath, replace);
    }

    public static void unzipAndClose(InputStream in, String outPath, boolean replace) throws IOException {
        try (ZipInputStream zipIn = toZipInputStream(in)) {
            unzip(zipIn, outPath, replace);
        }
    }

    public static void unzip(InputStream in, String outPath, boolean replace) throws IOException {
        ZipInputStream zipIn = toZipInputStream(in);
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
            Path path = Paths.get(outPath, entry.getName());
            if (entry.isDirectory()) {
                if (Files.notExists(path)) {
                    Files.createDirectories(path);
                }
            } else {
                if (replace) {
                    Files.copy(zipIn, path, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(zipIn, path);
                }
            }
        }
    }

    /**
     * 解压出文件内容保存为byte
     */
    public static List<byte[]> unzip(File zipFile) throws IOException {
        return unzipAndClose(new FileInputStream(zipFile));
    }

    public static List<byte[]> unzip(URL url) throws IOException {
        return unzipAndClose(url.openStream());
    }

    public static List<byte[]> unzipAndClose(InputStream in) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(in)) {
            return unzip(zipIn);
        }
    }

    public static List<byte[]> unzip(InputStream in) throws IOException {
        ZipInputStream zipIn = toZipInputStream(in);
        List<byte[]> list = new ArrayList<>();
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            try (byteOut) {
                zipIn.transferTo(byteOut);
            }
            list.add(byteOut.toByteArray());
        }
        return list;
    }

    /**
     * 转为zip输出流
     */
    private static ZipOutputStream toZipOutputStream(OutputStream out) {
        if (out instanceof ZipOutputStream) {
            return (ZipOutputStream) out;
        }
        return new ZipOutputStream(out);
    }

    /**
     * 转为zip输入流
     */
    private static ZipInputStream toZipInputStream(InputStream in) {
        if (in instanceof ZipInputStream) {
            return (ZipInputStream) in;
        }
        return new ZipInputStream(in);
    }

    //----------------------------gzip------------------------------------

    /**
     * 压缩
     */
    public static byte[] gzip(byte[] bytes) throws IOException {
        return gzipAndClose(new ByteArrayInputStream(bytes));
    }

    public static byte[] gzip(File file) throws IOException {
        return gzipAndClose(new FileInputStream(file));
    }

    public static byte[] gzip(URL url) throws IOException {
        return gzipAndClose(url.openStream());
    }

    public static byte[] gzipAndClose(InputStream in) throws IOException {
        try {
            return gzip(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static byte[] gzip(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOut = toGZIPOutputStream(out)) {
            in.transferTo(gzipOut);
        }
        // 等gzipOut close后输出byte
        return out.toByteArray();
    }

    /**
     * 解压
     */
    public static byte[] ungzip(byte[] bytes) throws IOException {
        return ungzipAndClose(new ByteArrayInputStream(bytes));
    }

    public static byte[] ungzip(File file) throws IOException {
        return ungzipAndClose(new FileInputStream(file));
    }

    public static byte[] ungzip(URL url) throws IOException {
        return ungzipAndClose(url.openStream());
    }

    public static byte[] ungzipAndClose(InputStream in) throws IOException {
        try {
            return ungzip(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static byte[] ungzip(InputStream in) throws IOException {
        GZIPInputStream gzipIn = toGZIPInputStream(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (out) {
            gzipIn.transferTo(out);
        }
        return out.toByteArray();
    }

    /**
     * 转为gzip输出流
     */
    private static GZIPOutputStream toGZIPOutputStream(OutputStream out) throws IOException {
        if (out instanceof GZIPOutputStream) {
            return (GZIPOutputStream) out;
        }
        return new GZIPOutputStream(out);
    }


    /**
     * 转为gzip输入流
     */
    private static GZIPInputStream toGZIPInputStream(InputStream in) throws IOException {
        if (in instanceof GZIPInputStream) {
            return (GZIPInputStream) in;
        }
        return new GZIPInputStream(in);
    }

}


