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
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ScanVF extends AbstractSite {

    private static final Logger logger = LogManager.getLogger(ScanVF.class);

    private static ScanVF instance = null;

    public static ScanVF getInstance() {
        if (instance == null) {
            instance = new ScanVF();
        }
        return instance;
    }

    private ScanVF() {
        super("Scan-vf", "https://www.scan-vf.net");
    }

    public Map<String, Manga> getAvailableMangas() {
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
                            .chapters(new ConcurrentLinkedQueue<>())
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
        return mangaList.stream()
                .collect(Collectors.toMap(Manga::name, manga -> manga));
    }

    @Override
    public Manga getChapters(Manga manga) {
        Document document = getDocument(manga.url());
        Elements elements = document.select("li .chapter-title-rtl");

        var availableChapters = manga.chapters().stream().collect(Collectors.toMap(Chapter::number, chapter -> chapter));
        for (Element element : elements) {
            try {
                var titleAndChapter = element.text();
                var title = titleAndChapter.substring(titleAndChapter.indexOf(":") + 1).trim();
                var url = element.select("a").get(0).absUrl("href");

                var lastSegment = url.substring(url.lastIndexOf("/") + 1);
                var chapterNumberString = DOUBLE_MATCHER.matcher(lastSegment);
                var couldExtractChapterNumber = chapterNumberString.find();
                if(!couldExtractChapterNumber) {
                    throw new IllegalArgumentException("Couldn't extract chapter number" + lastSegment);
                }
                var number = Double.parseDouble(chapterNumberString.group());
                var chapter = Chapter.builder()
                        .name(title)
                        .url(url)
                        .number(number)
                        .pages(new ConcurrentLinkedQueue<>())
                        .build();
                availableChapters.putIfAbsent(number, chapter);

            } catch (Exception e) {
                logger.atError()
                        .withThrowable(e)
                        .log("couldn't extract chapter {}", element);
            }
        }

        manga.chapters()
                .addAll(availableChapters.values());
        return manga;
    }

    @Override
    public Manga getPages(Manga manga) {

        try (ProgressBar pb = new ProgressBar("construct " + manga.name() + " tree...", manga.chapters().size(), 10, System.out, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.MILLIS, 0L, Duration.ZERO)) {
            manga.chapters().parallelStream().forEach(chapter -> {

                var pages = new ArrayList<Page>();

                try {
                    if (chapter.pages().isEmpty()) {
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
                        chapter.pages()
                                .addAll(pages);
                    }
                    pb.step();
                    pb.setExtraMessage("Completed chapter " + chapter.number());
                } catch (IOException e) {
                    logger.atError()
                            .withThrowable(e)
                            .log("Couldn't access to the site");
                }
            });
        }

        return manga;
    }

}
