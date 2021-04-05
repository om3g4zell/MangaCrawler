package com.om3g4zell.mangacrawler.download;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class PersonalizedProgressBar {

    public static ProgressBar progressBar(String name, int size) {
        var builder = new ProgressBarBuilder();
        return builder.setTaskName(name)
                .setInitialMax(size)
                .setStyle(ProgressBarStyle.ASCII)
                .setMaxRenderedLength(200)
                .showSpeed()
                .build();
    }
}
