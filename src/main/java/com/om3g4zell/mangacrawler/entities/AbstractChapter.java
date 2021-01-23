package com.om3g4zell.mangacrawler.entities;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AbstractChapter {

    abstract double number();

    abstract String name();

    abstract String url();

    abstract List<Page> pages();
}
