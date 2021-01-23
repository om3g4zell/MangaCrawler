package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.entities.Page;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ScanVF extends AbstractSite {

    private static final Logger logger = LogManager.getLogger(ScanVF.class);

    public ScanVF() {
        super("Scan-vf", "https://www.scan-vf.net");
    }

    public List<Manga> getAvailableMangas() {
        List<Manga> mangaList = new ArrayList<>();
        try {
            Document document = Jsoup.connect(String.join("/", url, "changeMangaList?type=text")).get();
            Elements elements = document.select("li a");
            for (Element element : elements) {
                try {
                    var manga = Manga.builder()
                            .sourceWebSiteName(name)
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
                    var number = Double.parseDouble(url.substring(url.lastIndexOf("-") + 1));

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
                    .withSourceWebSiteName(name)
                    .withChapters(availableChapters);
        } catch (IOException e) {
            logger.atError()
                    .withThrowable(e)
                    .log("Couldn't access to the site");
        }

        return Manga.copyOf(manga);
    }

    @Override
    public Manga getPages(Manga manga, List<Double> chapters) {

        var extractedChapters = new ConcurrentLinkedQueue<Chapter>();
        try (ProgressBar pb = new ProgressBar("construct " + manga.name() + " tree...", chapters.size(), 10, System.out, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.MILLIS, 0L, Duration.ZERO)) {
            chapters.parallelStream().forEach(chapterId -> {

                var maybeChapter = manga.chapters().stream()
                        .filter(chapter -> chapter.number() == chapterId)
                        .findFirst();
                if (maybeChapter.isEmpty()) {
                    // The chapter doens't exist, we skip
                    return;
                }

                var chapter = maybeChapter.get();
                var pages = new ArrayList<Page>();

                try {
                    Document document = Jsoup.connect(chapter.url()).get();

                    // all <img>
                    Elements elements = document.select("#all img");

                    for (Element e : elements) {
                        var imageUrl = e.attr("data-src");
                        var altAttr = e.attr("alt");
                        var pageNumber = altAttr.substring(altAttr.lastIndexOf(" ") + 1).trim();
                        pages.add(Page.builder()
                                .imageUrl(imageUrl.trim())
                                .number(Integer.parseInt(pageNumber))
                                .build());
                    }
                    extractedChapters.add(Chapter.copyOf(chapter).withPages(pages));
                    pb.step();
                    pb.setExtraMessage("Completed chapter " + chapterId);
                } catch (IOException e) {
                    logger.atError()
                            .withThrowable(e)
                            .log("Couldn't access to the site");
                }
            });
        }

        return Manga.copyOf(manga)
                .withSourceWebSiteName(name)
                .withChapters(extractedChapters);
    }

}
