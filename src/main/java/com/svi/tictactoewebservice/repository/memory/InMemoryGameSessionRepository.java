package com.svi.tictactoewebservice.repository.memory;

import com.svi.tictactoewebservice.model.GameSession;
import com.svi.tictactoewebservice.repository.GameSessionRepository;

import javax.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class InMemoryGameSessionRepository implements GameSessionRepository {

    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    @Override
    public GameSession getOrCreate(String gameCode) {
        GameSession existingSession = sessions.get(gameCode);

        if (existingSession != null) {
            return existingSession;
        }

        GameSession newSession = new GameSession(gameCode);

        GameSession previousSession = sessions.putIfAbsent(gameCode, newSession);

        if (previousSession != null) {
            return previousSession;
        }

        return newSession;
    }

    @Override
    public GameSession findByGameCode(String gameCode) {
        return sessions.get(gameCode);
    }

    @Override
    public void save(GameSession session) {
        sessions.put(session.getGameCode(), session);
    }
}
