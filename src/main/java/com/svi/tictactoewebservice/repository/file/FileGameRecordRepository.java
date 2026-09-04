package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.model.Game;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.util.FileUtil;

import javax.enterprise.context.ApplicationScoped;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class FileGameRecordRepository implements GameRecordRepository {

    private static final Logger LOGGER =
            Logger.getLogger(FileGameRecordRepository.class.getName());

    private final ConcurrentHashMap<String, Object> fileLocks = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        String gameId = game.getGameid();

        synchronized (getFileLock(gameId)) {
            File file = FileUtil.getGameFile(gameId);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(toLine(game));
                writer.newLine();
            } catch (IOException e) {
                LOGGER.log(
                        Level.SEVERE,
                        "Failed to append game record to '"
                                + file.getAbsolutePath() + "'.",
                        e);
                throw new RuntimeException("Could not save game record.", e);
            }
        }
    }

    @Override
    public List<String> findAllGameIds() {
        File directory = FileUtil.getGamesDirectory();
        File[] recordFiles = directory.listFiles(
                file -> file.isFile() && file.getName().endsWith(".txt"));

        List<String> gameIds = new ArrayList<>();

        if (recordFiles == null) {
            return gameIds;
        }

        Arrays.sort(recordFiles, Comparator.comparing(File::getName));

        for (File file : recordFiles) {
            String gameId = findGameId(file);

            if (gameId != null) {
                gameIds.add(gameId);
            }
        }

        return gameIds;
    }

    @Override
    public List<Game> findByGameId(String gameId) {
        synchronized (getFileLock(gameId)) {
            File file = FileUtil.getGameFile(gameId);
            List<Game> games = new ArrayList<>();

            if (!file.exists()) {
                return games;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    games.add(toGame(line));
                }
            } catch (IOException e) {
                LOGGER.log(
                        Level.WARNING,
                        "Failed to read game records from '"
                                + file.getAbsolutePath() + "'.",
                        e);
                throw new RuntimeException("Could not read game records.", e);
            }

            return games;
        }
    }

    @Override
    public boolean existsByGameId(String gameId) {
        synchronized (getFileLock(gameId)) {
            return FileUtil.getGameFile(gameId).exists();
        }
    }

    private String toLine(Game game) {
        return game.getGameid() + ","
                + game.getPlayerid() + ","
                + game.getSymbol() + ","
                + game.getLocation() + ","
                + game.getDatesave();
    }

    private String findGameId(File file) {
        String fileName = file.getName();
        String expectedGameId = fileName.substring(0, fileName.length() - ".txt".length());

        synchronized (getFileLock(expectedGameId)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String firstLine = reader.readLine();

                if (firstLine == null || firstLine.trim().isEmpty()) {
                    LOGGER.log(
                            Level.WARNING,
                            "Game record file '" + fileName
                                    + "' is empty and will be excluded.");
                    return null;
                }

                String[] values = firstLine.split(",", -1);

                if (!expectedGameId.equals(values[0])) {
                    LOGGER.log(
                            Level.SEVERE,
                            "Game record file '" + fileName
                                    + "' does not start with the expected game ID '"
                                    + expectedGameId + "'. Actual first line: " + firstLine);
                    return null;
                }

                if (values.length != 5) {
                    LOGGER.log(
                            Level.SEVERE,
                            "Game record file '" + fileName
                                    + "' has a malformed first line: " + firstLine);
                    return null;
                }

                return expectedGameId;
            } catch (IOException e) {
                LOGGER.log(
                        Level.WARNING,
                        "Failed to read and validate game record file '"
                                + file.getAbsolutePath() + "'.",
                        e);
                throw new RuntimeException("Could not read game records.", e);
            }
        }
    }

    private Game toGame(String line) {
        String[] values = line.split(",", -1);
        return new Game(values[0], values[1], values[2], values[3], values[4]);
    }

    private Object getFileLock(String gameId) {
        return fileLocks.computeIfAbsent(gameId, key -> new Object());
    }
}
