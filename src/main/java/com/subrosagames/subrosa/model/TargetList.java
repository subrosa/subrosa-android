package com.subrosagames.subrosa.model;

import java.util.List;

/**
 *
 */
public class TargetList {

    private List<? extends Target> targets;

    public List<? extends Target> getTargets() {
        return targets;
    }

    public void setTargets(List<? extends Target> targets) {
        this.targets = targets;
    }

    public boolean hasTargets() {
        return !targets.isEmpty();
    }
}
