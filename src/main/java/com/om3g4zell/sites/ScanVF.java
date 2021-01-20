package com.om3g4zell.sites;

import com.om3g4zell.entities.Chapter;
import com.om3g4zell.entities.Manga;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanVF extends AbstractSite {

    private static final Logger logger = LogManager.getLogger(ScanVF.class);

    public ScanVF() {
        super("Scan-vf", "https://www.scan-vf.net");
    }

    public List<Manga> getAvailableMangas() {
        List<Manga> mangaList = new ArrayList<>();
        // TODO add some resilience4J
        // TODO think about wrap http
        // HTML helper or something like that
        try {
            Document document = Jsoup.connect(String.join("/", url, "changeMangaList?type=text")).get();
            Elements elements = document.select("li a");
            for (Element element : elements) {
                try {
                    var manga = Manga.builder()
                            .name(element.text())
                            .url(element.absUrl("href"))
                            .build();
                    mangaList.add(manga);
                } catch (Exception e) {
                    logger.atError()
                            .withThrowable(e)
                            .log("couldn't parse {}", element);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mangaList;
    }

    @Override
    public Manga getChapters(Manga manga) {
        try {
            Document document = Jsoup.connect(manga.url()).get();
            Elements elements = document.select("li .chapter-title-rtl");

            var availableChapters = new ArrayList<Chapter>();
            for (Element element : elements) {
                try {
                    var titleAndChapter = element.text();
                    var title = titleAndChapter.substring(titleAndChapter.indexOf(":") + 1).trim();
                    var url = element.select("a").get(0).absUrl("href");
                    var number = url.substring(url.lastIndexOf("-") + 1);

                    var chapter = Chapter.builder()
                            .name(title)
                            .url(url)
                            .number(number)
                            .build();
                    availableChapters.add(chapter);

                } catch (Exception e) {
                    logger.atError()
                            .withThrowable(e)
                            .log("couldn't extract chapter {}", element);
                }
            }
            return Manga.copyOf(manga)
                    .withChapters(availableChapters);
        } catch (IOException e) {
            logger.atError()
                    .withThrowable(e)
                    .log("Couldn't access to the site");
        }

        return Manga.copyOf(manga);
    }

    @Override
    public Manga getPages(Manga manga, List<String> chapters) {
        for (String chapterId : chapters) {
            var maybeChapter = manga.chapters().stream()
                    .filter(chapter -> chapter.number().equals(chapterId))
                    .findFirst();
            if (maybeChapter.isEmpty()) {
                // The chapter doens't exist, we skip
                continue;
            }

            var chapter = maybeChapter.get();

            try {
                Document document = Jsoup.connect(chapter.url()).get();

                // all <img>
                Elements elements = document.select("#all img");

                for (Element e : elements) {
                    var imageUrl = e.attr("data-src");
                    var pageNumber = e.attr("alt").substring(e.attr("alt").lastIndexOf(" " + 1));
                }
            } catch (IOException e) {
                logger.atError()
                        .withThrowable(e)
                        .log("Couldn't access to the site");
            }

        }
        return null;
    }

}
