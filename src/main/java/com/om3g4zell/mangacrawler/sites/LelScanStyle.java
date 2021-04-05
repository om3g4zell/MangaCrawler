package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.download.PersonalizedProgressBar;
import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import com.om3g4zell.mangacrawler.entities.Page;
import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;
import me.tongfei.progressbar.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public abstract class LelScanStyle extends AbstractSite {

    private static final Logger logger = LogManager.getLogger(LelScanStyle.class);

    public LelScanStyle(String name, String url) {
        super(name, url);
    }

    @Override
    public Map<String, Manga> getAvailableMangas() throws ThirdPartyCallFailedException {
        List<Manga> mangaList = new ArrayList<>();

        Document document = getDocument(String.join("/", url, "changeMangaList?type=text"));
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

        return mangaList.stream()
                .collect(Collectors.toMap(Manga::name, manga -> manga));
    }

    protected String getChapterSelector() {
        return ".chapter-title-rtlrr";
    }

    @Override
    public Manga getChapters(Manga manga) {

        Document document = getDocument(manga.url());
        Elements elements = document.select(getChapterSelector());

        var availableChapters = manga.chapters()
                .stream()
                .collect(Collectors
                        .toMap(Chapter::number, chapter -> chapter));
        var newChapters = new HashMap<Double, Chapter>();
        for (Element element : elements) {
            try {
                var title = element.select("em").text();
                var url = element.select("a").get(0).absUrl("href").trim();

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

    @Override
    public Manga getPages(Manga manga) {

        try (ProgressBar pb = PersonalizedProgressBar.progressBar("construct " + manga.name() + " tree...", manga.chapters().size())) {
            manga.chapters().parallelStream().forEach(chapter -> {

                var pages = new ArrayList<Page>();

                try {
                    if (chapter.pages().isEmpty()) {
                        Document document = getDocument(chapter.url());

                        // all <img>
                        Elements elements = document.select("#all img");

                        for (Element e : elements) {
                            var imageUrl = e.attr("data-src").trim();

                            if(imageUrl.startsWith("//")) {
                                imageUrl = imageUrl.replace("//", "https://www.");
                            }

                            var altAttr = e.attr("alt");
                            var pageNumber = altAttr.substring(altAttr.lastIndexOf(" ") + 1).trim();

                            pages.add(Page.builder()
                                    .imageUrl(imageUrl)
                                    .number(Integer.parseInt(pageNumber))
                                    .build());
                        }
                        chapter.pages()
                                .addAll(pages);
                    }
                    pb.step();
                    pb.setExtraMessage("Completed chapter " + chapter.number());
                } catch (Exception e) {
                    logger.atError()
                            .withThrowable(e)
                            .log("Couldn't access to the site {}", chapter.url());
                }
            });
        }
        return manga;
    }
}
