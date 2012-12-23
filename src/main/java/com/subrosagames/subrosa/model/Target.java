package com.subrosagames.subrosa.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "targetType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TargetPlayer.class, name = "PLAYER"),
        @JsonSubTypes.Type(value = TargetTeam.class, name = "TEAM")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Target {

    private int targetId;
    private TargetType targetType;

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
}
