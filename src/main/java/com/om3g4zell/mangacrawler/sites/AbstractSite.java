package com.om3g4zell.mangacrawler.sites;

import com.om3g4zell.mangacrawler.sites.exceptions.ThirdPartyCallFailedException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.Duration;

public abstract class AbstractSite implements Site {

    private static final Logger logger = LogManager.getLogger(Site.class);

    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    protected static final RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(50)
            .waitDuration(Duration.ofSeconds(5))
            .retryExceptions(ThirdPartyCallFailedException.class)
            .build();

    protected String url;

    protected String name;

    public AbstractSite(String name, String url) {
        this.url = url;
        this.name = name;
    }

    protected Document getDocument(String url) throws ThirdPartyCallFailedException {
        Retry retry = Retry.of(String.join("-", "retry", name), retryConfig);
        retry.getEventPublisher().onRetry(event -> logger.error("Couldn't access to {} let's retry", url));
        try {
            return retry.executeCheckedSupplier(() -> {
                try {
                    return Jsoup.connect(url)
                            .userAgent(USER_AGENT)
                            .get();
                } catch (Exception e) {
                    throw new ThirdPartyCallFailedException("Error while fetching" + url);
                }
            });
        } catch (Throwable throwable) {
            throw new ThirdPartyCallFailedException("Couldn't access to " + url + " even after retries");
        }
    }

}
