package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.model.RecordId;

import java.util.List;

public interface RoomService {

    List<RecordId> getAllRooms();

    List<RecordId> getRoomGames(String roomId);

    void recordGameForRoom(String roomId, String gameId);
}
