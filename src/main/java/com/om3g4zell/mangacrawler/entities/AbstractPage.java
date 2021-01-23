package com.om3g4zell.mangacrawler.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = Page.class)
public abstract class AbstractPage {

    abstract double number();

    abstract String imageUrl();
}
