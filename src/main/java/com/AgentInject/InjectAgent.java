//package com.AgentInject;
//
//import com.sun.tools.attach.*;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * 需要导入库，$JAVA_HOME/lib/tools.jar
// */
//
//import java.io.IOException;
//import java.util.List;
//
//@Controller
//public class InjectAgent {
//    @RequestMapping("/InjectAgent")
//    @ResponseBody
//    public String InjectAgent() throws AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException {
//        List<VirtualMachineDescriptor> list = VirtualMachine.list();
//        for(VirtualMachineDescriptor vmd : list){
//            System.out.println(vmd.displayName());
//            //遍历每一个正在运行的JVM，如果JVM名称为AgentSpringApplication则连接该JVM并加载特定Agent
//            if(vmd.displayName().contains("AgentSpringApplication")){
//
//                //连接指定JVM
//                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
//                //加载Agent
//                virtualMachine.loadAgent("/Users/y1shin/Downloads/servletDemo/src/main/java/com/AgentInject/aaaa.jar");
//                //断开JVM连接
//                virtualMachine.detach();
//            }
//
//        }
//        return "inject Success";
//    }
//
//
//
//
//}
