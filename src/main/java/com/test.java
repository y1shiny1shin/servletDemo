package com;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class test {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {

        System.out.println(Files.readAllLines(Paths.get("/Users/y1shin/flag")).toString());
        Class<?> millisClass = TimeUnit.MILLISECONDS.getClass();
        Class<?> timeUnitClass = TimeUnit.class;

        // 检查是否是子类关系
        boolean isSubclass = timeUnitClass.isAssignableFrom(millisClass);
        boolean isSuperclass = millisClass.isAssignableFrom(timeUnitClass);

        System.out.println("TimeUnit是TimeUnit.MILLISECONDS的父类/接口吗? " + isSubclass);
        System.out.println("TimeUnit.MILLISECONDS是TimeUnit的父类/接口吗? " + isSuperclass);

        try {
            Class.forName("org.apache.tomcat.util.threads.ThreadPoolExecutor$RejectPolicy").getSuperclass();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
