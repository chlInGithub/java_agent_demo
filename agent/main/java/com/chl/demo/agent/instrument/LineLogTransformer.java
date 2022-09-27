package com.chl.demo.agent.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.chl.deml.agent.annotation.AgentClassAnnotation;
import com.chl.deml.agent.annotation.AgentMethodAnnotation;
import com.chl.demo.agent.instrument.cache.AgentContextCache;
import com.chl.demo.agent.instrument.ex.AgentException;
import com.chl.demo.agent.instrument.param.AgentParam;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class LineLogTransformer extends BaseTransformer {

    LineLogParam param;
    AgentParam agentParam;

    public LineLogTransformer(AgentParam param) {
        setParam(param);
    }

    @Override
    public void setParam(AgentParam param) {
        this.agentParam = param;

        String transformerParam = param.getTransformerParam();
        if (null == transformerParam || transformerParam.length() == 0) {
            throw new AgentException("transformerParam is empty");
        }
        String[] split = transformerParam.split("@");
        if (split.length < 2) {
            throw new AgentException("transformerParam length less 2");
        }

        this.param = new LineLogParam(Integer.valueOf(split[ 0 ]), split[ 1 ]);
    }

    @Override
    public void cleanParam() {
        param = null;
        agentParam = null;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        System.out.println(className);
        System.out.println("classBeingRedefined is not null ? " + (null != classBeingRedefined));
        System.out.println("classfileBuffer is not null ? " + (null != classfileBuffer));

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass;
        String classNameTemp = className.replaceAll("/", ".");
        if (AgentContextCache.needRecover(classNameTemp)) {
            return classfileBuffer;
        }

        try {
            ctClass = classPool.get(classNameTemp);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ex at ctClass");
            return classfileBuffer;
        }

        if (!ctClass.hasAnnotation(AgentClassAnnotation.class)) {
            System.out.println("not AgentClassAnnotation");
            return classfileBuffer;
        }

        ctClass.stopPruning(true);
        ctClass.defrost();
        ctClass.rebuildClassFile();

        for (CtMethod method : ctClass.getMethods()) {
            if (method.hasAnnotation(AgentMethodAnnotation.class)) {
                try {
                    method.insertAt(param.getLineNum(), param.getLogContent());
                    System.out.println("insertAt " + param.getLineNum());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ex at insertBefore");
                    return classfileBuffer;
                }
            }
        }

        try {
            AgentContextCache.addCache(classNameTemp, classfileBuffer);

            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ex at toBytecode");
            return classfileBuffer;
        }
    }

    class LineLogParam{

        Integer lineNum;

        String logContent;

        public LineLogParam(Integer lineNum, String logContent) {
            this.lineNum = lineNum;
            this.logContent = logContent;
        }

        public Integer getLineNum() {
            return lineNum;
        }

        public void setLineNum(Integer lineNum) {
            this.lineNum = lineNum;
        }

        public String getLogContent() {
            return logContent;
        }

        public void setLogContent(String logContent) {
            this.logContent = logContent;
        }
    }
}
