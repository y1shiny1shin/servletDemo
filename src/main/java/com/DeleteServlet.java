package com;

import org.apache.catalina.Wrapper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/checkServlet")
public class DeleteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ServletContext servletContext = req.getServletContext();

            java.lang.reflect.Field contextField = servletContext.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            org.apache.catalina.core.ApplicationContext applicationContext = (org.apache.catalina.core.ApplicationContext) contextField.get(servletContext);
            //获取ApplicationContext中的StandardContext
            contextField = applicationContext.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            org.apache.catalina.core.StandardContext standardContext = (org.apache.catalina.core.StandardContext) contextField.get(applicationContext);

            String servletName = "execServlet";
            String servletPath = "/exec";
            Wrapper wrapper = (Wrapper) standardContext.findChild(servletName);
            standardContext.removeChild(wrapper);

            standardContext.removeServletMapping(servletPath);
            



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
