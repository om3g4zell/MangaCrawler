package com.om3g4zell.mangacrawler.sites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScanFRCC extends LelScanStyle {

    private static final Logger logger = LogManager.getLogger(ScanFRCC.class);

    private static ScanFRCC instance = null;

    public static ScanFRCC getInstance() {
        if (instance == null) {
            instance = new ScanFRCC();
        }
        return instance;
    }

    private ScanFRCC() {
        super("Scan-fr", "https://www.scan-fr.cc/");
    }
}
