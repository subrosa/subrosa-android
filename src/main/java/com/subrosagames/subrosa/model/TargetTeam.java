package com.subrosagames.subrosa.model;

import org.codehaus.jackson.annotate.JsonTypeName;

/**
 *
 */
@JsonTypeName("TEAM")
public class TargetTeam extends Target {

    private Image image;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
