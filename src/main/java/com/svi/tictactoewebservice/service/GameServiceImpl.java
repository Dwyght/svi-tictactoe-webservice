package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.dto.request.EmoteRequest;
import com.svi.tictactoewebservice.dto.request.PlayerSessionRequest;
import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.dto.request.ScoreRequest;
import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.exception.RecordSaveException;
import com.svi.tictactoewebservice.exception.PlayerNameConflictException;
import com.svi.tictactoewebservice.model.Game;
import com.svi.tictactoewebservice.model.GameSession;
import com.svi.tictactoewebservice.model.RecordId;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.GameSessionRepository;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GameServiceImpl implements GameService {

    private final GameRecordRepository gameRecordRepository;
    private final PlayerRecordRepository playerRecordRepository;
    private final RoomService roomService;
    private final GameSessionRepository gameSessionRepository;

    // Required so CDI can create a client proxy for this application-scoped bean.
    protected GameServiceImpl() {
        this(null, null, null, null);
    }

    @Inject
    public GameServiceImpl(
            GameRecordRepository gameRecordRepository,
            PlayerRecordRepository playerRecordRepository,
            RoomService roomService,
            GameSessionRepository gameSessionRepository) {
        this.gameRecordRepository = gameRecordRepository;
        this.playerRecordRepository = playerRecordRepository;
        this.roomService = roomService;
        this.gameSessionRepository = gameSessionRepository;
    }

    // ========================================
    // SAVE MOVE
    // ========================================

    @Override
    public void save(SaveRequest request) {
        GameSession session = getSessionForSave(request);
        Game game = new Game(
                request.getGameid(),
                request.getPlayerid(),
                request.getSymbol(),
                request.getLocation(),
                request.getDatesave());

        synchronized (session) {
            if (!request.getGameid().equals(session.getCurrentGameId())) {
                throw new RecordSaveException("Record could not be saved");
            }

            try {
                gameRecordRepository.save(game);
                playerRecordRepository.saveGameId(request.getPlayerid(), request.getGameid());
                roomService.recordGameForRoom(request.getRoomid(), request.getGameid());
            } catch (RuntimeException e) {
                throw new RecordSaveException("Record could not be saved", e);
            }
        }
    }

    // ========================================
    // GET ALL GAMES
    // ========================================

    @Override
    public List<RecordId> getAllGames() {
        List<String> gameIds = gameRecordRepository.findAllGameIds();

        if (gameIds.isEmpty()) {
            throw new RecordNotFoundException("Record not found");
        }

        List<RecordId> recordIds = new ArrayList<>();

        for (String gameId : gameIds) {
            recordIds.add(new RecordId(gameId));
        }

        return recordIds;
    }

    // ========================================
    // GET PLAYER GAMES
    // ========================================

    @Override
    public List<RecordId> getPlayerGames(String playerId) {
        if (!playerRecordRepository.existsByPlayerId(playerId)) {
            throw new RecordNotFoundException("Record not found");
        }

        List<String> gameIds = playerRecordRepository.findGameIdsByPlayerId(playerId);
        List<RecordId> recordIds = new ArrayList<>();

        for (String gameId : gameIds) {
            recordIds.add(new RecordId(gameId));
        }

        return recordIds;
    }

    // ========================================
    // GET GAME DETAILS
    // ========================================

    @Override
    public List<Game> getGame(String gameId) {
        if (!gameRecordRepository.existsByGameId(gameId)) {
            throw new RecordNotFoundException("Record not found");
        }

        List<Game> records = gameRecordRepository.findByGameId(gameId);
        records.sort(Comparator.comparing(Game::getDatesave));

        return records;
    }

    // ========================================
    // CREATE NEW ROUND / GAME ID
    // ========================================

    @Override
    public String createGameId(String gameCode) {
        GameSession session = gameSessionRepository.getOrCreate(gameCode);

        synchronized (session) {
            String gameId = UUID.randomUUID().toString();
            session.setCurrentGameId(gameId);

            // New round: old emotes should not carry over.
            session.setXEmoteId(null);
            session.setXEmoteEventId(0);
            session.setOEmoteId(null);
            session.setOEmoteEventId(0);

            gameSessionRepository.save(session);

            return gameId;
        }
    }

    // ========================================
    // GET CURRENT GAME ID
    // ========================================

    @Override
    public String getCurrentGameId(String gameCode) {
        GameSession session = getExistingSession(gameCode);

        if (session.getCurrentGameId() == null) {
            throw new RecordNotFoundException("Record not found");
        }

        return session.getCurrentGameId();
    }

    // ========================================
    // REGISTER / UPDATE PLAYER
    // ========================================

    @Override
    public void registerPlayer(String gameCode, PlayerSessionRequest request) {
        GameSession session = gameSessionRepository.getOrCreate(gameCode);

        synchronized (session) {
            if ("X".equals(request.getSymbol())) {
                validateOpponentPlayerName(request.getPlayerid(), session.getOPlayerId());
                session.setXPlayerId(request.getPlayerid());
                session.setXSushiId(request.getSushiid());
            } else {
                validateOpponentPlayerName(request.getPlayerid(), session.getXPlayerId());
                session.setOPlayerId(request.getPlayerid());
                session.setOSushiId(request.getSushiid());
            }

            gameSessionRepository.save(session);
        }
    }

    // ========================================
    // UPDATE SCORE
    // ========================================

    @Override
    public void updateScore(String gameCode, ScoreRequest request) {
        GameSession session = getExistingSession(gameCode);

        synchronized (session) {
            session.setXScore(request.getXscore());
            session.setOScore(request.getOscore());

            gameSessionRepository.save(session);
        }
    }

    // ========================================
    // SEND EMOTE
    // ========================================

    @Override
    public void sendEmote(String gameCode, EmoteRequest request) {
        GameSession session = getExistingSession(gameCode);

        synchronized (session) {
            long eventId = session.getEmoteSequence() + 1;
            session.setEmoteSequence(eventId);

            if ("X".equals(request.getSymbol())) {
                session.setXEmoteId(request.getEmoteid());
                session.setXEmoteEventId(eventId);
            } else {
                session.setOEmoteId(request.getEmoteid());
                session.setOEmoteEventId(eventId);
            }

            gameSessionRepository.save(session);
        }
    }

    // ========================================
    // GET FULL SESSION
    // ========================================

    @Override
    public GameSession getSession(String gameCode) {
        return getExistingSession(gameCode);
    }

    // ========================================
    // HELPERS
    // ========================================

    private GameSession getExistingSession(String gameCode) {
        GameSession session = gameSessionRepository.findByGameCode(gameCode);

        if (session == null) {
            throw new RecordNotFoundException("Record not found");
        }

        return session;
    }

    private void validateOpponentPlayerName(String playerId, String opponentPlayerId) {
        if (opponentPlayerId != null && playerId.equalsIgnoreCase(opponentPlayerId)) {
            throw new PlayerNameConflictException("Player name is already being used in this room.");
        }
    }

    private GameSession getSessionForSave(SaveRequest request) {
        GameSession session = gameSessionRepository.findByGameCode(request.getRoomid());

        if (session == null) {
            throw new RecordSaveException("Record could not be saved");
        }

        return session;
    }
}
