package com.example.demo.util.excelUtil;



import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;


public class ExcelExport {
	public static <T> void getExcelExport(List list,String url) throws Exception {

		File file = new File(url);

		if(!file.exists()){
			boolean flag = file.mkdirs();
		}
		if(list.size()==0){
			throw new Exception("当前数据为空");
		}
		Timestamp nowTimestamp = new Timestamp(new Date().getTime());
		System.out.println("--2007---"+list.size()+"条数据导数开始时间：--\n" + nowTimestamp);
		SXSSFWorkbook workBook = new SXSSFWorkbook();
		Map<String,Object> map= (Map<String, Object>) list.get(0);
		List<String> fields = new ArrayList<>(map.keySet());
		String[] heads=fields.toArray(new String[0]);
		int sheetCount= Integer.parseInt("100000");
		int countTotal=list.size();
		try{
			if(list.size()>1000000){
				throw new Exception("当前数据量过大，不支持导出，请联系管理员检查");
			}
			for (int i = 0; i <list.size() ; i=i+sheetCount) {
				Sheet sheet = workBook.createSheet("sheet1");       //创建Excel工作表（页签）
				for(int co=0; co < heads.length; co++){
					sheet.setColumnWidth(co,5000);                       //设置列宽
				}
				InitExcelHead(workBook, sheet, heads);             //初始化抬头和样式
				setExcelValue(sheetCount,sheet,countTotal,i, heads,list);
				if(countTotal>sheetCount){
					countTotal=countTotal-sheetCount;
				}
			}
			excelExport(workBook,url);  //导出处理
		}catch (Exception e){
			throw new Exception(e.getMessage());
		}
		System.out.println("--2007---"+list.size()+"条数据导数结束时间：--\n" + new Timestamp(new Date().getTime()));
	}
	public static void getTitleStyle(SXSSFWorkbook workbook, Row title) {
		CellStyle style = workbook.createCellStyle();              // 创建样式
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);            // 字体居中
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		Font font = workbook.createFont();                         // 创建字体样式
		font.setFontName("宋体");                                   // 字体
		font.setFontHeightInPoints((short) 16);                    // 字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);              // 加粗
		style.setFont(font);                         //给样式指定字体
		title.getCell(0).setCellStyle(style);        //给标题设置样式
	}
	private static Row InitExcelHead(SXSSFWorkbook workBook, Sheet sheet, String[] head) {
		Row row = sheet.createRow(0);
		CellStyle style = getHeaderStyle(workBook);             //获取表头样式
		for(int i=0; i<head.length; i++){
			row.createCell(i).setCellValue(head [i]);            
			row.getCell(i).setCellStyle(style);                 //设置标题样式
		}
		return row;
	}
	private static <T> void setExcelValue(int sheetCount,Sheet sheet,int countTotal,int count, String[] head, List list) throws Exception {
		StringBuffer buffer = new StringBuffer();
		int rowNumber= Math.min(countTotal, sheetCount);
		for(int i=0; i<rowNumber; i++){
			//sheet.createRow(i+2) 2003excel参数里面的类型是int，所以一次只能导出65535条数据
			Row row = sheet.createRow(i+1);
			for(int j=0; j < head.length; j++){
				Map<String, Object> map = convertObjectToMap(list.get(count+i));
				buffer.append(map.get(head[j]));
				if("null".equals(buffer.toString())){
					row.createCell(j).setCellValue("");
				}else {
					row.createCell(j).setCellValue(buffer.toString());
				}
				buffer.delete(0, buffer.length());
			}
		}
		System.out.println("当前行数"+(count+rowNumber));
	}
	public static Map<String, Object> convertObjectToMap(Object object) throws IllegalAccessException {
		Map<String, Object> map = new HashMap<String,Object>();
		Class<?> clazz = object.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValue = field.get(object);
			map.put(fieldName, fieldValue);
		}
		return map;
	}
	private static void excelExport(SXSSFWorkbook workBook,String srcFile) {
		FileOutputStream fileOut = null ;
		try {
			File file = new File(srcFile);
			if(file.exists()){
				boolean flag=file.delete();
			}
			fileOut = new FileOutputStream(file);
			workBook.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static CellStyle getHeaderStyle(SXSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);      //居中
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		Font font = workbook.createFont();               // 创建字体样式
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 14);              // 字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);        // 加粗
		style.setFont(font);                                 //给样式指定字体
		return style;
	}
}