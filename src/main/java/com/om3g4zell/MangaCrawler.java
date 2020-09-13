package com.om3g4zell;

import com.om3g4zell.entities.Manga;
import com.om3g4zell.sites.ScanVF;
import com.om3g4zell.sites.Site;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) {
        Site scanVF = new ScanVF();
        List<Manga> mangas = scanVF.getMangas();
        mangas.forEach(manga -> System.out.println(manga));

        Manga manga = mangas.get(0);
        scanVF.getChapters(manga);

        manga.getChapters().forEach(chapter -> System.out.println(chapter));

    }
}
