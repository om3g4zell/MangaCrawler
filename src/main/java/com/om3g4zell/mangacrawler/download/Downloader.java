package com.om3g4zell.mangacrawler.download;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.om3g4zell.mangacrawler.entities.Chapter;
import com.om3g4zell.mangacrawler.entities.Manga;
import me.tongfei.progressbar.ProgressBar;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Downloader {

    private Downloader() {
        throw new IllegalStateException("Private ctor");
    }

    private static final Logger logger = LogManager.getLogger(Downloader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(Duration.ofSeconds(30))
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(true)
            .retryOnConnectionFailure(true)
            .build();


    public static void saveTree(Manga m, Path folderName) throws IOException {
        // TODO mutualise
        var folderPath = buildFolderPath(m, folderName);
        logger.atInfo()
                .log("Saving tree to {}", folderPath);
        m = m.withChapters(m.chapters().stream()
                .sorted(Comparator.comparing(Chapter::number))
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new))
        );
        var content = objectMapper.writeValueAsString(m);
        var fileName = String.join(".", sanitizePath(m.name()), "json");
        var file = new File(folderPath.toString());
        file.mkdirs();
        var treefile = new File(Path.of(folderPath.toString(), fileName).toString());
        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(content.getBytes()), treefile);
        logger.atInfo()
                .log("Tree saved");
    }

    @Nullable
    public static Manga readTree(Path folderName, String mangaName, String sourceSite) {
        var folderPath = buildFolderPath(Manga.builder()
                .name(mangaName)
                .sourceWebSiteName(sourceSite)
                .chapters(new ConcurrentLinkedQueue<>())
                .url("").build(), folderName);
        var file = new File(Path.of(folderPath.toString(), sanitizePath(mangaName) + ".json").toString());
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, Manga.class);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
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
        try (ProgressBar pb = PersonalizedProgressBar.progressBar("downloading " + m.name(), atomicInteger.get())) {
            m.chapters().forEach(chapter -> {
                var chapterFolder = "unknown";
                var chapterName = sanitizePath(chapter.name());
                if (chapterName.isBlank() || chapterName.isEmpty()) {
                    chapterFolder = toPrettyString(chapter.number());
                } else {
                    chapterFolder = String.join("-", toPrettyString(chapter.number()), chapterName);
                }

                var chapterPath = Paths.get(mangaPath.toString(), chapterFolder);
                var chapterDirectory = new File(chapterPath.toString());
                chapterDirectory.mkdirs();
                chapter.pages().forEach(page -> {
                    try {
                        var url = page.imageUrl();
                        var extension = url.substring(url.lastIndexOf(".") + 1);
                        var pageFile = new File(chapterPath.toString(), toPrettyString(page.number()) + "." + extension);

                        if (!pageFile.exists()) {

                            var request = new Request.Builder()
                                    .addHeader("User-Agent", USER_AGENT)
                                    .url(url)
                                    .get()
                                    .build();

                            var call = client.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    logger.atError()
                                            .withThrowable(e)
                                            .log("Couldn't access to {}", page.imageUrl());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    var body = response.body();

                                    if (body != null) {
                                        var stream = body.byteStream();
                                        FileUtils.copyInputStreamToFile(stream, pageFile);
                                    }
                                    pb.step();
                                    pb.setExtraMessage("saved " + pageFile.toString());
                                }
                            });
                        }
                        else {
                            pb.step();
                        }
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
        return Paths.get(folderName.toString(), manga.sourceWebSiteName(), sanitizePath(manga.name()));
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
