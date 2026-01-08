package com;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

@WebServlet("/injectThread")
public class InjectThread extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HashSet set = new HashSet();
        Thread d = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+"time");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        } ,"GC demo2" );
        d.setDaemon(true);
        d.start();

    }
}
