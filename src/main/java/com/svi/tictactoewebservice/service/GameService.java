package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.dto.request.EmoteRequest;
import com.svi.tictactoewebservice.dto.request.PlayerSessionRequest;
import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.dto.request.ScoreRequest;
import com.svi.tictactoewebservice.model.Game;
import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.model.GameSession;

import java.util.List;

public interface GameService {

    void save(SaveRequest request);

    List<GameId> getAllGames();

    List<GameId> getPlayerGames(String playerId);

    List<Game> getGame(String gameId);

    String createGameId(String gameCode);

    String getCurrentGameId(String gameCode);

    void registerPlayer(String gameCode, PlayerSessionRequest request);

    void updateScore(String gameCode, ScoreRequest request);

    void sendEmote(String gameCode, EmoteRequest request);

    GameSession getSession(String gameCode);
}
