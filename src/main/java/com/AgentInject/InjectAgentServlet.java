package com.AgentInject;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/injectAgentServlet")
public class InjectAgentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for(VirtualMachineDescriptor vmd : list){
            System.out.println(vmd.displayName());
            //遍历每一个正在运行的JVM，如果JVM名称为AgentSpringApplication则连接该JVM并加载特定Agent
            if(vmd.displayName().contains("org.apache.catalina.startup.Bootstrap")){
                try {
                    //连接指定JVM
                    VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                    //加载Agent
                    virtualMachine.loadAgent("/Users/y1shin/Downloads/servletDemo/target/servletDemo-1.0-SNAPSHOT.jar");

                    virtualMachine.detach();
                }catch (Exception e){
                    System.out.println(e);
                }
                //断开JVM连接

            }

        }
        resp.getWriter().write("Inject Success");
    }
}
