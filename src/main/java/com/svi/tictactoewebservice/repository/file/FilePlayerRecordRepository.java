package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import com.svi.tictactoewebservice.util.FileUtil;

import javax.enterprise.context.ApplicationScoped;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class FilePlayerRecordRepository implements PlayerRecordRepository {

    private static final Logger LOGGER =
            Logger.getLogger(FilePlayerRecordRepository.class.getName());

    /**
     * Maintains one lock per player ID so unrelated player files remain independent while the
     * read-check-append sequence for one flat file cannot race with a concurrent writer.
     */
    private final ConcurrentHashMap<String, Object> fileLocks = new ConcurrentHashMap<>();

    @Override
    public void saveGameId(String playerId, String gameId) {
        synchronized (getFileLock(playerId)) {
            List<String> gameIds = readGameIds(playerId);

            if (gameIds.contains(gameId)) {
                return;
            }

            File file = FileUtil.getPlayerFile(playerId);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(gameId);
                writer.newLine();
            } catch (IOException e) {
                LOGGER.log(
                        Level.SEVERE,
                        "Failed to append game ID to player record '"
                                + file.getAbsolutePath() + "'.",
                        e);
                throw new RuntimeException("Could not save player record.", e);
            }
        }
    }

    @Override
    public List<String> findGameIdsByPlayerId(String playerId) {
        synchronized (getFileLock(playerId)) {
            return readGameIds(playerId);
        }
    }

    @Override
    public boolean existsByPlayerId(String playerId) {
        synchronized (getFileLock(playerId)) {
            return FileUtil.getPlayerFile(playerId).exists();
        }
    }

    private List<String> readGameIds(String playerId) {
        File file = FileUtil.getPlayerFile(playerId);
        List<String> gameIds = new ArrayList<>();

        if (!file.exists()) {
            return gameIds;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                gameIds.add(line);
            }
        } catch (IOException e) {
            LOGGER.log(
                    Level.WARNING,
                    "Failed to read game IDs from player record '"
                            + file.getAbsolutePath() + "'.",
                    e);
            throw new RuntimeException("Could not read player records.", e);
        }

        return gameIds;
    }

    /**
     * Returns the shared monitor for a player ID; atomic map initialization ensures every
     * thread targeting that player's file synchronizes on the same object.
     */
    private Object getFileLock(String playerId) {
        return fileLocks.computeIfAbsent(playerId, key -> new Object());
    }
}
