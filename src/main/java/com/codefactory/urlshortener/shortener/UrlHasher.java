package com.codefactory.urlshortener.shortener;

public interface UrlHasher {
    public String getPrefix();
    public String hash(String url);
}
