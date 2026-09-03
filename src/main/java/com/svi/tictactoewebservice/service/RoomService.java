package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.model.GameId;

import java.util.List;

public interface RoomService {

    List<GameId> getAllRooms();

    List<GameId> getRoomGames(String roomId);

    void recordGameForRoom(String roomId, String gameId);
}
