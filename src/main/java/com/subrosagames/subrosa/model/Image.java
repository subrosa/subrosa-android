package com.subrosagames.subrosa.model;

import java.net.URI;

/**
 *
 */
public class Image {

    private Integer id;
    private ImageType imageType;
    private URI uri;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
