package com.chl.demo.agent;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author hailongchen
 */
public class AfterAgentClient {

    public static void main(String[] args) {
        String jvmId = "91996";
        try {
            VirtualMachine virtualMachine = VirtualMachine.attach(jvmId);
            String srcContent = "person = new com.chl.demo.agent.target.Person();\n"
                    + "person.setAge(10);\n"
                    + "if (null != person) {\n"
                    + "System.out.println(\"person age is \" + person.getAge());\n"
                    + "}";
            virtualMachine.loadAgent("D:\\workspace\\agentDemo\\agent\\target\\agent-1.0-SNAPSHOT-jar-with-dependencies.jar",
                    "1#com.chl.demo.agent.target.AgentTarget#targetMethod#0@num = 2;int j = 101;param = param + 101;" + srcContent);
            virtualMachine.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
