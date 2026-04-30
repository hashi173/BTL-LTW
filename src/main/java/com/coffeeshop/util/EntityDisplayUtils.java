package com.coffeeshop.util;

import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public final class EntityDisplayUtils {

    private EntityDisplayUtils() {
    }

    public static String buildReadableCode(String prefix, String source, UUID fallbackId) {
        String token = normalizeCodeToken(source, 40);
        if (!StringUtils.hasText(token) && fallbackId != null) {
            token = fallbackId.toString().substring(0, 8).toUpperCase(Locale.ROOT);
        }
        if (!StringUtils.hasText(token)) {
            token = "DRAFT";
        }
        return prefix + "-" + token;
    }

    public static String normalizeCodeToken(String source, int maxLength) {
        if (!StringUtils.hasText(source)) {
            return "";
        }

        String normalized = Normalizer.normalize(source, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('\u0111', 'd')
                .replace('\u0110', 'D')
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "")
                .replaceAll("-{2,}", "-");

        if (!StringUtils.hasText(normalized)) {
            return "";
        }

        if (normalized.length() <= maxLength) {
            return normalized;
        }

        String shortened = normalized.substring(0, maxLength).replaceAll("-+$", "");
        return StringUtils.hasText(shortened) ? shortened : normalized.substring(0, maxLength);
    }

    public static String resolveProductImagePath(String image) {
        if (!StringUtils.hasText(image)) {
            return "/images/no-image.png";
        }

        String trimmed = image.trim();
        if (trimmed.startsWith("http://")
                || trimmed.startsWith("https://")
                || trimmed.startsWith("/uploads/")
                || trimmed.startsWith("/images/")) {
            return trimmed;
        }
        if (trimmed.startsWith("uploads/") || trimmed.startsWith("images/")) {
            return "/" + trimmed;
        }
        return "/images/products/" + trimmed;
    }
}
