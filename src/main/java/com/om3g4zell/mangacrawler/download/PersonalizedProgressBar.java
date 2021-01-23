package com.om3g4zell.mangacrawler.download;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class PersonalizedProgressBar {

    public static ProgressBar progressBar(String name, int size) {
        return new ProgressBar(name, size, 10, System.out, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO);
    }
}
