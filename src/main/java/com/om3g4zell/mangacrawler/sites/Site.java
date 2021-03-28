package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;

import java.util.Map;

public interface Site {

    Map<String, Manga> getAvailableMangas() throws ThirdPartyCallFailedException;

    Manga getChapters(Manga manga) throws ThirdPartyCallFailedException;

    Manga getPages(Manga manga);

    String getName();

    String getUrl();

}
