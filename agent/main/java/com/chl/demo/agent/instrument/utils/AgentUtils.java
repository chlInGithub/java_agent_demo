package com.chl.demo.agent.instrument.utils;

public class AgentUtils {

    public static String classNameIntervalDot(String className) {
        String classNameTemp = className.replaceAll("/", ".");
        return classNameTemp;
    }
}
