package com.chl.demo.agent.instrument.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.chl.demo.agent.instrument.utils.AgentUtils;

/**
 *
 */
public class AgentContextCache {

    /**
     * original class bytes, used for recover
     */
    private static Map<String, CacheValue> classByteCacheMap= new HashMap();

    /**
     * if need recover
     */
    private static Set<String> recoverClass = new HashSet<>();

    public static void recover(String className) {
        recoverClass.add(AgentUtils.classNameIntervalDot(className));
    }

    public static boolean needRecover(String className) {
        return recoverClass.contains(AgentUtils.classNameIntervalDot(className));
    }

    public static void remRecover(String className) {
        recoverClass.remove(AgentUtils.classNameIntervalDot(className));
    }

    public static void addOriginalBytesCache(String className, byte[] bytes) {
        className = AgentUtils.classNameIntervalDot(className);
        if (!classByteCacheMap.containsKey(className)) {
            CacheValue cacheValue = new CacheValue();
            cacheValue.bytes = bytes;
            classByteCacheMap.putIfAbsent(className, cacheValue);
            System.out.println("add cache " + className);
        }
    }

    public static byte[] getOriginalBytes(String className) {
        className = AgentUtils.classNameIntervalDot(className);
        CacheValue cacheValue = classByteCacheMap.get(className);
        if (null != cacheValue) {
            return cacheValue.bytes;
        }
        return null;
    }

    private static class CacheValue{
        byte[] bytes;
    }
}
