package com.om3g4zell.mangacrawler;

import com.om3g4zell.mangacrawler.download.Downloader;
import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.pdf.PdfSaver;
import com.om3g4zell.mangacrawler.sites.*;
import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Map;

public class MangaCrawler {

    private static final Path ROOT_PATH = Path.of("res");

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) throws ThirdPartyCallFailedException, IOException, InterruptedException {

        var site = LelScanVF.getInstance();
        getOrUpdateManga(site, "Solo Leveling", true, false, ROOT_PATH);
        //updateRoot(ROOT_PATH, site, true, true);
        //getOrUpdateManga(site, "Solo Leveling", false, true, ROOT_PATH);

        //PdfSaver.save(Path.of("res/test/test"), 0);
        //updateManga(site, "", false, ROOT_PATH);
    }

    private static void getOrUpdateManga(Site source, String name, boolean downloadImages, boolean makePdf, Path rootPath) throws IOException {
        var manga = Downloader.readTree(rootPath, name, source.getName());
        if (manga == null) {
            manga = source.getAvailableMangas().get(name);
        }

        // Update chapter list
        source.getChapters(manga);
        source.getPages(manga);

        Downloader.saveTree(manga, rootPath);

        if(downloadImages) {
            Downloader.download(manga, rootPath);
        }

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

    public static class fakeSite extends AbstractSite {

        public fakeSite() {
            super("test", "test.com");
        }

        @Override
        public Map<String, Manga> getAvailableMangas() throws ThirdPartyCallFailedException {
            return null;
        }

        @Override
        public Manga getChapters(Manga manga) throws ThirdPartyCallFailedException {
            return null;
        }

        @Override
        public Manga getPages(Manga manga) {
            return null;
        }
    }

}
