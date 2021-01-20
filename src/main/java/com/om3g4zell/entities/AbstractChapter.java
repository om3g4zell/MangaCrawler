package com.om3g4zell.entities;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AbstractChapter {

    abstract String number();

    abstract String name();

    abstract String url();

    abstract List<Page> pages();
}
