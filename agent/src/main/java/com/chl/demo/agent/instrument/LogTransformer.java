package com.chl.demo.agent.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.chl.deml.agent.annotation.AgentClassAnnotation;
import com.chl.deml.agent.annotation.AgentMethodAnnotation;
import com.chl.demo.agent.instrument.utils.AgentUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class LogTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            ctClass = classPool.get(AgentUtils.classNameIntervalDot(className));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ex at ctClass");
            return classfileBuffer;
        }

        if (!ctClass.hasAnnotation(AgentClassAnnotation.class)) {
            return classfileBuffer;
        }

        ctClass.stopPruning(true);
        ctClass.defrost();

        for (CtMethod method : ctClass.getMethods()) {
            if (method.hasAnnotation(AgentMethodAnnotation.class)) {
                try {
                    method.insertBefore("System.out.println(\"=== before targetMethod method ===\");");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ex at insertBefore");
                    return classfileBuffer;
                }
            }
        }

        try {
            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ex at toBytecode");
            return classfileBuffer;
        }
    }
}
