package com.svi.tictactoewebservice.repository;

public interface GameSessionRepository {

    void saveCurrentGameId(String gameCode, String gameId);

    String findCurrentGameId(String gameCode);
}