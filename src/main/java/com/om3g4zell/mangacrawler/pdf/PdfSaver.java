package com.om3g4zell.mangacrawler.pdf;

import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

public class PdfSaver {
    private static final Logger logger = LogManager.getLogger(PdfSaver.class);

    public static void save(Path path, int chapterPerBook) {
        var mangaFolder = new File(path.toString());
        var maybeFiles = mangaFolder.listFiles();
        if (maybeFiles == null) {
            logger.atWarn()
                    .log("The supplied folder is empty {}", path);
            return;
        }

        try (var pdf = new Document()) {
            PdfWriter.getInstance(pdf, new FileOutputStream(String.format("%s%s%s.pdf", path, File.separator, path.getFileName().toString())));
            pdf.open();
            var files = Arrays.asList(maybeFiles);
            files.stream().filter(File::isDirectory).sorted(fileComparator())
                    .forEachOrdered(chapterFolder -> {
                        try {
                            var maybeImages = chapterFolder.listFiles();
                            if (maybeImages == null) {
                                return;
                            }
                            var images = Arrays.asList(maybeImages);
                            images.stream().sorted(fileComparator()).forEachOrdered(image -> {
                                try {
                                    Image jpg = Image.getInstance(image.getAbsolutePath());
                                    jpg.setAbsolutePosition(0, 0);
                                    pdf.setPageSize(jpg);
                                    pdf.newPage();
                                    pdf.add(jpg);
                                } catch (Exception e) {
                                    logger.atError()
                                            .withThrowable(e)
                                            .log("Error while generating page {} of folder {}", image, chapterFolder);
                                }
                            });
                        } catch (Exception e) {
                            logger.atError()
                                    .withThrowable(e)
                                    .log("Error while generating chapter {}", chapterFolder);
                        }
                    });
        } catch (Exception e) {
            logger.atError()
                    .withThrowable(e)
                    .log("Error while generating pdf");
        }
    }

    private static Comparator<File> fileComparator() {
        return (o1, o2) -> {
            var f1Name = o1.getName();
            if (f1Name.contains("-")) {
                f1Name = f1Name.substring(0, f1Name.indexOf("-"));
            } else if (f1Name.contains(".")) {
                f1Name = f1Name.substring(0, f1Name.indexOf("."));
            }
            var f2Name = o2.getName();
            if (f2Name.contains("-")) {
                f2Name = f2Name.substring(0, f2Name.indexOf("-"));
            } else if (f2Name.contains(".")) {
                f2Name = f2Name.substring(0, f2Name.indexOf("."));
            }

            var f1 = Double.parseDouble(f1Name);
            var f2 = Double.parseDouble(f2Name);

            return Double.compare(f1, f2);
        };
    }
}
