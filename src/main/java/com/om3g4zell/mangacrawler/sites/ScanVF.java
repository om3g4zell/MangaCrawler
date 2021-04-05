package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ScanVF extends LelScanStyle {

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

    @Override
    public Manga getChapters(Manga manga) {
        Document document = getDocument(manga.url());
        Elements elements = document.select("li .chapter-title-rtl");

        var availableChapters = manga.chapters().stream()
                .collect(Collectors.toMap(Chapter::number, chapter -> chapter));
        var newChapters = new HashMap<Double, Chapter>();
        for (Element element : elements) {
            try {
                var titleAndChapter = element.text();
                var title = titleAndChapter.substring(titleAndChapter.indexOf(":") + 1).trim();
                var url = element.select("a").get(0).absUrl("href");

                var lastSegment = url.substring(url.lastIndexOf("/") + 1);
                var chapterNumberString = CHAPTER_MATCHER.matcher(lastSegment);
                var couldExtractChapterNumber = chapterNumberString.find();
                if (!couldExtractChapterNumber) {
                    throw new IllegalArgumentException("Couldn't extract chapter number" + lastSegment);
                }
                var number = Double.parseDouble(chapterNumberString.group());
                var chapter = Chapter.builder()
                        .name(title)
                        .url(url)
                        .number(number)
                        .pages(new ConcurrentLinkedQueue<>())
                        .build();

                if (!availableChapters.containsKey(number)) {
                    newChapters.put(number, chapter);
                }

            } catch (Exception e) {
                logger.atError()
                        .withThrowable(e)
                        .log("couldn't extract chapter {}", element);
            }
        }

        manga.chapters()
                .addAll(newChapters.values());
        return manga;
    }

}
