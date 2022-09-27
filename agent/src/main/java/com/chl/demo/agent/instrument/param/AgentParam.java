package com.chl.demo.agent.instrument.param;

/**
 *
 * @author hailongchen
 */
public class AgentParam {

    public AgentParam(Integer command, String className, String methodName, String transformerParam) {
        this.command = command;
        this.className = className;
        this.methodName = methodName;
        this.transformerParam = transformerParam;
    }

    /**
     * @see com.chl.demo.agent.instrument.enums.AgentParamCommandEnum
     */
    private Integer command;

    String className;
    String methodName;

    String transformerParam;

    public String getTransformerParam() {
        return transformerParam;
    }

    public void setTransformerParam(String transformerParam) {
        this.transformerParam = transformerParam;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }
}
