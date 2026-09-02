package com.svi.tictactoewebservice.util;

import com.svi.tictactoewebservice.config.Config;

import java.io.File;

public final class FileUtil {

    private static final String PLAYERS_DIRECTORY = "players";
    private static final String ROOMS_DIRECTORY = "rooms";
    private static final String GAMES_DIRECTORY = "games";

    private FileUtil() {
    }

    public static File getRecordsDirectory() {
        return getOrCreateDirectory(
                new File(Config.RECORDS_DIRECTORY.getValue())
        );
    }

    public static File getPlayersDirectory() {
        return getOrCreateDirectory(
                new File(getRecordsDirectory(), PLAYERS_DIRECTORY)
        );
    }

    public static File getRoomsDirectory() {
        return getOrCreateDirectory(
                new File(getRecordsDirectory(), ROOMS_DIRECTORY)
        );
    }

    public static File getGamesDirectory() {
        return getOrCreateDirectory(
                new File(getRecordsDirectory(), GAMES_DIRECTORY)
        );
    }

    public static File getPlayerFile(String playerId) {
        return new File(getPlayersDirectory(), playerId + ".txt");
    }

    public static File getRoomFile(String roomId) {
        return new File(getRoomsDirectory(), roomId + ".txt");
    }

    public static File getGameFile(String gameId) {
        return new File(getGamesDirectory(), gameId + ".txt");
    }

    private static File getOrCreateDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }
}
