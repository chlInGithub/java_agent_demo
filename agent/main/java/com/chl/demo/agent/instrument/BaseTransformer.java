package com.chl.demo.agent.instrument;

import java.lang.instrument.ClassFileTransformer;

import com.chl.demo.agent.instrument.param.AgentParam;

public abstract class BaseTransformer implements ClassFileTransformer {
    public abstract void setParam(AgentParam param);
    public abstract void cleanParam();
}
