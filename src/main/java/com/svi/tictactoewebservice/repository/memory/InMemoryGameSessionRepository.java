package com.svi.tictactoewebservice.repository.memory;

import com.svi.tictactoewebservice.repository.GameSessionRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGameSessionRepository
        implements GameSessionRepository {

    private static final Map<String, String> CURRENT_GAMES =
            new ConcurrentHashMap<>();

    @Override
    public void saveCurrentGameId(
            String gameCode,
            String gameId) {

        CURRENT_GAMES.put(gameCode, gameId);
    }

    @Override
    public String findCurrentGameId(String gameCode) {
        return CURRENT_GAMES.get(gameCode);
    }
}