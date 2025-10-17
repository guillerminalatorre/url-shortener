package com.codefactory.urlshortener.shortener;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UrlHasherFactory {

    private final Map<String, UrlHasher> hashers = new HashMap<>();

    public UrlHasherFactory(List<UrlHasher> availableHashers) {
        for (UrlHasher hasher : availableHashers) {
            hashers.put(hasher.getPrefix(), hasher);
        }
    }

    public UrlHasher getByPrefix(String prefix) {
        UrlHasher hasher = hashers.get(prefix);
        if (hasher == null) {
            throw new IllegalArgumentException("Unknown hasher prefix: " + prefix);
        }
        return hasher;
    }
}

