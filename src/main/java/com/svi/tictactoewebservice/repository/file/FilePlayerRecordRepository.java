package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import com.svi.tictactoewebservice.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilePlayerRecordRepository implements PlayerRecordRepository {

    @Override
    public void saveGameId(String playerId, String gameId) {
        File file = FileUtil.getRecordFile(playerId);

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, true))) {

            writer.write(gameId);
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not save player record.", e);
        }
    }

    @Override
    public List<String> findGameIdsByPlayerId(String playerId) {
        File file = FileUtil.getRecordFile(playerId);
        List<String> gameIds = new ArrayList<>();

        if (!file.exists()) {
            return gameIds;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                gameIds.add(line);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not read player records.", e);
        }

        return gameIds;
    }

    @Override
    public boolean existsByPlayerId(String playerId) {
        return FileUtil.getRecordFile(playerId).exists();
    }
}