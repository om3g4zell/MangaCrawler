package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.entities.Manga;

import java.util.List;

public interface Site {

    List<Manga> getAvailableMangas();

    Manga getChapters(Manga manga);

    Manga getPages(Manga manga, List<String> chapters);

}
