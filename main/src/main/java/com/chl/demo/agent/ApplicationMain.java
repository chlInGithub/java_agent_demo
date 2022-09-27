package com.chl.demo.agent;

import com.chl.demo.agent.target.AgentTarget;

public class ApplicationMain {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main");
        AgentTarget agentTarget = new AgentTarget();
        while (true) {
            Thread.sleep(10000);
            agentTarget.targetMethod("from main param");
        }
    }
}
