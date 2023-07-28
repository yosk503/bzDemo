package com.example.demo.util.commonUtil;

import org.hibernate.engine.jdbc.SerializableBlobProxy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ConvertMapToObject {
    public static <T> List<T> convertMapToObject(List<Map<String, Object>> list, Class<T> clazz) {
        List<T> classList=new ArrayList<>();
        try {
            for (Map<String, Object> map : list) {
                T object = clazz.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    if (Proxy.isProxyClass(fieldValue.getClass())) {
                        InvocationHandler invocationHandler = Proxy.getInvocationHandler(fieldValue);
                        if (invocationHandler instanceof SerializableBlobProxy) {
                            SerializableBlobProxy serializableBlobProxy = (SerializableBlobProxy) invocationHandler;
                            Blob blob = serializableBlobProxy.getWrappedBlob();
                            if (blob != null) {
                                fieldValue = blob.getBytes(1, (int) blob.length());
                            }
                        }
                    }
                    String columnName = convertToCamelCase(fieldName);
                    setFieldValue(object, columnName, fieldValue);
                }
                classList.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return classList;
    }

    public static byte[] inputStreamToByteArray(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } finally {
            // 关闭输入流和输出流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static String convertFieldName(String fieldName) {
        StringBuilder columnName = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c)) {
                columnName.append("_").append(Character.toLowerCase(c));
            } else {
                columnName.append(c);
            }
        }
        return columnName.toString().toUpperCase(Locale.ROOT);
    }

    public static String convertToCamelCase(String input) {
        StringBuilder output = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    output.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    output.append(Character.toLowerCase(currentChar));
                }
            }
        }
        return output.toString();
    }
    private static void setFieldValue(Object object, String fieldName, Object fieldValue) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            // 检查字段类型，如果不匹配则进行类型转换
            if (fieldType == Long.class && fieldValue instanceof BigInteger) {
                fieldValue = ((BigInteger) fieldValue).longValue();
            } else if (fieldType == Integer.class && fieldValue instanceof BigInteger) {
                fieldValue = ((BigInteger) fieldValue).intValue();
            } else if (fieldType == Short.class && fieldValue instanceof BigInteger) {
                fieldValue = ((BigInteger) fieldValue).shortValue();
            } else if (fieldType == Byte.class && fieldValue instanceof BigInteger) {
                fieldValue = ((BigInteger) fieldValue).byteValue();
            } else if (fieldType == Double.class && fieldValue instanceof BigDecimal) {
                fieldValue = ((BigDecimal) fieldValue).doubleValue();
            } else if (fieldType == Float.class && fieldValue instanceof BigDecimal) {
                fieldValue = ((BigDecimal) fieldValue).floatValue();
            } else if (fieldType == Boolean.class && fieldValue instanceof Integer) {
                fieldValue = ((Integer) fieldValue) != 0;
            }else if (fieldType == String.class && fieldValue instanceof CharSequence) {
                fieldValue = fieldValue.toString();
            } else if (fieldType == Character.class && fieldValue instanceof CharSequence) {
                fieldValue = ((CharSequence) fieldValue).charAt(0);
            } else if (fieldType == BigDecimal.class && fieldValue instanceof Double) {
                fieldValue = BigDecimal.valueOf((Double) fieldValue);
            } else if (fieldType == BigInteger.class && fieldValue instanceof Long) {
                fieldValue = BigInteger.valueOf((Long) fieldValue);
            } else if (fieldType == Date.class && fieldValue instanceof Long) {
                fieldValue = new Date((Long) fieldValue);
            }else if (fieldType == Date.class && fieldValue instanceof String) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    fieldValue = dateFormat.parse((String) fieldValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (fieldType == LocalDate.class && fieldValue instanceof String) {
                fieldValue = LocalDate.parse((String) fieldValue);
            } else if (fieldType == LocalDateTime.class && fieldValue instanceof String) {
                fieldValue = LocalDateTime.parse((String) fieldValue);
            } else if (fieldType == LocalTime.class && fieldValue instanceof String) {
                fieldValue = LocalTime.parse((String) fieldValue);
            } else if (fieldType == UUID.class && fieldValue instanceof String) {
                fieldValue = UUID.fromString((String) fieldValue);
            }
            field.set(object, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
