package com.om3g4zell.mangacrawler.sites.exceptions;

public class ThirdPartyCallFailedException extends RuntimeException {

    public ThirdPartyCallFailedException(String error) {
        super(error);
    }
}
