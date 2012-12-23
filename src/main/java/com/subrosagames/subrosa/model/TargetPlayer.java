package com.subrosagames.subrosa.model;

import org.codehaus.jackson.annotate.JsonTypeName;

/**
 *
 */
@JsonTypeName("PLAYER")
public class TargetPlayer extends Target {

    private Image photoId;
    private Image actionPhoto;
    private Image avatar;
    private Address homeAddress;
    private Address workAddress;

    public Image getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Image photoId) {
        this.photoId = photoId;
    }

    public Image getActionPhoto() {
        return actionPhoto;
    }

    public void setActionPhoto(Image actionPhoto) {
        this.actionPhoto = actionPhoto;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }
}
