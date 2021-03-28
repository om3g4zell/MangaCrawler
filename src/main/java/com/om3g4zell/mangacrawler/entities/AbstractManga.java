package com.om3g4zell.mangacrawler.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Collection;

@Value.Immutable
@JsonDeserialize(as = Manga.class)
public abstract class AbstractManga {

    abstract String sourceWebSiteName();

    abstract String name();

    abstract String url();

    abstract Collection<Chapter> chapters();
}
