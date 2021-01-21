package com.om3g4zell.mangacrawler;

import com.om3g4zell.mangacrawler.download.Downloader;
import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.sites.ScanVF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.stream.Collectors;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) throws InterruptedException {
        var scanVF = new ScanVF();
        var mangas = scanVF.getAvailableMangas().stream().collect(Collectors.toMap(Manga::name, manga -> manga));
        System.out.println(mangas);
        var manga = scanVF.getChapters(mangas.get("Solo Leveling"));
        manga = scanVF.getPages(manga, manga.chapters().stream().map(Chapter::number).collect(Collectors.toList()));
        Downloader.download(manga, Path.of("res"));
    }
}
