package com.om3g4zell.entities;

import java.awt.*;

public class Page {

    private final Chapter chapter;

    private Integer number;

    private String imageUrl;

    // Maybe change type
    private byte[] image;

    public Page(Chapter chapter) {
        this.chapter = chapter;
    }

}
