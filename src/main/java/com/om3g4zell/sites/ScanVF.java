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
        // TODO split jsoup and logic
        List<Manga> mangaList = new ArrayList<>();
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
    public Manga getManga(Manga manga) {
        return null;
    }

    @Override
    public List<Chapter> getChapters(Manga manga) {
        List<Chapter> chapters = new ArrayList<>();
        try {
            Document document = Jsoup.connect(manga.getUrl()).get();
            

        } catch (IOException e) {
            e.printStackTrace();
        }

        return chapters;
    }

    @Override
    public Manga getManga(Manga manga, List<Integer> chapters) {
        return null;
    }
}
