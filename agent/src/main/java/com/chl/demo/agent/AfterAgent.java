package com.chl.demo.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.chl.demo.agent.instrument.BaseTransformer;
import com.chl.demo.agent.instrument.cache.AgentContextCache;
import com.chl.demo.agent.instrument.LineLogTransformer;
import com.chl.demo.agent.instrument.enums.AgentParamCommandEnum;
import com.chl.demo.agent.instrument.ex.AgentException;
import com.chl.demo.agent.instrument.param.AgentParam;
import com.sun.tools.attach.VirtualMachine;

/**
 * after main, need call
 * @author hailongchen
 */
public class AfterAgent {

    static Map<Class, ClassFileTransformer> transformerMap = new HashMap<>();

    public static void main(String[] args) {
        String jvmId = "142764";
        try {
            VirtualMachine virtualMachine = VirtualMachine.attach(jvmId);
            virtualMachine.loadAgent("D:\\workspace\\agentDemo\\agent\\target\\agent-1.0-SNAPSHOT-jar-with-dependencies.jar",
                    "1#com.chl.demo.agent.target.AgentTarget#targetMethod#1@System.out.println(\"=== this is from agentMain param0 ===\");");
            virtualMachine.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<ClassFileTransformer> transformers = new ArrayList<>();

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

        try {
            System.out.println("agentmain " + classes.size());
            System.out.println("current transformers size " + transformers.size());
            Iterator<ClassFileTransformer> iterator = transformers.iterator();
            while (iterator.hasNext()) {
                ClassFileTransformer next = iterator.next();
                boolean b = inst.removeTransformer(next);
                if (b) {
                    iterator.remove();
                    System.out.println("remove result " + b);
                }
            }

            switch (AgentParamCommandEnum.parseType(param.getCommand())) {
                case ADD_LINE_LOG:{
                    for (Class aClass : classes) {
                        AgentContextCache.remRecover(aClass.getName());
                    }
                    ClassFileTransformer transformerCache = transformerMap.get(LineLogTransformer.class);
                    if (null == transformerCache) {
                        ClassFileTransformer transformer = new LineLogTransformer(param);
                        transformerMap.put(LineLogTransformer.class, transformer);
                        transformers.add(transformer);
                        inst.addTransformer(transformer, true);
                        inst.retransformClasses(classes.toArray(new Class[0]));
                    }else {
                        LineLogTransformer lineLogTransformer = (LineLogTransformer) transformerCache;
                        lineLogTransformer.setParam(param);
                        inst.retransformClasses(classes.toArray(new Class[0]));
                    }
                    break;
                }
                case RECOVER:{
                    Iterator<ClassFileTransformer> iteratorTemp = transformers.iterator();
                    while (iteratorTemp.hasNext()) {
                        ClassFileTransformer next = iteratorTemp.next();
                        BaseTransformer baseTransformer = (BaseTransformer) next;
                        baseTransformer.cleanParam();
                    }

                    List<ClassDefinition> classDefinitions = new ArrayList<>();
                    for (Class aClass : classes) {
                        String classNameTemp = aClass.getName();
                        byte[] value = AgentContextCache.getValue(classNameTemp);
                        System.out.println("will recover " + classNameTemp);
                        if (null != value) {
                            System.out.println("real recover " + classNameTemp);
                            ClassDefinition classDefinition = new ClassDefinition(aClass, value);
                            classDefinitions.add(classDefinition);
                            AgentContextCache.recover(classNameTemp);
                        }
                    }
                    inst.redefineClasses(classDefinitions.toArray(new ClassDefinition[ 0 ]));
                    break;
                }
                default:{
                    break;
                }
            }
        } catch (UnmodifiableClassException | ClassNotFoundException e) {
            e.printStackTrace();
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
