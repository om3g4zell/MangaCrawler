package com.om3g4zell.mangacrawler.sites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LelScanVF extends LelScanStyle {

    private static final Logger logger = LogManager.getLogger(LelScanVF.class);

    private static LelScanVF instance = null;

    public static LelScanVF getInstance() {
        if (instance == null) {
            instance = new LelScanVF();
        }
        return instance;
    }

    private LelScanVF() {
        super("lelscan-vf", "https://lelscan-vf.co/");
    }

    @Override
    protected String getChapterSelector() {
        return ".chapter-title-rtl";
    }
}
