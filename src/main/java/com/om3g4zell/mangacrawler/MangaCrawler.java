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

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) throws ThirdPartyCallFailedException {
        //PdfSaver.save(Path.of("res", "Scan-vf", "Tales Of Demons And Gods"), 0);
        //var scanVF = new ScanVF();
        //var scanManga = new ScanManga();
        var scanFr = new ScanFRCC();

        var mangas = scanFr.getAvailableMangas().stream().collect(Collectors.toMap(Manga::name, manga -> manga));
        System.out.println(mangas);
        var manga = scanFr.getChapters(mangas.get("Tales Of Demons And Gods"));
        System.out.println(manga);
        manga = scanFr.getPages(manga, manga.chapters().stream().map(Chapter::number).collect(Collectors.toList()));
        System.out.println(manga);
        Downloader.download(manga, Path.of("res"));
    }
}
