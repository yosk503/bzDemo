package com.example.demo.util;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class EntityConverter {
    public static <T> List<T> convertList(List<Map<String, Object>> list, Class<T> targetType) throws IllegalAccessException, InstantiationException {
        List<T> entityList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            T entity = targetType.newInstance();
            Field[] fields = targetType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                String columnName = convertFieldName(fieldName);
                if (map.containsKey(columnName)) {
                    Object fieldValue = map.get(columnName);
                    //Object fieldValue = getFieldFromObject(map,fieldName);
                    System.out.println(fieldValue.getClass());
                    System.out.println(field.getType());
                    if (fieldValue != null && field.getType().isAssignableFrom(fieldValue.getClass())) {
                        field.set(entity, fieldValue);
                    }
                }
            }
            entityList.add(entity);
        }
        return entityList;
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

    private static Object getFieldFromObject(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            // Check if fieldValue is a proxy object
            if (Proxy.isProxyClass(fieldValue.getClass())) {
                fieldValue = Proxy.getInvocationHandler(fieldValue);
            }
            return fieldValue;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}