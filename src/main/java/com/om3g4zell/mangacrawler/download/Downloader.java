package com.om3g4zell.mangacrawler.download;

import com.om3g4zell.mangacrawler.entities.Manga;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Downloader {

    private static final Logger logger = LogManager.getLogger(Downloader.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    public static void download(Manga m, Path path) {
        var mangaPath = Paths.get(path.toString(), m.name());
        var atomicInteger = new AtomicInteger(0);
        m.chapters().forEach(
                chapter -> chapter.pages().forEach(page -> {
                    atomicInteger.incrementAndGet();
                })
        );
        try (ProgressBar pb = new ProgressBar("downloading " + m.name(), atomicInteger.get(), 10, System.out, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO)) {
            m.chapters().parallelStream().forEach(chapter -> {
                var chapterFolder = "unknown";
                if(chapter.name().isBlank() || chapter.name().isEmpty()) {
                    chapterFolder = chapter.number();
                }
                else {
                    chapterFolder = String.join("-", chapter.number(), chapter.name());
                }

                var chapterPath = Paths.get(mangaPath.toString(), chapterFolder);
                var chapterDirectory = new File(chapterPath.toString());
                chapterDirectory.mkdirs();
                chapter.pages().parallelStream().forEach(page -> {
                    try {
                        var url = page.imageUrl();
                        var extension = url.substring(url.lastIndexOf(".") + 1);
                        var pageFile = new File(chapterPath.toString(), page.number() + "." + extension);

                        if (!pageFile.exists()) {
                            URLConnection connection = new URL(url).openConnection();
                            connection
                                    .setRequestProperty("User-Agent", USER_AGENT);
                            connection.connect();
                            InputStream stream = connection.getInputStream();
                            FileUtils.copyInputStreamToFile(stream, pageFile);
                        }
                        pb.step();
                        pb.setExtraMessage("Saved " + pageFile.toString());
                    } catch (Exception e) {
                        logger.atError()
                                .withThrowable(e)
                                .log("Couldn't access to {}", page.imageUrl());
                    }
                });

            });
        }
    }

}
