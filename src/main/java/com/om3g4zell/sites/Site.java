package com.om3g4zell.sites;

import com.om3g4zell.entities.Manga;

import java.util.List;

public interface Site {

    List<Manga> getMangas();

    void getManga(Manga manga);

    void getChapters(Manga manga);

    void getManga(Manga manga, List<Integer> chapters);

}
