package com.svi.tictactoewebservice.util;

import java.io.File;

public final class FileUtil {

    private static final String RECORDS_DIRECTORY = "records";

    private FileUtil() {
    }

    public static File getRecordsDirectory() {
        File directory = new File(RECORDS_DIRECTORY);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }

    public static File getRecordFile(String id) {
        return new File(getRecordsDirectory(), id + ".txt");
    }
}