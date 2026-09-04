package com.svi.tictactoewebservice.repository;

import com.svi.tictactoewebservice.model.GameSession;

/**
 * Owns in-memory sessions keyed by game code, representing current live match state; persistent
 * room metadata and per-round move history are owned by the room and game repositories.
 */
public interface GameSessionRepository {

    GameSession getOrCreate(String gameCode);

    GameSession findByGameCode(String gameCode);

    void save(GameSession session);
}
