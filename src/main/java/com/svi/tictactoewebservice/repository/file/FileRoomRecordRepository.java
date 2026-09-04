package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.repository.RoomRecordRepository;
import com.svi.tictactoewebservice.util.FileUtil;

import javax.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class FileRoomRecordRepository implements RoomRecordRepository {

    private static final Logger LOGGER =
            Logger.getLogger(FileRoomRecordRepository.class.getName());

    /**
     * Maintains one lock per room ID so unrelated room files remain independent while the
     * read-check-append sequence for one flat file cannot race with a concurrent writer.
     */
    private final ConcurrentHashMap<String, Object> fileLocks = new ConcurrentHashMap<>();

    @Override
    public void saveGameId(String roomId, String gameId) {
        synchronized (getFileLock(roomId)) {
            List<String> gameIds = readGameIds(roomId);

            if (gameIds.contains(gameId)) {
                return;
            }

            File file = FileUtil.getRoomFile(roomId);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(gameId);
                writer.newLine();
            } catch (IOException e) {
                LOGGER.log(
                        Level.SEVERE,
                        "Failed to append game ID to room record '"
                                + file.getAbsolutePath() + "'.",
                        e);
                throw new RuntimeException("Could not save room record.", e);
            }
        }
    }

    @Override
    public List<String> findAllRoomIds() {
        File directory = FileUtil.getRoomsDirectory();
        File[] recordFiles = directory.listFiles(
                file -> file.isFile() && file.getName().endsWith(".txt"));

        List<String> roomIds = new ArrayList<>();

        if (recordFiles == null) {
            return roomIds;
        }

        Arrays.sort(recordFiles, Comparator.comparing(File::getName));

        for (File file : recordFiles) {
            String fileName = file.getName();
            roomIds.add(fileName.substring(0, fileName.length() - ".txt".length()));
        }

        return roomIds;
    }

    @Override
    public List<String> findGameIdsByRoomId(String roomId) {
        synchronized (getFileLock(roomId)) {
            return readGameIds(roomId);
        }
    }

    @Override
    public boolean existsByRoomId(String roomId) {
        synchronized (getFileLock(roomId)) {
            return FileUtil.getRoomFile(roomId).exists();
        }
    }

    private List<String> readGameIds(String roomId) {
        File file = FileUtil.getRoomFile(roomId);
        List<String> gameIds = new ArrayList<>();

        if (!file.exists()) {
            return gameIds;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    gameIds.add(line);
                }
            }
        } catch (IOException e) {
            LOGGER.log(
                    Level.WARNING,
                    "Failed to read game IDs from room record '"
                            + file.getAbsolutePath() + "'.",
                    e);
            throw new RuntimeException("Could not read room records.", e);
        }

        return gameIds;
    }

    /**
     * Returns the shared monitor for a room ID; atomic map initialization ensures every thread
     * targeting that room's file synchronizes on the same object.
     */
    private Object getFileLock(String roomId) {
        return fileLocks.computeIfAbsent(roomId, key -> new Object());
    }
}
