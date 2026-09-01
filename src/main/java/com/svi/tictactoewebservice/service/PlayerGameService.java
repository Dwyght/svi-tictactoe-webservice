package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;

import java.util.ArrayList;
import java.util.List;

public class PlayerGameService {

    private final PlayerRecordRepository playerRecordRepository;

    public PlayerGameService(PlayerRecordRepository playerRecordRepository) {
        this.playerRecordRepository = playerRecordRepository;
    }

    public List<GameId> getGames(String playerId) {

        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID is required.");
        }

        if (!playerRecordRepository.existsByPlayerId(playerId)) {
            return null;
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