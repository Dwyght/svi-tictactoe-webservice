package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;

import java.util.ArrayList;
import java.util.List;

public class PlayerGameService {

    private final PlayerRecordRepository playerRecordRepository;

    public PlayerGameService(
            PlayerRecordRepository playerRecordRepository) {

        this.playerRecordRepository = playerRecordRepository;
    }

    public List<GameId> getGames(String playerId) {

        if (!playerRecordRepository.existsByPlayerId(playerId)) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        List<String> gameIds =
                playerRecordRepository.findGameIdsByPlayerId(playerId);

        List<GameId> games = new ArrayList<>();

        for (String gameId : gameIds) {
            games.add(new GameId(gameId));
        }

        return games;
    }
}