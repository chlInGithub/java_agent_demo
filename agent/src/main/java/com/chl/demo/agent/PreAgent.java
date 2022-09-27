package com.chl.demo.agent;

import java.lang.instrument.Instrumentation;

import com.chl.demo.agent.instrument.LogTransformer;

/**
 * before main
 */
public class PreAgent {

    public static void premain(String agentArgs, Instrumentation inst){
        System.out.println("premain has two params s");
        inst.addTransformer(new LogTransformer());
        System.out.println("premain has two params e");
    }

    public static void premain(String agentArgs){
        System.out.println("premain has one param");
    }
}
