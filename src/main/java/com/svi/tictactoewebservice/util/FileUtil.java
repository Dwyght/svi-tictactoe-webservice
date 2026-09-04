package com.svi.tictactoewebservice.util;

import com.svi.tictactoewebservice.config.Config;
import com.svi.tictactoewebservice.exception.InvalidRecordIdException;

import java.io.File;

public final class FileUtil {

    private FileUtil() {
    }

    public static File getPlayersDirectory() {
        return getOrCreateDirectory(
                new File(Config.PLAYERS_DIRECTORY.getValue())
        );
    }

    public static File getRoomsDirectory() {
        return getOrCreateDirectory(
                new File(Config.ROOMS_DIRECTORY.getValue())
        );
    }

    public static File getGamesDirectory() {
        return getOrCreateDirectory(
                new File(Config.GAMES_DIRECTORY.getValue())
        );
    }

    public static File getPlayerFile(String playerId) {
        validateRecordId(playerId);
        return getRecordFile(getPlayersDirectory(), playerId);
    }

    public static File getRoomFile(String roomId) {
        validateRecordId(roomId);
        return getRecordFile(getRoomsDirectory(), roomId);
    }

    public static File getGameFile(String gameId) {
        validateRecordId(gameId);
        return getRecordFile(getGamesDirectory(), gameId);
    }

    private static File getRecordFile(File directory, String recordId) {
        return new File(directory, recordId + ".txt");
    }

    private static void validateRecordId(String recordId) {
        if (recordId == null) {
            throw new InvalidRecordIdException("Record ID must not be null.");
        }

        if (recordId.contains("/")
                || recordId.contains("\\")
                || recordId.contains("..")
                || recordId.indexOf('\0') >= 0) {
            throw new InvalidRecordIdException(
                    "Record ID contains unsafe path characters.");
        }
    }

    private static File getOrCreateDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }
}
