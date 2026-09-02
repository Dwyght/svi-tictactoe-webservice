package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.dto.request.EmoteRequest;
import com.svi.tictactoewebservice.dto.request.PlayerSessionRequest;
import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.dto.request.ScoreRequest;
import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.exception.RecordSaveException;
import com.svi.tictactoewebservice.exception.PlayerNameConflictException;
import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.model.GameSession;
import com.svi.tictactoewebservice.model.MoveRecord;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.GameSessionRepository;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import com.svi.tictactoewebservice.repository.RoomRecordRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GameService {

    private final GameRecordRepository gameRecordRepository;
    private final PlayerRecordRepository playerRecordRepository;
    private final RoomRecordRepository roomRecordRepository;
    private final GameSessionRepository gameSessionRepository;

    public GameService(
            GameRecordRepository gameRecordRepository,
            PlayerRecordRepository playerRecordRepository,
            RoomRecordRepository roomRecordRepository,
            GameSessionRepository gameSessionRepository) {

        this.gameRecordRepository = gameRecordRepository;
        this.playerRecordRepository = playerRecordRepository;
        this.roomRecordRepository = roomRecordRepository;
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
    // GET ALL GAMES
    // ========================================

    public List<GameId> getAllGames() {

        List<String> gameIds =
                gameRecordRepository.findAllGameIds();

        if (gameIds.isEmpty()) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        List<GameId> games = new ArrayList<>();

        for (String gameId : gameIds) {
            games.add(new GameId(gameId));
        }

        return games;
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
    // GET ALL ROOMS
    // ========================================

    public List<GameId> getAllRooms() {

        List<String> roomIds =
                roomRecordRepository.findAllRoomIds();

        if (roomIds.isEmpty()) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        List<GameId> rooms = new ArrayList<>();

        for (String roomId : roomIds) {
            rooms.add(new GameId(roomId));
        }

        return rooms;
    }

    // ========================================
    // GET ROOM GAMES
    // ========================================

    public List<GameId> getRoomGames(String roomId) {

        if (!roomRecordRepository.existsByRoomId(roomId)) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        List<String> gameIds =
                roomRecordRepository.findGameIdsByRoomId(roomId);

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
    // CREATE NEW ROUND / GAME ID
    // ========================================

    public String createGameId(String gameCode) {

        GameSession session =
                gameSessionRepository.getOrCreate(gameCode);

        synchronized (session) {

            String gameId = UUID.randomUUID().toString();

            session.setCurrentGameId(gameId);

            // New round: old emotes should not carry over.
            session.setXEmoteId(null);
            session.setXEmoteEventId(0);

            session.setOEmoteId(null);
            session.setOEmoteEventId(0);

            gameSessionRepository.save(session);
            roomRecordRepository.saveGameId(gameCode, gameId);

            return gameId;
        }
    }

    // ========================================
    // GET CURRENT GAME ID
    // ========================================

    public String getCurrentGameId(String gameCode) {

        GameSession session =
                getExistingSession(gameCode);

        if (session.getCurrentGameId() == null) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        return session.getCurrentGameId();
    }

    // ========================================
    // REGISTER / UPDATE PLAYER
    // ========================================

    public void registerPlayer(
            String gameCode,
            PlayerSessionRequest request) {

        GameSession session =
                gameSessionRepository.getOrCreate(gameCode);

        synchronized (session) {

            if ("X".equals(request.getSymbol())) {

                validateOpponentPlayerName(
                        request.getPlayerid(),
                        session.getOPlayerId()
                );

                session.setXPlayerId(
                        request.getPlayerid()
                );

                session.setXSushiId(
                        request.getSushiid()
                );

            } else {

                validateOpponentPlayerName(
                        request.getPlayerid(),
                        session.getXPlayerId()
                );

                session.setOPlayerId(
                        request.getPlayerid()
                );

                session.setOSushiId(
                        request.getSushiid()
                );

            }

            gameSessionRepository.save(session);
        }
    }

    // ========================================
    // UPDATE SCORE
    // ========================================

    public void updateScore(
            String gameCode,
            ScoreRequest request) {

        GameSession session =
                getExistingSession(gameCode);

        synchronized (session) {

            session.setXScore(request.getXscore());
            session.setOScore(request.getOscore());

            gameSessionRepository.save(session);
        }
    }

    // ========================================
    // SEND EMOTE
    // ========================================

    public void sendEmote(
            String gameCode,
            EmoteRequest request) {

        GameSession session =
                getExistingSession(gameCode);

        synchronized (session) {

            long eventId =
                    session.getEmoteSequence() + 1;

            session.setEmoteSequence(eventId);

            if ("X".equals(request.getSymbol())) {

                session.setXEmoteId(
                        request.getEmoteid()
                );

                session.setXEmoteEventId(eventId);

            } else {

                session.setOEmoteId(
                        request.getEmoteid()
                );

                session.setOEmoteEventId(eventId);

            }

            gameSessionRepository.save(session);
        }
    }

    // ========================================
    // GET FULL SESSION
    // ========================================

    public GameSession getSession(String gameCode) {
        return getExistingSession(gameCode);
    }

    // ========================================
    // HELPERS
    // ========================================

    private GameSession getExistingSession(String gameCode) {

        GameSession session =
                gameSessionRepository.findByGameCode(gameCode);

        if (session == null) {
            throw new RecordNotFoundException(
                    "Record not found"
            );
        }

        return session;
    }

    private void validateSaveRequest(SaveRequest request) {

        if (request == null) {
            throw new RecordSaveException(
                    "Record could not be saved"
            );
        }

        if (isBlank(request.getGameid())
                || !request.getPlayerid()
                .matches("^[A-Za-z0-9_-]{1,10}$")
                || isBlank(request.getSymbol())
                || isBlank(request.getLocation())
                || isBlank(request.getDatesave())) {

            throw new RecordSaveException(
                    "Record could not be saved"
            );
        }
    }

    private void validateOpponentPlayerName(
            String playerId,
            String opponentPlayerId) {

        if (opponentPlayerId != null
                && playerId.equalsIgnoreCase(opponentPlayerId)) {
            throw new PlayerNameConflictException(
                    "Player name is already being used in this room."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
