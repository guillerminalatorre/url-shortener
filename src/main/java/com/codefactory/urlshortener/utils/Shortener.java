package com.codefactory.urlshortener.utils;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public class Shortener {

    public static String shortenUrl(String url) {
        byte[] bytes = Hashing.murmur3_32()
                .hashString(url, StandardCharsets.UTF_8)
                .asBytes();

        return BaseEncoding.base64Url().omitPadding().encode(bytes);
    }
}
