package com.example.demo.util.bzUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class IdCardCode {
    private static final int[] WEIGHTS = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
    private static final String[] CHECK_CODES = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };

    public static String generateID() {
        // 生成6位随机地区码（前6位）
        String regionCode = generateRegionCode();

        // 生成8位随机出生日期码（第7位到14位）
        String birthdayCode = generateBirthdayCode();

        // 生成3位随机顺序码（第15位到17位）
        String sequenceCode = generateSequenceCode();

        // 计算校验码（最后一位）
        String checkCode = calculateCheckCode(regionCode + birthdayCode + sequenceCode);

        // 组合生成身份证号码
        return regionCode + birthdayCode + sequenceCode + checkCode;
    }

    private static String generateRegionCode() {
        Random random = new Random();
        int regionCode = random.nextInt(999999) % 900000 + 100000; // 生成100000到999999之间的随机数
        return String.valueOf(regionCode);
    }

    private static String generateBirthdayCode() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getRandomNumberInRange(1950, 2005)); // 生成1950到2005之间的随机年份
        calendar.set(Calendar.MONTH, getRandomNumberInRange(0, 11)); // 生成0到11之间的随机月份
        calendar.set(Calendar.DAY_OF_MONTH, getRandomNumberInRange(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))); // 生成1到当月最大天数之间的随机日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(calendar.getTime());
    }

    private static String generateSequenceCode() {
        DecimalFormat decimalFormat = new DecimalFormat("000");
        return decimalFormat.format(getRandomNumberInRange(0, 999));
    }

    private static String calculateCheckCode(String idNumber) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Integer.parseInt(String.valueOf(idNumber.charAt(i))) * WEIGHTS[i];
        }
        int remainder = sum % 11;
        return CHECK_CODES[remainder];
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static void main(String[] args) {
        String idNumber = generateID();
        System.out.println("生成的身份证号码：" + idNumber);
    }
}