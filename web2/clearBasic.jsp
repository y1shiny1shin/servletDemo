<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.core.StandardPipeline" %>
<%@ page import="org.apache.catalina.Valve" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 设置响应类型
    response.setContentType("text/plain;charset=UTF-8");

    try {
        // 1. 获取当前 Web 应用的 StandardContext
        // 方法1：从 application 作用域获取
        Object contextObj = application;
        StandardContext standardContext = null;

        // 尝试通过反射获取真正的 StandardContext
        Field contextField = application.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        Object appContext = contextField.get(application);

        Field standardContextField = appContext.getClass().getDeclaredField("context");
        standardContextField.setAccessible(true);
        standardContext = (StandardContext) standardContextField.get(appContext);

        out.println("=== 原始状态 ===\n");
        out.println("Web应用: " + standardContext.getName() + "\n");
        out.println("Context路径: " + standardContext.getPath() + "\n");

        // 2. 获取 Pipeline
        StandardPipeline pipeline = (StandardPipeline) standardContext.getPipeline();

        // 3. 使用反射获取和修改 basic 字段
        Field basicField = StandardPipeline.class.getDeclaredField("basic");
        basicField.setAccessible(true);

        // 获取当前的 basic Valve
        Valve currentBasic = (Valve) basicField.get(pipeline);
        out.println("当前 basic Valve: " + currentBasic + "\n");
        if (currentBasic != null) {
            out.println("basic Valve 类型: " + currentBasic.getClass().getName() + "\n");
            out.println("basic Valve 的 next: " + currentBasic.getNext() + "\n");
        }

        // 4. 将 basic 修改为 null
        out.println("\n=== 开始修改 ===\n");
        basicField.set(pipeline, null);
        out.println("已执行: basicField.set(pipeline, null);\n");

        // 5. 验证修改结果
        Valve newBasic = (Valve) basicField.get(pipeline);
        out.println("修改后的 basic Valve: " + newBasic + "\n");

        // 6. 获取 first 字段状态
        Field firstField = StandardPipeline.class.getDeclaredField("first");
        firstField.setAccessible(true);
        Valve first = (Valve) firstField.get(pipeline);
        out.println("first 字段: " + first + "\n");

        // 7. 获取所有 Valves
        out.println("\n=== 所有 Valves ===\n");
        Field valvesField = StandardPipeline.class.getDeclaredField("valves");
        valvesField.setAccessible(true);
        Object valvesObj = valvesField.get(pipeline);

        if (valvesObj != null) {
            if (valvesObj instanceof Valve[]) {
                Valve[] valves = (Valve[]) valvesObj;
                out.println("valves 数组长度: " + valves.length + "\n");
                for (int i = 0; i < valves.length; i++) {
                    out.println("valves[" + i + "]: " + valves[i] + "\n");
                }
            } else {
                out.println("valves 类型: " + valvesObj.getClass().getName() + "\n");
            }
        } else {
            out.println("valves 为 null\n");
        }

        // 8. 使用 getValves() 方法验证
        out.println("\n=== getValves() 方法返回 ===\n");
        Valve[] allValves = pipeline.getValves();
        out.println("getValves() 长度: " + allValves.length + "\n");
        for (int i = 0; i < allValves.length; i++) {
            out.println("getValves()[" + i + "]: " + allValves[i] + "\n");
        }

        out.println("\n=== 修改完成 ===\n");

    } catch (Exception e) {
        out.println("错误: " + e.toString() + "\n");
        out.println("错误详情:\n");
        for (StackTraceElement element : e.getStackTrace()) {
            out.println("    " + element.toString() + "\n");
        }
    }
%>