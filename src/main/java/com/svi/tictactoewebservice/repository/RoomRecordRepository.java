package com.svi.tictactoewebservice.repository;

import java.util.List;

public interface RoomRecordRepository {

    void saveGameId(String roomId, String gameId);

    List<String> findGameIdsByRoomId(String roomId);

    boolean existsByRoomId(String roomId);
}
