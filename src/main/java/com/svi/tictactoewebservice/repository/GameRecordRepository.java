package com.svi.tictactoewebservice.repository;

import com.svi.tictactoewebservice.model.Game;
import java.util.List;

public interface GameRecordRepository {

    void save(Game game);

    List<String> findAllGameIds();
    List<Game> findByGameId(String gameId);
    boolean existsByGameId(String gameId);
}
