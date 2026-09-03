package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.repository.RoomRecordRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RoomServiceImpl implements RoomService {

    private final RoomRecordRepository roomRecordRepository;

    // Required so CDI can create a client proxy for this application-scoped bean.
    protected RoomServiceImpl() {
        this(null);
    }

    @Inject
    public RoomServiceImpl(RoomRecordRepository roomRecordRepository) {
        this.roomRecordRepository = roomRecordRepository;
    }

    @Override
    public List<GameId> getAllRooms() {
        List<String> roomIds = roomRecordRepository.findAllRoomIds();

        if (roomIds.isEmpty()) {
            throw new RecordNotFoundException("Record not found");
        }

        List<GameId> rooms = new ArrayList<>();

        for (String roomId : roomIds) {
            rooms.add(new GameId(roomId));
        }

        return rooms;
    }

    @Override
    public List<GameId> getRoomGames(String roomId) {
        if (!roomRecordRepository.existsByRoomId(roomId)) {
            throw new RecordNotFoundException("Record not found");
        }

        List<String> gameIds = roomRecordRepository.findGameIdsByRoomId(roomId);
        List<GameId> games = new ArrayList<>();

        for (String gameId : gameIds) {
            games.add(new GameId(gameId));
        }

        return games;
    }

    @Override
    public void recordGameForRoom(String roomId, String gameId) {
        roomRecordRepository.saveGameId(roomId, gameId);
    }
}
