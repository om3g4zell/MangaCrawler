package com.om3g4zell.mangacrawler.entities;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AbstractManga {

    abstract String sourceWebSiteName();

    abstract String name();

    abstract String url();

    abstract List<Chapter> chapters();
}
