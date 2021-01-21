package com.om3g4zell.mangacrawler;

import com.om3g4zell.mangacrawler.sites.ScanVF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) {
        var scanVF = new ScanVF();
        var mangas = scanVF.getAvailableMangas();
        var manga = scanVF.getChapters(mangas.get(0));
        System.out.println(manga);
        System.out.println(scanVF.getPages(manga, List.of(manga.chapters().get(0).number())));
    }
}
