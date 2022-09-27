package com.chl.demo.agent.instrument;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.chl.demo.agent.instrument.cache.AgentContextCache;
import javassist.ClassPool;
import javassist.CtClass;

public class RecoverAfterAgentTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        System.out.println(className);
        System.out.println("RecoverTransformer classBeingRedefined is not null ? " + (null != classBeingRedefined));
        System.out.println("RecoverTransformer classfileBuffer is not null ? " + (null != classfileBuffer));

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass;
        String classNameTemp = className.replaceAll("/", ".");
        byte[] bytes = AgentContextCache.getOriginalBytes(classNameTemp);

        // other after transformer happens before recover. if no this check, will cover preAgent result
        if (null == bytes) {
            return classfileBuffer;
        }

        try {
            ctClass = classPool.get(classNameTemp);
            ctClass.defrost();
            InputStream inputStream;
            if (null != bytes) {
                inputStream = new ByteArrayInputStream(bytes);
            }else {
                inputStream = ClassLoader.getSystemResourceAsStream(className + ".class");
            }
            ctClass = classPool.makeClass(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ex at ctClass");
            return classfileBuffer;
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
