package com.local.rag.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Удаляет дубликаты чанков по MD5-хешу содержимого.
 */
@Component
public class Deduplicator {

    /**
     * Возвращает список чанков без повторяющегося содержимого (порядок сохраняется).
     *
     * @param chunks исходные чанки
     * @return список уникальных чанков
     * @throws IllegalStateException если MD5 недоступен в JRE
     */
    public List<String> unique(List<String> chunks) {
        Set<String> seenHashes = new HashSet<>();
        List<String> result = new ArrayList<>();
        for (String chunk : chunks) {
            String hash = md5(chunk);
            if (seenHashes.add(hash)) {
                result.add(chunk);
            }
        }
        return result;
    }

    private String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is unavailable", e);
        }
    }
}
