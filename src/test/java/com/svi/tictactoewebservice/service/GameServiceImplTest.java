package com.svi.tictactoewebservice.service;

import com.svi.tictactoewebservice.dto.request.PlayerSessionRequest;
import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.exception.PlayerNameConflictException;
import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.exception.RecordSaveException;
import com.svi.tictactoewebservice.model.Game;
import com.svi.tictactoewebservice.model.GameSession;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.GameSessionRepository;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameServiceImpl")
class GameServiceImplTest {

    @Mock
    private GameRecordRepository gameRecordRepository;

    @Mock
    private PlayerRecordRepository playerRecordRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private GameSessionRepository gameSessionRepository;

    private GameServiceImpl gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameServiceImpl(
                gameRecordRepository,
                playerRecordRepository,
                roomService,
                gameSessionRepository);
    }

    @Test
    @DisplayName("save persists a valid move and records it for the player and room")
    void savePersistsAValidMove() {
        SaveRequest request = validSaveRequest();
        GameSession session = sessionWithCurrentGame(request.getRoomid(), request.getGameid());
        when(gameSessionRepository.findByGameCode(request.getRoomid())).thenReturn(session);

        gameService.save(request);

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRecordRepository, times(1)).save(gameCaptor.capture());
        Game savedGame = gameCaptor.getValue();
        assertAll(
                () -> assertEquals(request.getGameid(), savedGame.getGameid()),
                () -> assertEquals(request.getPlayerid(), savedGame.getPlayerid()),
                () -> assertEquals(request.getSymbol(), savedGame.getSymbol()),
                () -> assertEquals(request.getLocation(), savedGame.getLocation()),
                () -> assertEquals(request.getDatesave(), savedGame.getDatesave()));
        verify(playerRecordRepository, times(1))
                .saveGameId(request.getPlayerid(), request.getGameid());
        verify(roomService, times(1))
                .recordGameForRoom(request.getRoomid(), request.getGameid());
    }

    @Test
    @DisplayName("save rejects a room with no existing session")
    void saveRejectsMissingSession() {
        SaveRequest request = validSaveRequest();
        when(gameSessionRepository.findByGameCode(request.getRoomid())).thenReturn(null);

        assertThrows(RecordSaveException.class, () -> gameService.save(request));

        verify(gameSessionRepository).findByGameCode(request.getRoomid());
        verifyNoInteractions(gameRecordRepository, playerRecordRepository, roomService);
    }

    @Test
    @DisplayName("save rejects a game id that differs from the session's current game")
    void saveRejectsMismatchedGameId() {
        SaveRequest request = validSaveRequest();
        GameSession session = sessionWithCurrentGame(request.getRoomid(), "another-game");
        when(gameSessionRepository.findByGameCode(request.getRoomid())).thenReturn(session);

        assertThrows(RecordSaveException.class, () -> gameService.save(request));

        verifyNoInteractions(gameRecordRepository, playerRecordRepository, roomService);
    }

    @Test
    @DisplayName("save wraps repository runtime failures in RecordSaveException")
    void saveWrapsRepositoryRuntimeFailure() {
        SaveRequest request = validSaveRequest();
        GameSession session = sessionWithCurrentGame(request.getRoomid(), request.getGameid());
        RuntimeException repositoryFailure = new RuntimeException("disk unavailable");
        when(gameSessionRepository.findByGameCode(request.getRoomid())).thenReturn(session);
        doThrow(repositoryFailure).when(gameRecordRepository).save(any(Game.class));

        RecordSaveException exception = assertThrows(
                RecordSaveException.class,
                () -> gameService.save(request));

        assertSame(repositoryFailure, exception.getCause());
        verify(playerRecordRepository, never()).saveGameId(any(), any());
        verify(roomService, never()).recordGameForRoom(any(), any());
    }

    @Test
    @DisplayName("save relies on the controller boundary for request field validation")
    void saveDoesNotRepeatRequestFieldValidation() {
        SaveRequest request = validSaveRequest();
        request.setLocation("   ");
        GameSession session = sessionWithCurrentGame(request.getRoomid(), request.getGameid());
        when(gameSessionRepository.findByGameCode(request.getRoomid())).thenReturn(session);

        gameService.save(request);

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRecordRepository).save(gameCaptor.capture());
        assertEquals("   ", gameCaptor.getValue().getLocation());
        verify(playerRecordRepository).saveGameId(request.getPlayerid(), request.getGameid());
        verify(roomService).recordGameForRoom(request.getRoomid(), request.getGameid());
    }

    @Test
    @DisplayName("registerPlayer rejects X when O already uses the same player id ignoring case")
    void registerPlayerRejectsOpponentNameConflictIgnoringCase() {
        GameSession session = new GameSession("room-1");
        session.setOPlayerId("PlayerOne");
        when(gameSessionRepository.getOrCreate("room-1")).thenReturn(session);
        PlayerSessionRequest request = playerRequest("playerone", "X", "sushi-x");

        assertThrows(
                PlayerNameConflictException.class,
                () -> gameService.registerPlayer("room-1", request));

        assertNull(session.getXPlayerId());
        verify(gameSessionRepository, never()).save(any(GameSession.class));
    }

    @Test
    @DisplayName("registerPlayer persists distinct X and O players on the session")
    void registerPlayerPersistsDistinctPlayers() {
        GameSession session = new GameSession("room-1");
        when(gameSessionRepository.getOrCreate("room-1")).thenReturn(session);

        gameService.registerPlayer("room-1", playerRequest("alice", "X", "sushi-x"));
        assertAll(
                () -> assertEquals("alice", session.getXPlayerId()),
                () -> assertEquals("sushi-x", session.getXSushiId()));
        verify(gameSessionRepository, times(1)).save(session);

        gameService.registerPlayer("room-1", playerRequest("bob", "O", "sushi-o"));

        assertAll(
                () -> assertEquals("alice", session.getXPlayerId()),
                () -> assertEquals("sushi-x", session.getXSushiId()),
                () -> assertEquals("bob", session.getOPlayerId()),
                () -> assertEquals("sushi-o", session.getOSushiId()));
        verify(gameSessionRepository, times(2)).save(session);
    }

    @Test
    @DisplayName("getGame throws when the game id does not exist")
    void getGameRejectsMissingGameId() {
        when(gameRecordRepository.existsByGameId("missing-game")).thenReturn(false);

        assertThrows(
                RecordNotFoundException.class,
                () -> gameService.getGame("missing-game"));

        verify(gameRecordRepository, never()).findByGameId(any());
    }

    @Test
    @DisplayName("getPlayerGames throws when the player id does not exist")
    void getPlayerGamesRejectsMissingPlayerId() {
        when(playerRecordRepository.existsByPlayerId("missing-player")).thenReturn(false);

        assertThrows(
                RecordNotFoundException.class,
                () -> gameService.getPlayerGames("missing-player"));

        verify(playerRecordRepository, never()).findGameIdsByPlayerId(any());
    }

    @Test
    @DisplayName("getGame returns records sorted by datesave ascending")
    void getGameSortsRecordsByDateSaveAscending() {
        List<Game> records = new ArrayList<>(Arrays.asList(
                game("2026-09-04T12:00:00Z"),
                game("2026-09-04T09:00:00Z"),
                game("2026-09-04T10:30:00Z")));
        when(gameRecordRepository.existsByGameId("game-1")).thenReturn(true);
        when(gameRecordRepository.findByGameId("game-1")).thenReturn(records);

        List<Game> result = gameService.getGame("game-1");

        assertAll(
                () -> assertEquals("2026-09-04T09:00:00Z", result.get(0).getDatesave()),
                () -> assertEquals("2026-09-04T10:30:00Z", result.get(1).getDatesave()),
                () -> assertEquals("2026-09-04T12:00:00Z", result.get(2).getDatesave()));
    }

    private SaveRequest validSaveRequest() {
        SaveRequest request = new SaveRequest();
        request.setRoomid("room-1");
        request.setGameid("game-1");
        request.setPlayerid("player_1");
        request.setSymbol("X");
        request.setLocation("top-left");
        request.setDatesave("2026-09-04T09:00:00Z");
        return request;
    }

    private GameSession sessionWithCurrentGame(String roomId, String gameId) {
        GameSession session = new GameSession(roomId);
        session.setCurrentGameId(gameId);
        return session;
    }

    private PlayerSessionRequest playerRequest(String playerId, String symbol, String sushiId) {
        PlayerSessionRequest request = new PlayerSessionRequest();
        request.setPlayerid(playerId);
        request.setSymbol(symbol);
        request.setSushiid(sushiId);
        return request;
    }

    private Game game(String dateSave) {
        return new Game("game-1", "player_1", "X", "top-left", dateSave);
    }
}
