package com.om3g4zell.sites;

import com.om3g4zell.entities.Chapter;
import com.om3g4zell.entities.Manga;

import java.util.List;

public interface Site {

    List<Manga> getAvailableMangas();

    Manga getChapters(Manga manga);

    Manga getPages(List<Chapter> chapters);

}
