package com.chl.demo.agent.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.chl.demo.agent.instrument.cache.AgentContextCache;
import com.chl.demo.agent.instrument.param.AgentParam;
import com.chl.demo.agent.instrument.utils.AgentUtils;

public abstract class BaseAfterAgentTransformer implements ClassFileTransformer {
    public abstract void setParam(AgentParam param);
    public abstract void cleanParam();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String classNameTemp = AgentUtils.classNameIntervalDot(className);
        AgentContextCache.addCache(classNameTemp, classfileBuffer);

        return doTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }

    protected abstract byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer);
}
