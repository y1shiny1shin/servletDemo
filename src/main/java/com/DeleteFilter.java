package com;

import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Vector;

@WebServlet("/check")
public class DeleteFilter extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("123");
        try {
            String name = "evilFilter";
            String tomcatVersion = "org.apache.tomcat.util.descriptor.web.";
            ServletContext servletContext = req.getServletContext();

            java.lang.reflect.Field contextField=servletContext.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            org.apache.catalina.core.ApplicationContext applicationContext = (org.apache.catalina.core.ApplicationContext) contextField.get(servletContext);
            //获取ApplicationContext中的StandardContext
            contextField=applicationContext.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            org.apache.catalina.core.StandardContext standardContext= (org.apache.catalina.core.StandardContext) contextField.get(applicationContext);

            Method findFilterMapMethod = standardContext.getClass().getDeclaredMethod("findFilterMaps");
            findFilterMapMethod.setAccessible(true);
            Object[] filterMaps = (Object[]) findFilterMapMethod.invoke(standardContext);
            Class c = Class.forName(tomcatVersion+"FilterMap");

            Object filterMap = null;
            for (int i = 0; i < filterMaps.length; i++){
                Object o = filterMaps[i];
                Method getFilterNameMethod = c.getDeclaredMethod("getFilterName");
                getFilterNameMethod.setAccessible(true);
                if (getFilterNameMethod.invoke(o) == name){
                    filterMap=o;
                }
            }
            Method removeFilterMapMethod = standardContext.getClass().getMethod("removeFilterMap",c);
            removeFilterMapMethod.invoke(standardContext,c.cast(filterMap));

            java.lang.reflect.Method findFilterDefMethod = standardContext.getClass().getMethod("findFilterDef",String.class);
            Object filterDef = findFilterDefMethod.invoke(standardContext,name);
            Class d = Class.forName(tomcatVersion + "FilterDef");

            Method removeFilterDefMethod = standardContext.getClass().getMethod("removeFilterDef" ,d);
            removeFilterDefMethod.setAccessible(true);
            removeFilterDefMethod.invoke(standardContext,filterDef);



        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
