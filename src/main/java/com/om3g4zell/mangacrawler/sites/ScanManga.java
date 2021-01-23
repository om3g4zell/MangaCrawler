package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.entities.Manga;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanManga extends AbstractSite{

    // TODO make it singleton
    private static final Logger logger = LogManager.getLogger(ScanVF.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    public ScanManga() {
        super("Scan-manga", "https://www.scan-manga.com/");
    }

    @Override
    public List<Manga> getAvailableMangas() {
        var mangas = new ArrayList<Manga>();

        try {
            Document document = Jsoup.connect(url).userAgent(USER_AGENT).get();
            System.out.println(document);
        } catch (IOException e) {
            logger.atError()
                    .withThrowable(e)
                    .log("Couldn't access to the site");
        }

        return mangas;
    }

    @Override
    public Manga getChapters(Manga manga) {
        return null;
    }

    @Override
    public Manga getPages(Manga manga, List<Double> chapters) {
        return null;
    }
}
