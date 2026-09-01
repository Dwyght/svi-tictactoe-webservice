package com.svi.tictactoewebservice.repository;

import java.util.List;

public interface PlayerRecordRepository {
    void saveGameId(String playerId, String gameId);
    List<String> findGameIdsByPlayerId(String playerId);
    boolean existsByPlayerId(String playerId);
}