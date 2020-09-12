package com.om3g4zell.sites;

import com.om3g4zell.entities.Chapter;
import com.om3g4zell.entities.Manga;

import java.util.List;

public interface Site {

    List<Manga> getMangas();

    Manga getManga(Manga manga);

    List<Chapter> getChapters(Manga manga);

    Manga getManga(Manga manga, List<Integer> chapters);

}
