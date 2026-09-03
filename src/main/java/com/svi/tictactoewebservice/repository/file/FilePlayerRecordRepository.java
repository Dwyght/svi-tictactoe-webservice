package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import com.svi.tictactoewebservice.util.FileUtil;

import javax.enterprise.context.ApplicationScoped;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class FilePlayerRecordRepository implements PlayerRecordRepository {

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
            throw new RuntimeException("Could not read player records.", e);
        }

        return gameIds;
    }

    private Object getFileLock(String playerId) {
        return fileLocks.computeIfAbsent(playerId, key -> new Object());
    }
}
