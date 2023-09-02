package com.simpleclouddisk.utils;

import java.util.HashSet;
import java.util.Set;

public class FileTypeUtil {
    public static final Set<String> imageExtensions = new HashSet<>();
    public static final Set<String> videoExtensions = new HashSet<>();
    public static final Set<String> musicExtensions = new HashSet<>();

    static {
        imageExtensions.add(".jpg");
        imageExtensions.add(".jpeg");
        imageExtensions.add(".png");
        imageExtensions.add(".gif");
        imageExtensions.add(".bmp");
        imageExtensions.add(".webp");
    }

    static {
        videoExtensions.add(".mp4");
        videoExtensions.add(".avi");
        videoExtensions.add(".mkv");
        videoExtensions.add(".mov");
        videoExtensions.add(".wmv");
        videoExtensions.add(".flv");
        videoExtensions.add(".mpeg");
        videoExtensions.add(".webm");
        videoExtensions.add(".3gp");
    }

    static {
        musicExtensions.add(".mp3");
        musicExtensions.add(".wav");
        musicExtensions.add(".ogg");
        musicExtensions.add(".flac");
        musicExtensions.add(".aac");
        musicExtensions.add(".wma");
        musicExtensions.add(".m4a");
        musicExtensions.add(".opus");
        musicExtensions.add(".mid");
        musicExtensions.add(".midi");
        musicExtensions.add(".amr");
        musicExtensions.add(".ac3");
        musicExtensions.add(".ape");
    }

    public static boolean isImageFile(String fileName) {
        if (fileName.length() > 1) {
            String extension = fileName.toLowerCase();
            return imageExtensions.contains(extension);
        }
        return false;
    }

    public static boolean isVideoFile(String fileName) {
        if (fileName.length() > 1 ) {
            String extension = fileName.toLowerCase();
            return videoExtensions.contains(extension);
        }
        return false;
    }

    public static boolean isMusicFile(String fileName) {
        if (fileName.length() > 1) {
            String extension = fileName.toLowerCase();
            return musicExtensions.contains(extension);
        }
        return false;
    }

}
