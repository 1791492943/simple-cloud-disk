package com.simpleclouddisk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class FileTypeUtil {
    public static final Set<String> imageExtensions = new HashSet<>();
    public static final Set<String> videoExtensions = new HashSet<>();

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

    public static double getVideoDuration(String videoPath) {
        String ffprobeCommand = String.format("ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 %s", videoPath);

        try {
            Process process = Runtime.getRuntime().exec(ffprobeCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String durationStr = reader.readLine();
            process.waitFor();
            return Double.parseDouble(durationStr);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
