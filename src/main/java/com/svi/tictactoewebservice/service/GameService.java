package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.model.MoveRecord;
import com.svi.tictactoewebservice.repository.GameRecordRepository;

import java.util.Comparator;
import java.util.List;

public class GameService {

    private final GameRecordRepository gameRecordRepository;

    public GameService(GameRecordRepository gameRecordRepository) {
        this.gameRecordRepository = gameRecordRepository;
    }

    public List<MoveRecord> getGame(String gameId) {

        if (!gameRecordRepository.existsByGameId(gameId)) {
            throw new RecordNotFoundException("Record not found");
        }

        List<MoveRecord> records =
                gameRecordRepository.findByGameId(gameId);

        records.sort(
                Comparator.comparing(MoveRecord::getDatesave)
        );

        return records;
    }
}