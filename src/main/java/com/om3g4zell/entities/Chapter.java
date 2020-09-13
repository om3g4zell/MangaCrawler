package com.om3g4zell.entities;

import java.util.List;

public class Chapter {

    private final Manga manga;

    private Integer number;

    // Maybe we can't have this
    private String name;

    private String url;

    private List<Page> pages;


    public Chapter(Manga manga) {
        this.manga = manga;
    }

    public Manga getManga() {
        return manga;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "manga=" + manga.getName() +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
