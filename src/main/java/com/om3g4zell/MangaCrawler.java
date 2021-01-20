package com.om3g4zell;

import com.om3g4zell.sites.ScanVF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) {
        var scanVF = new ScanVF();
        var mangas = scanVF.getAvailableMangas();
        mangas.forEach(manga -> logger.atInfo()
                .log(scanVF.getChapters(manga)));
    }
}
