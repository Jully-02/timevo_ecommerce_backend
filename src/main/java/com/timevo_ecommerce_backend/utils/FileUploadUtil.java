package com.timevo_ecommerce_backend.utils;

import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.io.FilenameUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUploadUtil {
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    public static boolean isAllowedExtension (String fileName, String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed (MultipartFile file, String pattern) throws Exception {
        long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new Exception("Max file size is 10MB");
        }
        String fileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        if (!isAllowedExtension(fileName, pattern)) {
            throw new Exception("Only jpg, png, gif, bmp files are allowed");
        }
    }

    public static String getFileName (String name) {
        return UUID.randomUUID().toString() + "_" + name;
    }
}
