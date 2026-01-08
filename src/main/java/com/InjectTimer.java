package com;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

@WebServlet("/injectTimer")
public class InjectTimer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Timer 内存马 init");
        Timer exec = new Timer();
        exec.schedule(new TimerTask() {
            public void run() {
                try{
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+"time");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } ,0 ,1000);
    }
}
