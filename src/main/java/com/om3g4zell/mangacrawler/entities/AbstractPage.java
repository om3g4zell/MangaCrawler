package com.om3g4zell.mangacrawler.entities;

import org.immutables.value.Value;

@Value.Immutable
public abstract class AbstractPage {

    abstract int number();

    abstract String imageUrl();
}
