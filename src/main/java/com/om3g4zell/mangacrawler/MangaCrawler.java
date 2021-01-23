package com.om3g4zell.mangacrawler;

import com.om3g4zell.mangacrawler.download.Downloader;
import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.pdf.PdfSaver;
import com.om3g4zell.mangacrawler.sites.ScanFRCC;
import com.om3g4zell.mangacrawler.sites.ScanManga;
import com.om3g4zell.mangacrawler.sites.ScanVF;
import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) throws ThirdPartyCallFailedException, IOException {
        //var site = new ScanVF();
        //var scanManga = new ScanManga();
        var site = new ScanFRCC();

        /*var mangas = site.getAvailableMangas().stream().collect(Collectors.toMap(Manga::name, manga -> manga));
        System.out.println(mangas);
        var manga = site.getChapters(mangas.get("Tales Of Demons And Gods"));
        System.out.println(manga);
        manga = site.getPages(manga, manga.chapters().stream().map(Chapter::number).collect(Collectors.toList()));
        System.out.println(manga);*/
       //var manga = Downloader.readTree(Path.of("res/Scan-fr-Tales Of Demons And Gods-tree.json"));
       //Downloader.download(manga, Path.of("res"));
        PdfSaver.save(Path.of("res", "scan-fr", "Tales Of Demons And Gods"), 0);
    }
}
