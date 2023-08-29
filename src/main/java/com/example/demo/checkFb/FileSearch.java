package com.example.demo.checkFb;


import cn.hutool.core.io.CharsetDetector;
import com.example.demo.util.commonUtil.Application;
import com.example.demo.util.commonUtil.UnzipUtility;
import com.example.demo.util.excelUtil.ExcelUtils;
import com.example.demo.util.fbUtil.MakeTrash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.codehaus.plexus.archiver.tar.TarEntry;
import org.codehaus.plexus.archiver.tar.TarInputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


/**
 * @version 1.0
 * @className: FileSearch
 * @author: LongY
 * @date: 2023/3/22
 * 检查发版文件下的所有文件，检查依赖以及非法文件
 */
@Slf4j
public class FileSearch {
    public static String url;
    public static String excelSuffix;
    public static String tarType;
    public static String[] illegalFile;
    public static String[] checkFiles;
    public static String checkFile;
    public static String version;
    public static String tarPath;

    static {
        try {
            url = Application.getProperty("pmass_download_dir");
            tarType = Application.getProperty("tarType");
            excelSuffix = Application.getProperty("excelSuffix");
            String illegal = Application.getProperty("illegalFile");
            illegalFile = illegal.split(";");
            checkFile = Application.getProperty("check_File");
            version = Application.getProperty("version");
            checkFiles = checkFile.split(";");
            tarPath = Application.getProperty("pmass_tar_dir");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        startCheckFile();
    }

    /**
     * 检查依赖开始
     */

    public static void startCheckFile() throws Exception {
        log.info("=====================================发版补丁检查开始============================");
        log.info("当前文件路径：" + url);
        //判断是否已经解压
        if (new File(url).exists()) {
            checkAlreadyUnzip();
        } else {
            unZip();
            startCheck();
        }
        log.info("=====================================发版补丁检查结束============================");
    }

    /**
     * 已经解压的文件处理
     */
    public static void checkAlreadyUnzip() throws Exception {
        if (new File(url + ".zip").exists()) {
            log.info("当前压缩包已经解压，请确认是否使用已解压文件夹或者重新解压\n" + "1-使用，2-重新解压");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if ("1".equals(input)) {
                //直接使用
                startCheck();
            } else if ("2".equals(input)) {
                unZip();
                startCheck();
            } else {
                throw new Exception("请选择正确的方式！");
            }
        } else {
            //直接使用
            startCheck();
        }
    }

    /**
     * 正常校验 执行方法
     */
    public static void startCheck() throws Exception {
        String[] files = new File(url).list();
        String excelUrl = FbUtil.getExcelName(url, version, excelSuffix);
        files = Arrays.stream(files).filter((str) -> checkFile.contains(str)).collect(Collectors.toList()).toArray(new String[0]);
        String newUrl = url;
        if ("new".equals(version)) {
            newUrl = FbUtil.getPrefix(newUrl);
        }
        Map<String, String> excelMap = getExcelMap(newUrl + "\\" + excelUrl);
        //排除key为null的情况
        excelMap.entrySet().removeIf(entry -> entry.getKey() == null);
        for (int i = 0; i < files.length; i++) {
            File file = new File(url + "\\" + files[i]);
            if (!file.isFile()) {
                log.info("=====================================开始检查文件" + files[i] + "============================");
                checkFile(excelMap, files[i], tarPath);
                for (int j = 0; j < checkFiles.length; j++) {
                    if (files[i].equals(checkFiles[j]) && !"调度".equals(files[i])) {
                        MakeTrash.findpath(url + "\\" + files[i] + "\\");
                    }
                }
            }
        }
    }

    /**
     * 解压zip压缩包文件
     */
    public static void unZip() throws Exception {
        //获取要解压到的地址
        String[] array = url.split("\\\\");
        String newUrl = String.join("\\\\", Arrays.copyOf(array, array.length - 1));
        //删除文件夹，重新解压
        deleteFileList(new File(url));
        UnzipUtility.unzip(url + ".zip", newUrl);
    }

    /**
     * 删除文件
     */
    public static void deleteFileList(File dir) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {// 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            if (basicFileAttributes.isRegularFile()) {// 如果文件
                long size = basicFileAttributes.size();
                boolean result = file.delete();
                if (result) {
                    log.info(file.getName() + "文件大小：" + size + "---" + "删除成功");
                }
            } else {// 如果是目录
                deleteFileList(file);// 回调自身继续删除
            }
        }
    }

    /**
     * 按照根目录文件单个循环判断
     */
    public static void checkFile(Map<String, String> excelMap, String files, String tarPath) throws Exception {
        List<Map<String, String>> fileNames = new ArrayList<>();
        //每个压缩包的文件
        FileSearch.findFileList(new File(url + "\\" + files), fileNames, tarPath);
        fileNames.forEach(map -> {
            map.forEach((key, value) -> log.debug(value));
            log.debug(""); // 输出空行
        });
        //压缩包中的路径
        List<String> fileList = new ArrayList<>();
        //全路径
        List<String> fileListPath = new ArrayList<>();
        for (int i = 0; i < fileNames.size(); i++) {
            Map<String, String> map = fileNames.get(i);
            for (String path : map.keySet()) {
                fileListPath.add(path);
                fileList.add(map.get(path));
            }
        }


        Map<String, Long> countMap = fileList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<String> compareList = countMap.keySet().stream().filter(key -> countMap.get(key) > 1).collect(Collectors.toList());
        for (int i = 0; i < compareList.size(); i++) {
            for (int j = 0; j < fileListPath.size(); j++) {
                String path = fileListPath.get(j).split(tarType)[1];
                if (path.substring(1).equals(compareList.get(i))) {
                    String name = getTarName(fileListPath.get(j));
                    name = name.substring(0, name.indexOf("_"));
                    log.info("冲突文件：" + compareList.get(i) + "，所在文件的位置：" + fileListPath.get(j) + "，涉及到的补丁编号：" + name + ",登记人为：" + excelMap.get(name));
                }
            }
        }
        String illegal = Application.getProperty("illegalFile");
        String[] illegalFile = illegal.split(";");
        for (int i = 0; i < illegalFile.length; i++) {
            for (int j = 0; j < fileListPath.size(); j++) {
                if (fileListPath.get(j).contains(illegalFile[i])) {
                    log.info("非法文件的路径为：" + fileListPath.get(j));
                }
            }
        }
    }

    /**
     * 遍历整个文件夹，得到返回值
     */
    public static List<Map<String, String>> findFileList(File dir, List<Map<String, String>> fileList, String path) throws Exception {
        // 读取目录下的所有目录文件信息
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isFile()) {
                if (file.getName().endsWith(".tar")) {
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    log.debug("投产文件名：" + file.getName() + "    文件大小：" + basicFileAttributes.size() / 1024 + "KB");
                    if (basicFileAttributes.size() / 1024 == 0) {
                        throw new Exception("当前文件:" + file.getName() + "---大小为0KB,请仔细检查!");
                    }
                    fileList.add(getZipFilePath(dir + "\\" + file.getName(), path));
                }
            } else {
                // 回调自身继续查询
                findFileList(file, fileList, path);
            }
        }
        return fileList;
    }

    /**
     * 获取压缩包文件地址
     */
    public static Map<String, String> getZipFilePath(String tarFilePath, String outputFolderPath) throws IOException {
        Map<String, String> fileMap = new HashMap<>();
        try (FileInputStream fileInputStream = new FileInputStream(tarFilePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             TarArchiveInputStream tarInputStream = new TarArchiveInputStream(bufferedInputStream)) {
            TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    Path outputPath = Paths.get(outputFolderPath, entryName);
                    Files.createDirectories(outputPath.getParent());
                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputPath.toFile()))) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = tarInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    fileMap.put(tarFilePath + "\\" + entryName, entryName);
                }
            }
        } catch (IOException e) {
            throw new IOException("解压tar文件出错，请检查文件是否正确！" + tarFilePath, e);
        }
        deleteFiles(outputFolderPath);
        return fileMap;
    }

    public static void deleteFiles(String directory) throws IOException {
        Path directoryPath = Paths.get(directory);
        if (!Files.exists(directoryPath)) {
          return;
        }
        Files.walk(directoryPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * 判断压缩包内的目录是否为文件
     */
    public static Boolean checkIsFile(String tarPath) {
        String[] path = tarPath.split("\\\\");
        if (path[path.length - 1].contains(".")) {
            return true;
        }
        return false;
    }

    /**
     * 遍历excel,获取map
     */
    public static Map<String, String> getExcelMap(String url) throws Exception {
        String fileName = url;
        FileInputStream inputStream = new FileInputStream(fileName);
        List<List<Object>> list = ExcelUtils.getListByExcel(inputStream, fileName);
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < list.size(); i++) {
            Object obj = list.get(i);
            if (obj != null) {
                ArrayList arrayList = (ArrayList) list.get(i);
                if (arrayList.size() == list.get(1).size()) {
                    map.put(arrayList.get(2).toString(), arrayList.get(9).toString());
                }
            }

        }
        return map;
    }


    /**
     * 获取tar包的名称
     */
    public static String getTarName(String sameUrl) {
        String tarName = "";
        String[] arr = sameUrl.split("\\\\");
        for (String name : arr) {
            if (name.contains("tar")) {
                tarName = name;
            }
        }
        return tarName;
    }
}
