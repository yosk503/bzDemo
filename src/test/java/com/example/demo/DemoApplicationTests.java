package com.example.demo;

import com.example.demo.pmass.dao.PMassDao;
import com.example.demo.pmass.dao.PmAttachFilesDao;
import com.example.demo.pmass.entity.PmPatchReg;
import com.example.demo.util.Application;
import com.example.demo.util.ConvertMapToObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DemoApplicationTests {
    @Autowired
    private PMassDao pMassDao;
    @Autowired
    private PmAttachFilesDao pmAttachFilesDao;



    @Test
    public void test1() throws Exception {
        //获取需要下载的补丁编号
        List<String> patchCodeList = Arrays.asList(loadFileAsString().split(";"));
        //查询数据
        List<PmPatchReg> entityList = ConvertMapToObject.convertMapToObject(pMassDao.queryAllByPatchCode(patchCodeList),PmPatchReg.class);
        //判断目录有无数据
        String pMassDir= Application.getProperty("pmass_download_dir");
        String backDir= Application.getProperty("pmass_backFile_dir")+"\\"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File fileDir=new File(pMassDir+"\\");
        if(fileDir.exists()){
            File[] files = fileDir.listFiles();
            if(files != null && files.length > 0){
                moveFolderContents(new File(pMassDir),new File(backDir));
                deleteFolderContents(fileDir);
            }
        }else {
          boolean flag=fileDir.mkdirs();
        }

        assert entityList != null;
        for (PmPatchReg pmPatchReg: entityList){
            String fileName=pmPatchReg.getFileName();//文件名
            String patchDisc=pmPatchReg.getPatchDisc();//补丁描述
            String patchCode=pmPatchReg.getPatchCode();//补丁编号
            String patchDever=pmPatchReg.getPatchDever();//登记人
            //根据补丁描述获取补丁存放路径
            String filePath=getPath(patchCode,patchDisc,fileName,pMassDir);
            String absoluteName=patchCode+"_"+patchDever+"_"+fileName;
            //生成文件
            try {
                File attachFile=new File(filePath+"\\"+absoluteName);
                if(!attachFile.exists()){
                    boolean flag=attachFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(attachFile);
                fos.write(pmPatchReg.getFileBody());
                fos.close();
                System.out.println("文件已成功生成！");
            } catch (Exception e) {
                System.out.println("生成文件时出现错误：" + e.getMessage());
            }
        }

    }

    private static void deleteFolderContents(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子文件夹，则递归地删除子文件夹中的内容
                    deleteFolderContents(file);
                } else {
                    // 如果是文件，则删除文件
                    file.delete();
                }
            }
        }
        // 删除空文件夹
        folder.delete();
    }



    public String getPath(String patchCode,String patchDisc,String fileName,String pMassDir) throws Exception{
        //获取根目录

        String path;
        if((patchDisc.contains("【柜面】")||patchDisc.contains("[柜面]"))&&fileName.endsWith("tar")){
            path="gm";
        }else  if((patchDisc.contains("【网银】")||patchDisc.contains("[网银]"))&&fileName.endsWith("tar")){
            path="wy";
        }else  if(patchDisc.contains("【报表】")||patchDisc.contains("[报表]")){
            path="报表";
        }else  if(patchDisc.contains("【调度】")||patchDisc.contains("[调度]")){
            path="调度";
        }else  if(fileName.endsWith("txt")){
            path="sql";
        }else {
            throw new Exception("补丁描述错误:"+patchCode);
        }
        return pMassDir+"\\"+path;
    }

    public String loadFileAsString() throws IOException {
        ClassPathResource resource = new ClassPathResource("pmass.txt");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(fileData, StandardCharsets.UTF_8);
    }

    private static void moveFolderContents(File sourceFolder, File destinationFolder) throws IOException {
        // 获取源文件夹中的所有文件和子文件夹
        File[] files = sourceFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                File newFile = new File(destinationFolder.getAbsolutePath() + File.separator + file.getName());
                if(!newFile.exists()){
                   boolean flag= newFile.mkdirs();
                }
                if (file.isDirectory()) {
                    // 如果是子文件夹，则递归地移动子文件夹中的内容
                    moveFolderContents(file, newFile);
                } else {
                    // 如果是文件，则移动文件到目标文件夹
                    Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

}
