package com.chl.demo.agent.target;

import com.chl.deml.agent.annotation.AgentClassAnnotation;
import com.chl.deml.agent.annotation.AgentMethodAnnotation;

@AgentClassAnnotation
public class AgentTarget {
    private int num = 1;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    Person person;

    @AgentMethodAnnotation
    public void targetMethod(String param) {
        for (int i = 0; i < num; i++) {
            System.out.println(param);
        }
    }
}
