package com.codefactory.urlshortener.utils;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public class Shortener {

    public static String shortenUrl(String url) { // TODO: maybe use DI to inject the hasher in case we want to change it later
        byte[] bytes = Hashing.murmur3_32()
                .hashString(url, StandardCharsets.UTF_8)
                .asBytes();

        return BaseEncoding.base64Url().omitPadding().encode(bytes);
    }
}
