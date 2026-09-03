package com.svi.tictactoewebservice.repository.memory;

import com.svi.tictactoewebservice.model.GameSession;
import com.svi.tictactoewebservice.repository.GameSessionRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGameSessionRepository implements GameSessionRepository {

    private static final Map<String, GameSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public GameSession getOrCreate(String gameCode) {
        GameSession existingSession = SESSIONS.get(gameCode);

        if (existingSession != null) {
            return existingSession;
        }

        GameSession newSession = new GameSession(gameCode);

        GameSession previousSession = SESSIONS.putIfAbsent(gameCode, newSession);

        if (previousSession != null) {
            return previousSession;
        }

        return newSession;
    }

    @Override
    public GameSession findByGameCode(String gameCode) {
        return SESSIONS.get(gameCode);
    }

    @Override
    public void save(GameSession session) {
        SESSIONS.put(session.getGameCode(), session);
    }
}
