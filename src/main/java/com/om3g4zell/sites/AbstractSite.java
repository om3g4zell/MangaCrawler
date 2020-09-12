package com.om3g4zell.sites;

public abstract class AbstractSite implements Site {

    protected String url;

    protected String name;

    public AbstractSite(String name, String url) {
        this.url = url;
        this.name = name;
    }

}
