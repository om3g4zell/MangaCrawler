package com.om3g4zell.entities;

import org.immutables.value.Value;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS) // Make it class retention for incremental compilation
@Value.Style(
        typeImmutable = "*", // No prefix or suffix for generated immutable type
        visibility = Value.Style.ImplementationVisibility.PUBLIC) // Disable copy methods by default
public @interface MangaCrawlerImmutableStyle {
}
