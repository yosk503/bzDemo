package com.example.demo.checkFb;

import com.example.demo.util.Application;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.sql.Types.BOOLEAN;
import static java.sql.Types.NUMERIC;
import static java.util.stream.Collectors.toList;
import static org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType.FORMULA;
import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;


/**
 * 两个文件夹下的所有文件进行比较，判断差异
 */
public class CheckCodeDiff {

    /**
     * 需要过滤的文件格式后缀
     */
    public static String[] checkCodeFilter;

    static {
        try{
            checkCodeFilter= Application.getProperty("check_Code_filter").split(";");
            System.out.println("本次需要过滤的文件格式有"+ Arrays.toString(checkCodeFilter));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 两个文件夹下的所有文件进行比较，判断差异
     */
    public static void main(String[] args) throws Exception {
        String localUrl = "D:\\logs\\checkTest\\local";
        String checkUrl = "D:\\logs\\checkTest\\check";
        getFile(localUrl, checkUrl);
    }

    /**
     * 获取两个文件夹的所有文件，然后进行比较
     * 筛选出交集以及差集
     */
    public static void getFile(String urlOne, String urlTwo) throws Exception {
        ArrayList<String> fileListOne = new ArrayList<>();
        ArrayList<String> fileListTwo = new ArrayList<>();
        //获取本地的所有文件路径
        findFileList(new File(urlOne), fileListOne);
        //获取环境上的所有文件路径
        findFileList(new File(urlTwo), fileListTwo);
        //先以本地为主，找出环境与本地不同的
        String[] splitOne=urlOne.split("\\\\");
        String[] splitTwo=urlTwo.split("\\\\");
        List<String> one = new ArrayList<>(getDifferent(fileListOne, fileListTwo,splitOne[splitOne.length-1],splitTwo[splitTwo.length-1]));
        //再以环境为主，找出本地与环境不同的
        List<String> two = new ArrayList<>(getDifferent(fileListTwo, fileListOne,splitTwo[splitTwo.length-1],splitOne[splitOne.length-1]));
        //获取两个list的交集(本地和柜面不相同的文件，由于生产以及本地编译环境不同，导致编译后的字节码文件不相同，所以只能判断两个文件夹相差的文件)
        List<String> intersection = one.stream().filter(two::contains).collect(toList());
        System.out.println("本地和柜面不相同的文件");
        intersection.parallelStream().forEach(System.out :: println);
        //差集(list1 - list2)（本地为主 测试环境没有的文件）
        List<String> reduce1 = one.stream().filter(item -> !two.contains(item)).collect(toList());
        System.out.println("测试环境没有的文件");
        reduce1.parallelStream().forEach(System.out :: println);
        // 差集 (list2 - list1)（测试为主 本地没有的文件）
        List<String> reduce2 = two.stream().filter(item -> !one.contains(item)).collect(toList());
        System.out.println("本地环境没有的文件");
        reduce2.parallelStream().forEach(System.out :: println);
    }

    /**
     * 对两个文件夹的所有文件进行对比，比较所有文件
     * 以fileListOne为主，根据分隔符进行切割，比较结果返回list
     */
    public static ArrayList<String> getDifferent(ArrayList<String> fileListOne,ArrayList<String> fileListTwo,String splitOne,String splitTwo) throws Exception {
        ArrayList<String> newList=new ArrayList<>();
        for (String s : fileListOne) {
            boolean flag = false;
            String fileOne = s.split(splitOne+"\\\\")[1];
            for (String value : fileListTwo) {
                String fileTwo = value.split(splitTwo+"\\\\")[1];
                //文件名相同 则比较文件
                if (fileOne.equals(fileTwo)) {
                    if (!compareFiles(s, value)) {
                        newList.add(fileOne);
                    }
                    flag = true;
                }
            }
            if (!flag) {
                newList.add(fileOne);
            }
        }
        return newList;
    }

    /**
     * 遍历目录之下所有符合条件的文件列表
     */
    public static void findFileList(File dir, ArrayList<String> fileList) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        // 读取目录下的所有目录文件信息
        String[] files = dir.list();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            File file = new File(dir, files[i]);
            if (file.isFile()) {
                boolean addFlag=true;
                //需要过滤掉不需要进行比对的文件
                for (String s : checkCodeFilter) {
                    if (file.getAbsolutePath().endsWith(s)) {
                        addFlag = false;
                    }
                }
                if(addFlag){
                    fileList.add(file.getAbsolutePath());
                }
            } else {
                // 回调自身继续查询
                findFileList(file, fileList);
            }
        }
    }

    /**
     * 根据文件格式选择不同的比较方法
     */
    public static boolean compareFiles(String fileOne, String fileTwo) throws Exception {
        boolean flag;
        if(fileOne.endsWith("xlsx")){
            flag=comparatorExcel(fileOne,fileTwo);
        }else {
            flag=comparatorReader(fileOne,fileTwo);
        }
       return flag;
    }

    /**
     * 字符流比较
     */
    public static boolean comparatorReader(String fileOne, String fileTwo) throws IOException {
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        boolean areEqual = true;
        try {
            br1 = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileOne)), StandardCharsets.UTF_8));
            br2 = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileTwo)), StandardCharsets.UTF_8));
            String line1 = br1.readLine();
            String line2 = br2.readLine();
            while (line1 != null || line2 != null) {
                assert line1 != null;
                String s = line1.trim();
                String replaceAll = line2.trim();
                if (!s.equals(replaceAll)) {
                    areEqual = false;
                    break;
                }
                line1 = br1.readLine();
                line2 = br2.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            areEqual=false;
        }finally {
            if (br1 != null){
                br1.close();
            }
            if (br2 !=null){
                br2.close();
            }
        }
        return areEqual;
    }

    /**
     * excel比较  主要比较的是表格中的字符
     * 不对格式进行比较
     */
    public static boolean comparatorExcel(String fileOne, String fileTwo) throws IOException{
        FileInputStream fis1=null;
        FileInputStream fis2=null;
        boolean areEqual = true;
        try{
            fis1 = new FileInputStream(fileOne);
            fis2 = new FileInputStream(fileTwo);
            Workbook wb1 = new XSSFWorkbook(fis1);
            Workbook wb2 = new XSSFWorkbook(fis2);
            for (int i = 0; i < wb1.getNumberOfSheets(); i++) {
                Sheet sheet1 = wb1.getSheetAt(i);
                Sheet sheet2 = wb2.getSheetAt(i);
                if (!compareSheets(sheet1, sheet2)) {
                    areEqual = false;
                    break;
                }
            }

        }catch (Exception e){
           e.printStackTrace();
        }finally {
            if(fis1!=null){
                fis1.close();
            }
            if(fis2!=null){
                fis2.close();
            }
        }
        return  areEqual;
    }

    /**
     * 比较sheet
     */
    static boolean compareSheets(Sheet sheet1, Sheet sheet2) {
        if (sheet1.getLastRowNum() != sheet2.getLastRowNum()) {
            return false;
        }
        for (int j = 0; j <= sheet1.getLastRowNum(); j++) {
            Row row1 = sheet1.getRow(j);
            Row row2 = sheet2.getRow(j);
            if (!compareRows(row1, row2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较行
     */
    private static boolean compareRows(Row row1, Row row2) {
        if (row1.getLastCellNum() != row2.getLastCellNum()) {
            return false;
        }
        for (int k = 0; k < row1.getLastCellNum(); k++) {
            Cell cell1 = row1.getCell(k);
            Cell cell2 = row2.getCell(k);
            if (!compareCells(cell1, cell2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较每一个表格
     */
    private static boolean compareCells(Cell cell1, Cell cell2) {
        if (cell1 == null && cell2 == null) {
            return true;
        } else if (cell1 == null || cell2 == null) {
            return false;
        }
        if (cell1.getCellType() != cell2.getCellType()) {
            return false;
        }
        switch (cell1.getCellType()) {
            case STRING:
                return cell1.getStringCellValue().equals(cell2.getStringCellValue());
            case NUMERIC:
                return cell1.getNumericCellValue() == cell2.getNumericCellValue();
            case BOOLEAN:
                return cell1.getBooleanCellValue() == cell2.getBooleanCellValue();
            case FORMULA:
                return cell1.getCellFormula().equals(cell2.getCellFormula());
            default:
                return true;
        }
    }

}
