package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.config.Config;
import com.svi.tictactoewebservice.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FileGameRecordRepository")
class FileGameRecordRepositoryTest {

    @TempDir
    Path tempDirectory;

    private Path gamesDirectory;
    private FileGameRecordRepository repository;

    @BeforeEach
    void setUp() {
        gamesDirectory = tempDirectory.resolve("games");
        String config = "GAMES_DIRECTORY="
                + gamesDirectory.toString().replace('\\', '/')
                + System.lineSeparator();
        Config.setContext(new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8)));
        repository = new FileGameRecordRepository();
    }

    @Test
    @DisplayName("save and findByGameId round-trip a record in CSV format")
    void saveAndFindByGameIdRoundTripRecord() throws IOException {
        Game game = new Game(
                "game-1",
                "player_1",
                "X",
                "top-left",
                "2026-09-04T09:00:00Z");

        repository.save(game);

        Path recordFile = gamesDirectory.resolve("game-1.txt");
        String expectedLine = "game-1,player_1,X,top-left,2026-09-04T09:00:00Z";
        assertEquals(
                expectedLine + System.lineSeparator(),
                new String(Files.readAllBytes(recordFile), StandardCharsets.UTF_8));

        List<Game> records = repository.findByGameId("game-1");
        assertEquals(1, records.size());
        Game loadedGame = records.get(0);
        assertAll(
                () -> assertEquals(game.getGameid(), loadedGame.getGameid()),
                () -> assertEquals(game.getPlayerid(), loadedGame.getPlayerid()),
                () -> assertEquals(game.getSymbol(), loadedGame.getSymbol()),
                () -> assertEquals(game.getLocation(), loadedGame.getLocation()),
                () -> assertEquals(game.getDatesave(), loadedGame.getDatesave()));
    }

    @Test
    @DisplayName("existsByGameId changes from false to true after save")
    void existsByGameIdReflectsSavedGame() {
        assertFalse(repository.existsByGameId("game-1"));

        repository.save(new Game(
                "game-1",
                "player_1",
                "X",
                "top-left",
                "2026-09-04T09:00:00Z"));

        assertTrue(repository.existsByGameId("game-1"));
    }
}
