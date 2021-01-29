package com.om3g4zell.mangacrawler.download;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Downloader {

    private static final Logger logger = LogManager.getLogger(Downloader.class);
    private static final ObjectMapper objectMapper =  new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    public static void saveTree(Manga m, Path folderName) throws IOException {
        // TODO mutualise
        var folderPath =  buildFolderPath(m, folderName);
        logger.atInfo()
                .log("Saving tree to {}", folderPath);
        m = m.withChapters(m.chapters().stream().sorted(Comparator.comparing(Chapter::number)).collect(Collectors.toList()));
        var content = objectMapper.writeValueAsString(m);
        var fileName = String.join(".", m.name(), "json");
        var file = new File(folderPath.toString());
        file.mkdirs();
        var treefile = new File(Path.of(folderPath.toString(), fileName).toString());
        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(content.getBytes()), treefile );
        logger.atInfo()
                .log("Tree saved");
    }

    @Nullable
    public static Manga readTree(Path folderName, Manga manga) throws IOException {
        var folderPath = buildFolderPath(manga, folderName);
        var file = new File(Path.of(folderPath.toString(), manga.name() + ".json").toString());
        if(file.exists()) {
            return objectMapper.readValue(file, Manga.class);
        }
        return null;
    }

    public static void download(Manga m, Path path) {
        var mangaPath = buildFolderPath(m, path);
        var atomicInteger = new AtomicInteger(0);
        m.chapters().forEach(
                chapter -> chapter.pages().forEach(page -> {
                    atomicInteger.incrementAndGet();
                })
        );
        try (ProgressBar pb = new ProgressBar("downloading " + m.name(), atomicInteger.get(), 10, System.out, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO)) {
            m.chapters().forEach(chapter -> {
                var chapterFolder = "unknown";
                var chapterName = sanitizePath(chapter.name());
                if (chapterName.isBlank() ||chapterName.isEmpty()) {
                    chapterFolder = toPrettyString(chapter.number());
                } else {
                    chapterFolder = String.join("-", toPrettyString(chapter.number()), chapterName);
                }

                var chapterPath = Paths.get(mangaPath.toString(), chapterFolder);
                var chapterDirectory = new File(chapterPath.toString());
                chapterDirectory.mkdirs();
                chapter.pages().parallelStream().forEach(page -> {
                    try {
                        var url = page.imageUrl();
                        var extension = url.substring(url.lastIndexOf(".") + 1);
                        var pageFile = new File(chapterPath.toString(), toPrettyString(page.number()) + "." + extension);

                        var isBlank = (pageFile.exists() && FileUtils.sizeOf(pageFile) == 0);
                        if (!pageFile.exists() || isBlank) {
                            var httpClient = HttpClient.newBuilder()
                                    .followRedirects(HttpClient.Redirect.ALWAYS)
                                    .build();
                            var request = HttpRequest.newBuilder(URI.create(url))
                                    .GET()
                                    .build();
                            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

                            FileUtils.copyInputStreamToFile(response.body(), pageFile);
                            if(isBlank && FileUtils.sizeOf(pageFile) != 0) {
                                logger.info("Has been fixed !");
                            }
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

    private static Path buildFolderPath(Manga manga, Path folderName) {
        return Paths.get(folderName.toString(), manga.sourceWebSiteName(), manga.name());
    }

    private static String toPrettyString(double number) {
        if (number == (long) number)
            return String.format("%d", (long) number);
        else
            return String.format("%s", number);
    }

    private static String sanitizePath(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|.]", "").trim();
    }

}
