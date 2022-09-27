package com.chl.demo.agent.instrument.enums;

public enum AgentParamCommandEnum {
    ADD_LINE_LOG(1, ""),
    RECOVER(2, ""),
    ;

    Integer type;
    String desc;
    AgentParamCommandEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static AgentParamCommandEnum parseType(Integer type){
        for (AgentParamCommandEnum commandEnum : values()) {
            if (commandEnum.type.equals(type)) {
                return commandEnum;
            }
        }
        return null;
    }
}
