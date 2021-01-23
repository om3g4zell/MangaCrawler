package com.om3g4zell.mangacrawler.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = Chapter.class)
public abstract class AbstractChapter {

    abstract double number();

    abstract String name();

    abstract String url();

    abstract List<Page> pages();
}
