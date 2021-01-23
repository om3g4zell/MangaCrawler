package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;

import java.util.List;

public interface Site {

    List<Manga> getAvailableMangas() throws ThirdPartyCallFailedException;

    Manga getChapters(Manga manga) throws ThirdPartyCallFailedException;

    Manga getPages(Manga manga, List<Double> chapters);

}
