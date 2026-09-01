package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.model.MoveRecord;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileGameRecordRepository implements GameRecordRepository {

    @Override
    public void save(MoveRecord record) {
        File file = FileUtil.getRecordFile(record.getGameid());

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, true))) {

            writer.write(toLine(record));
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not save game record.", e);
        }
    }

    @Override
    public List<MoveRecord> findByGameId(String gameId) {
        File file = FileUtil.getRecordFile(gameId);
        List<MoveRecord> records = new ArrayList<>();

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                records.add(toMoveRecord(line));
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not read game records.", e);
        }

        return records;
    }

    @Override
    public boolean existsByGameId(String gameId) {
        return FileUtil.getRecordFile(gameId).exists();
    }

    private String toLine(MoveRecord record) {
        return record.getGameid() + ","
                + record.getPlayerid() + ","
                + record.getSymbol() + ","
                + record.getLocation() + ","
                + record.getDatesave();
    }

    private MoveRecord toMoveRecord(String line) {
        String[] values = line.split(",", -1);

        return new MoveRecord(
                values[0],
                values[1],
                values[2],
                values[3],
                values[4]
        );
    }
}