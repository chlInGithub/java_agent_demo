package com.chl.demo.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

import com.chl.demo.agent.instrument.LineLogAfterAgentTransformer;
import com.chl.demo.agent.instrument.RecoverAfterAgentTransformer;
import com.chl.demo.agent.instrument.enums.AgentParamCommandEnum;
import com.chl.demo.agent.instrument.ex.AgentException;
import com.chl.demo.agent.instrument.param.AgentParam;
import com.sun.tools.attach.VirtualMachine;

/**
 * after main, need call
 * @author hailongchen
 */
public class AfterAgent {

    public static void main(String[] args) {
        String jvmId = "54648";
        try {
            VirtualMachine virtualMachine = VirtualMachine.attach(jvmId);
            virtualMachine.loadAgent("D:\\workspace\\agentDemo\\agent\\target\\agent-1.0-SNAPSHOT-jar-with-dependencies.jar",
                    "2#com.chl.demo.agent.target.AgentTarget#targetMethod#1@System.out.println(\"=== this is from agentMain param1 ===\");");
            virtualMachine.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst){

        System.out.println("agentmain has two params " + agentArgs);
        AgentParam param = parseParam(agentArgs);
        String className = param.getClassName();

        List<Class> classes = new ArrayList<>();
        for (Class loadedClass : inst.getAllLoadedClasses()) {
            if (loadedClass.getName().equals(className)) {
                classes.add(loadedClass);
            }
        }

        ClassFileTransformer transformer = null;
        try {
            switch (AgentParamCommandEnum.parseType(param.getCommand())) {
                case ADD_LINE_LOG:{
                    transformer = new LineLogAfterAgentTransformer(param);
                    inst.addTransformer(transformer, true);
                    inst.retransformClasses(classes.toArray(new Class[0]));
                    break;
                }
                case RECOVER:{
                    transformer = new RecoverAfterAgentTransformer();
                    inst.addTransformer(transformer, true);
                    inst.retransformClasses(classes.toArray(new Class[0]));
                    break;
                }
                default:{
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != transformer) {
                boolean b = inst.removeTransformer(transformer);
                if (b) {
                    System.out.println("finally remove ok");
                }
            }
        }
    }

    private static AgentParam parseParam(String agentArgs) {
        String[] split = agentArgs.split("#");
        if (split.length < 3) {
            throw new AgentException("agent param length less 3");
        }
        Integer command = Integer.valueOf(split[ 0 ]);
        String className = split[ 1 ];
        String methodName = split[ 2 ];
        AgentParam param = new AgentParam(command, className, methodName, split.length > 3 ? split[ 3 ] : null);
        return param;
    }

    public static void agentmain(String agentArgs){
        System.out.println("agentmain has two params");
    }
}
