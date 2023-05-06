package com.example.demo.checkFb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author yanglonggui
 */
public class UnzipUtility {

    private static final int BUFFER_SIZE = 4096;

    public static void unzip(String zipFilePath, String destDirectory) throws Exception {
        System.out.println("=====================================开始压缩包解压============================");
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)),Charset.forName("GBK"));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // 如果条目是文件，则从InputStream输入流读取数据并将其写入文件。
                extractFile(zipIn, filePath);
            } else {
                // 如果条目是文件夹，则创建目录。
                File dir = new File(filePath);
                boolean flag=dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        System.out.println("=====================================压缩包解压成功============================");
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws Exception {
        BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}