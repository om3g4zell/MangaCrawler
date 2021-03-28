package com.om3g4zell.mangacrawler;

import com.om3g4zell.mangacrawler.download.Downloader;
import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.pdf.PdfSaver;
import com.om3g4zell.mangacrawler.sites.ScanFRCC;
import com.om3g4zell.mangacrawler.sites.ScanVF;
import com.om3g4zell.mangacrawler.sites.Site;
import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class MangaCrawler {

    private static final Path ROOT_PATH = Path.of("res");

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) throws ThirdPartyCallFailedException, IOException {
        var site = ScanFRCC.getInstance();
        updateRoot(ROOT_PATH, site, true, true);
        //lastChapters(site);

        //updateManga(site, "", false, ROOT_PATH);
    }

    private static void updateManga(Site source, String name, boolean makePdf, Path rootPath) throws IOException {
        var manga = Downloader.readTree(rootPath, name, source.getUrl());
        if (manga == null) {
            return;
        }

        // Update chapter list
        manga = source.getChapters(manga);
        manga = source.getPages(manga);

        Downloader.saveTree(manga, rootPath);
        Downloader.download(manga, rootPath);

        if (makePdf) {
            PdfSaver.save(manga, rootPath, 0);
        }
    }

    private static void lastChapters(Site source) {
        var mangas = source.getAvailableMangas();
        mangas.forEach((s, manga) -> source.getChapters(manga));
        mangas.values().parallelStream()
                .map(manga -> String.format("%s : %s -> %s",
                        manga.name(),
                        manga.chapters().stream()
                                .map(Chapter::number)
                                .min(Double::compareTo)
                                .orElse(-1d)
                                .toString(),
                        manga.chapters().stream()
                                .map(Chapter::number)
                                .max(Double::compareTo)
                                .orElse(-1d)
                                .toString()))
                .forEach(logger::info);
    }

    private static void updateRoot(Path root, Site source, boolean withPdf, boolean withImages) {
        var mangas = source.getAvailableMangas();
        mangas.values().forEach(manga -> {
            manga = Downloader.readTree(root, manga.name(), source.getName());
            if (manga == null) {
                return;
            }
            source.getChapters(manga);
            source.getPages(manga);
            try {
                Downloader.saveTree(manga, root);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(withImages) {
                Downloader.download(manga, root);
            }
            if(withPdf) {
                PdfSaver.save(manga, root, 0);
            }
        });
    }
}
