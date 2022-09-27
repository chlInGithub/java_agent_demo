package com.chl.demo.agent.target;

import com.chl.deml.agent.annotation.AgentClassAnnotation;
import com.chl.deml.agent.annotation.AgentMethodAnnotation;

@AgentClassAnnotation
public class AgentTarget {

    @AgentMethodAnnotation
    public void targetMethod(String param) {
        System.out.println(param);
    }
}
