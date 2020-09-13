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

public class ScanVF extends AbstractSite{

    private static final Logger logger = LogManager.getLogger(ScanVF.class);

    public ScanVF() {
        super("Scan-vf", "https://www.scan-vf.net");
    }

    public List<Manga> getMangas() {
        List<Manga> mangaList = new ArrayList<>();
        // TODO add some resilience4J
        // TODO think about wrap http
        // HTML helper or something like that
        try {
            Document document = Jsoup.connect(String.join("/", url, "changeMangaList?type=text")).get();
            Elements elements = document.select("li a");
            for(Element element : elements) {
                try {
                    Manga m = new Manga();
                    m.setName(element.text());
                    m.setUrl(element.absUrl("href"));
                    mangaList.add(m);
                } catch (Exception e) {
                    logger.error("couldn't parse {}", element, e);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mangaList;
    }

    @Override
    public void getManga(Manga manga) {
    }

    @Override
    public void getChapters(Manga manga) {
        try {
            Document document = Jsoup.connect(manga.getUrl()).get();
            Elements elements = document.select("li .chapter-title-rtl");
            List<Chapter> chapters = new ArrayList<>();
            manga.setChapters(chapters);
            for(Element element : elements) {
                try {
                    Chapter chapter = new Chapter(manga);
                    String titleAndChapter = element.text();

                    // FIXME robustness
                    String title = titleAndChapter.substring(titleAndChapter.indexOf(":"));
                    chapter.setName(title);

                    String url = element.select("a").get(0).absUrl("href");
                    chapter.setUrl(url);
                    chapter.setNumber(Integer.parseInt(url.substring(url.lastIndexOf("/") + 1).replace("chapitre-", "")));
                    chapters.add(chapter);

                } catch (Exception e) {
                    logger.error("couldn't extract chapter {}", element, e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getManga(Manga manga, List<Integer> chapters) {

    }
}
