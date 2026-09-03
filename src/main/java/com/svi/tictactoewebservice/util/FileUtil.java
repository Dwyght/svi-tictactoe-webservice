package com.svi.tictactoewebservice.util;

import com.svi.tictactoewebservice.config.Config;

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
