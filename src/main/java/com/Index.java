package com;

import javassist.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/index")
public class Index extends HttpServlet {
    public String namexx = "hhh";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        ClassPool classPool = ClassPool.getDefault();
//        String name = req.getParameter("msg");
//        resp.getWriter().println(namexx + name+System.currentTimeMillis());
    }
}
