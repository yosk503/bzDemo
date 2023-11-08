package com.example.demo.util.fbUtil;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class MakeTrash {
    public static void sort(String[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                String b1 = a[j].replaceAll("_", "");
                String b2 = a[j + 1].replaceAll("_", "");
                //System.out.println(b1+"===="+b2);
                String c1 = "";
                String c2 = "";
                if (b1.length() > 28) {
                    c1 = b1.substring(24, 36);
                } else {
                    c1 = b1.substring(8, 20);
                }
                if (b2.length() > 28) {
                    c2 = b2.substring(24, 36);
                } else {
                    c2 = b2.substring(8, 20);
                }
                // System.out.println(c1+"===="+c2);
                double d1 = Double.parseDouble(c1.trim());
                double d2 = Double.parseDouble(c2.trim());
                if (d1 > d2) {
                    String tmp = null;
                    tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                }
            }
        }
    }

    public static void findpath(String path1) {
        String destationFile = path1;
        String newFileName = "tarxvf.sh";
        String filepath = path1;
        File file = new File(destationFile + newFileName);
        ArrayList list = new ArrayList();
        list.add("String");
        if (file.exists()) {
            file.delete();
        }
        try {
            boolean flag=file.createNewFile();
            File file2 = new File(filepath);
            File[] temp = file2.listFiles();

            FileWriter fw = new FileWriter(path1 + newFileName);

            ArrayList<String> ls = new ArrayList<String>();
            for (File afile : temp) {
                String a = afile.getName();
                if (afile.getName().endsWith(".tar")) {
                    if (afile.length() < 1) {
                        throw new IOException(afile.getName() + "?????????");
                    }
                    ls.add(a);
                }

            }
            String[] arr = new String[ls.size()];
            ls.toArray(arr);
            if (arr.length > 1) {
                sort(arr);
            }
            for (String s : arr) {
                fw.write("tar -xvf " + s + " -C ../;\r\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("???" + destationFile + "???" + e.getMessage());
        }

    }
}


