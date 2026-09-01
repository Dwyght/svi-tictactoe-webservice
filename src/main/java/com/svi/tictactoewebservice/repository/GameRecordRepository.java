package com.svi.tictactoewebservice.repository;

import com.svi.tictactoewebservice.model.MoveRecord;

import java.util.List;

public interface GameRecordRepository {

    void save(MoveRecord record);

    List<MoveRecord> findByGameId(String gameId);
}