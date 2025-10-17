package com.codefactory.urlshortener.shortener;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Data
@Component
public class Base32UrlHasher implements UrlHasher{

    @Value("${app.url.prefixes.base32}")
    private String prefix;

    @Override
    public String hash(String url) {
        byte[] bytes = Hashing.murmur3_32()
                .hashString(url, StandardCharsets.UTF_8)
                .asBytes();

        return BaseEncoding.base64Url().omitPadding().encode(bytes);
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}
