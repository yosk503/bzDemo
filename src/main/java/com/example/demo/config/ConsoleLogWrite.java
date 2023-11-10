package com.example.demo.config;


import com.example.demo.util.commonUtil.Application;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 控制台日志写入文件
 *
 * @author Mr peng
 */
@Slf4j
@Component
public class ConsoleLogWrite extends OutputStream {


    private String consoleLogWriteWindowsUrl;
    //linux输出文件路径

    private String consoleLogWriteLinuxUrl;

    private OutputStream oldOutputStream, newOutputStream;

    public ConsoleLogWrite() {

    }

    public ConsoleLogWrite(OutputStream oldOutputStream, OutputStream newOutputStream) throws UnsupportedEncodingException {
        this.oldOutputStream = oldOutputStream;
        this.newOutputStream = newOutputStream;
    }


    public static byte[] intToBytes(int value) {
        byte[] src = new byte[1];
        src[0] = (byte) (value & 0xFF);
        return src;
    }


    //重写输出流的方式，改为两种，一种控制台输出，一种写入指定文件
    @Override
    public void write(int b) throws IOException {
        byte[] bytes = intToBytes(b);
        oldOutputStream.write(b);
        String mm = new String(bytes, StandardCharsets.UTF_8);
        if (!"\u001B".equals(mm) && !"[".equals(mm)) {
            newOutputStream.write(b);
        }

    }

	//当前bean初始化前调用
	@PostConstruct
	public void writeLogToFile() throws Exception {
		File tmplLogFile = new File(getUploadPath(Application.getProperty("consoleLogWrite.windowsUrl"), Application.getProperty("consoleLogWrite.windowsUrl")+"."+new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
		//启一个定时线程延迟15分钟后每过30分钟检查文件大小是否超过1G，如果超过则删除重新创建
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		executorService.scheduleWithFixedDelay(() -> {
			try {
				double KB = 1024 * 1024*1024;
				double MB = KB * 1024;
				if(tmplLogFile.length() > MB * 100){
					getNewFile(tmplLogFile.getPath());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}, 60, 60, TimeUnit.MINUTES);
        getNewFile(tmplLogFile.getPath());
		//设置输出模式
        PrintStream oldOutputStream = System.out;
        OutputStream newOutputStream = new FileOutputStream(tmplLogFile);
        ConsoleLogWrite multiOutputStream = new ConsoleLogWrite(oldOutputStream, new PrintStream(newOutputStream));
        System.setOut(new PrintStream(multiOutputStream));
        System.setErr(new PrintStream(multiOutputStream));
    }

    /**
     * 根据当前系统返回对应的路径
     *
     * @param linuxPath
     * @param windowsPath
     * @return
     */
    public static String getUploadPath(String linuxPath, String windowsPath) {
        if (System.getProperty("os.name").toLowerCase().indexOf("linux") > 0) {
            return linuxPath;
        }
        return windowsPath;
    }

    /**
     * 根据日志规则生成文件
     */
    public static String getNewFile(String oldFileName) throws Exception {
        File newFile = null;
        try {
            File tmplLogFile = new File(oldFileName);
            if (!tmplLogFile.exists()) {
                try {
                    boolean create = tmplLogFile.createNewFile();
                    log.info("控制台日志文件创建" + create + "," + tmplLogFile.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //文件存在就重命名 然后创建新的
                String[] attr = tmplLogFile.getPath().split("-");
                String newName;
                int number = 1;
                if (attr.length >= 4) {
                    number = Integer.parseInt(attr[attr.length - 1]);
                }
                for (; number < 10; number++) {
                    newName = tmplLogFile.getPath() + "." + number;
                    newFile = new File(newName);
                    if (!newFile.exists()) {
                        boolean flag = newFile.createNewFile();
                        if (flag) {
                            FileUtils.copyFile(tmplLogFile, newFile);
                            break;
                        }
                    }
                }
            }
            if (newFile == null) {
                return oldFileName;
            }
            return newFile.getPath();
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }
}
