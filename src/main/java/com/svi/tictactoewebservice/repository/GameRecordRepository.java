package com.svi.tictactoewebservice.repository;

import com.svi.tictactoewebservice.model.Game;
import java.util.List;

/**
 * Owns persisted move records for games, where each game ID identifies one completed or
 * in-progress round rather than the room lobby or its live in-memory session.
 */
public interface GameRecordRepository {

    void save(Game game);

    List<String> findAllGameIds();
    List<Game> findByGameId(String gameId);
    boolean existsByGameId(String gameId);
}
