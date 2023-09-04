package com.simpleclouddisk.utils;

import java.util.HashSet;
import java.util.Set;

public class FileTypeUtil {
    public static final Set<String> imageExtensions = new HashSet<>();
    public static final Set<String> videoExtensions = new HashSet<>();
    public static final Set<String> musicExtensions = new HashSet<>();
    public static final Set<String> documentExtensions = new HashSet<>();

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

    static {
        // 添加常见文档类型的扩展名
        documentExtensions.add(".pdf");
        documentExtensions.add(".doc");
        documentExtensions.add(".docx");
        documentExtensions.add(".ppt");
        documentExtensions.add(".pptx");
        documentExtensions.add(".xls");
        documentExtensions.add(".xlsx");
        documentExtensions.add(".txt");
        documentExtensions.add(".rtf");
        documentExtensions.add(".csv");
        documentExtensions.add(".html");
        documentExtensions.add(".xml");
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

    public static boolean isDocumentFile(String fileName) {
        if (fileName.length() > 1) {
            String extension = fileName.toLowerCase();
            return documentExtensions.contains(extension);
        }
        return false;
    }

}
