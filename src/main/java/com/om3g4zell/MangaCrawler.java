package com.om3g4zell;

import com.om3g4zell.sites.ScanVF;
import com.om3g4zell.sites.Site;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MangaCrawler {

    private static final Logger logger = LogManager.getLogger(MangaCrawler.class);

    public static void main(String[] args) {
        Site scanVF = new ScanVF();
        scanVF.getMangas().forEach(manga -> System.out.println(manga));
    }
}
