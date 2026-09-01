package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.exception.RecordSaveException;
import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.model.MoveRecord;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.GameSessionRepository;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GameService {

    private static final String GAME_ID_SEPARATOR = "__";

    private final GameRecordRepository gameRecordRepository;
    private final PlayerRecordRepository playerRecordRepository;
    private final GameSessionRepository gameSessionRepository;

    public GameService(
            GameRecordRepository gameRecordRepository,
            PlayerRecordRepository playerRecordRepository,
            GameSessionRepository gameSessionRepository) {

        this.gameRecordRepository = gameRecordRepository;
        this.playerRecordRepository = playerRecordRepository;
        this.gameSessionRepository = gameSessionRepository;
    }

    // ========================================
    // SAVE MOVE
    // ========================================

    public void save(SaveRequest request) {
        validateSaveRequest(request);

        MoveRecord record = new MoveRecord(
                request.getGameid(),
                request.getPlayerid(),
                request.getSymbol(),
                request.getLocation(),
                request.getDatesave()
        );

        try {
            playerRecordRepository.saveGameId(
                    request.getPlayerid(),
                    request.getGameid()
            );

            gameRecordRepository.save(record);

        } catch (RuntimeException e) {
            throw new RecordSaveException(
                    "Record could not be saved",
                    e
            );
        }
    }

    // ========================================
    // GET PLAYER GAMES
    // ========================================

    public List<GameId> getPlayerGames(String playerId) {

        if (!playerRecordRepository.existsByPlayerId(playerId)) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        List<String> gameIds =
                playerRecordRepository.findGameIdsByPlayerId(playerId);

        List<GameId> games = new ArrayList<>();

        for (String gameId : gameIds) {
            games.add(new GameId(gameId));
        }

        return games;
    }

    // ========================================
    // GET GAME DETAILS
    // ========================================

    public List<MoveRecord> getGame(String gameId) {

        if (!gameRecordRepository.existsByGameId(gameId)) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        List<MoveRecord> records =
                gameRecordRepository.findByGameId(gameId);

        records.sort(
                Comparator.comparing(MoveRecord::getDatesave)
        );

        return records;
    }

    // ========================================
    // CREATE NEW GAME ID
    // ========================================

    public String createGameId(String gameCode) {

        String gameId =
                gameCode
                        + GAME_ID_SEPARATOR
                        + UUID.randomUUID().toString();

        gameSessionRepository.saveCurrentGameId(
                gameCode,
                gameId
        );

        return gameId;
    }

    // ========================================
    // GET CURRENT GAME ID
    // ========================================

    public String getCurrentGameId(String gameCode) {

        String gameId =
                gameSessionRepository.findCurrentGameId(gameCode);

        if (gameId == null) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        return gameId;
    }

    // ========================================
    // SAVE VALIDATION
    // ========================================

    private void validateSaveRequest(SaveRequest request) {

        if (request == null) {
            throw new RecordSaveException(
                    "Record could not be saved"
            );
        }

        if (isBlank(request.getGameid())
                || isBlank(request.getPlayerid())
                || isBlank(request.getSymbol())
                || isBlank(request.getLocation())
                || isBlank(request.getDatesave())) {

            throw new RecordSaveException(
                    "Record could not be saved"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}