package com.om3g4zell.mangacrawler;

import com.om3g4zell.mangacrawler.download.Downloader;
import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.sites.ScanVF;
import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) throws ThirdPartyCallFailedException, IOException {
        var site = ScanVF.getInstance();
        var manga = Downloader.readTree(Path.of("res"), Manga.builder()
                .name("Solo Leveling")
                .url("")
                .sourceWebSiteName(site.getName()).build());
        manga = site.getChapters(manga);
        manga = site.getPages(manga);
        Downloader.saveTree(manga, Path.of("res"));
        Downloader.download(manga, Path.of("res"));
    }
}
