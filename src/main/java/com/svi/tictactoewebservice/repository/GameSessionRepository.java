package com.svi.tictactoewebservice.repository;

import com.svi.tictactoewebservice.model.GameSession;

public interface GameSessionRepository {

    GameSession getOrCreate(String gameCode);

    GameSession findByGameCode(String gameCode);

    void save(GameSession session);
}