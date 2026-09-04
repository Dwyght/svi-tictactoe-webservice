package com.svi.tictactoewebservice.repository;

import java.util.List;

/**
 * Owns persistent room-lobby records, keyed by room ID, and their associations to game IDs;
 * live players, scores, and emotes belong to the separate in-memory game session.
 */
public interface RoomRecordRepository {

    void saveGameId(String roomId, String gameId);

    List<String> findAllRoomIds();

    List<String> findGameIdsByRoomId(String roomId);

    boolean existsByRoomId(String roomId);
}
