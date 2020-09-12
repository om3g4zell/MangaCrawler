package com.om3g4zell.sites;

import com.om3g4zell.entities.Manga;

import java.util.List;

public interface Site {

    List<Manga> getMangas();

    Manga getManga(String name);

    Manga getManga(String name, List<Integer> chapters);

}
