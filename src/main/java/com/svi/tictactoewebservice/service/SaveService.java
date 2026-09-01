package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.model.MoveRecord;
import com.svi.tictactoewebservice.model.request.SaveRequest;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;

public class SaveService {

    private final GameRecordRepository gameRecordRepository;
    private final PlayerRecordRepository playerRecordRepository;

    public SaveService(
            GameRecordRepository gameRecordRepository,
            PlayerRecordRepository playerRecordRepository) {

        this.gameRecordRepository = gameRecordRepository;
        this.playerRecordRepository = playerRecordRepository;
    }

    public void save(SaveRequest request) {
        validate(request);

        MoveRecord record = new MoveRecord(
                request.getGameid(),
                request.getPlayerid(),
                request.getSymbol(),
                request.getLocation(),
                request.getDatesave()
        );

        playerRecordRepository.saveGameId(
                request.getPlayerid(),
                request.getGameid()
        );

        gameRecordRepository.save(record);
    }

    private void validate(SaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }

        if (isBlank(request.getGameid())
                || isBlank(request.getPlayerid())
                || isBlank(request.getSymbol())
                || isBlank(request.getLocation())
                || isBlank(request.getDatesave())) {

            throw new IllegalArgumentException("All fields are required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}