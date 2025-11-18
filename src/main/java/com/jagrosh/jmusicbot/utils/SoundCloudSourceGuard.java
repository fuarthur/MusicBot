package com.jagrosh.jmusicbot.utils;

import java.util.Locale;

public final class SoundCloudSourceGuard {
    public static final String SOUNDCLOUD_SEARCH_PREFIX = "scsearch:";

    private SoundCloudSourceGuard() {}

    public static boolean isUrl(String input) {
        if(input == null)
            return false;
        String trimmed = input.trim();
        return trimmed.regionMatches(true, 0, "http://", 0, 7)
                || trimmed.regionMatches(true, 0, "https://", 0, 8);
    }

    public static boolean isSoundCloudUrl(String input) {
        if(input == null)
            return false;
        String lower = input.toLowerCase(Locale.ROOT);
        return lower.contains("soundcloud.com")
                || lower.contains("sndcdn.com")
                || lower.contains("soundcloud.app.goo.gl")
                || lower.contains("soundcloud.page.link");
    }

    public static boolean isYouTubeUrl(String input) {
        if(input == null)
            return false;
        String lower = input.toLowerCase(Locale.ROOT);
        return lower.contains("youtube.com") || lower.contains("youtu.be");
    }

    public static boolean isBlockedSource(String input) {
        return isUrl(input) && !isSoundCloudUrl(input);
    }

    public static String prepareIdentifier(String input) {
        if(input == null)
            return null;
        String trimmed = input.trim();
        if(trimmed.isEmpty())
            return null;
        if(trimmed.startsWith(SOUNDCLOUD_SEARCH_PREFIX))
            return trimmed;
        if(isUrl(trimmed))
            return isSoundCloudUrl(trimmed) ? trimmed : null;
        return SOUNDCLOUD_SEARCH_PREFIX + trimmed;
    }

    public static boolean isSearchIdentifier(String identifier) {
        return identifier != null && identifier.startsWith(SOUNDCLOUD_SEARCH_PREFIX);
    }
}
