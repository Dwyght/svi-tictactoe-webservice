package com.svi.tictactoewebservice.repository.file;

import com.svi.tictactoewebservice.config.Config;
import com.svi.tictactoewebservice.model.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
    private Logger repositoryLogger;
    private Level originalLoggerLevel;
    private RecordingLogHandler logHandler;

    @BeforeEach
    void setUp() {
        gamesDirectory = tempDirectory.resolve("games");
        String config = "GAMES_DIRECTORY="
                + gamesDirectory.toString().replace('\\', '/')
                + System.lineSeparator();
        Config.setContext(new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8)));
        repository = new FileGameRecordRepository();

        repositoryLogger = Logger.getLogger(FileGameRecordRepository.class.getName());
        originalLoggerLevel = repositoryLogger.getLevel();
        repositoryLogger.setLevel(Level.ALL);
        logHandler = new RecordingLogHandler();
        logHandler.setLevel(Level.ALL);
        repositoryLogger.addHandler(logHandler);
    }

    @AfterEach
    void tearDown() {
        repositoryLogger.removeHandler(logHandler);
        repositoryLogger.setLevel(originalLoggerLevel);
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

    @Test
    @DisplayName("findAllGameIds includes valid files and logs excluded corrupt files")
    void findAllGameIdsValidatesDiscoveredFilesAndLogsCorruption() throws IOException {
        Files.createDirectories(gamesDirectory);
        Files.createFile(gamesDirectory.resolve("empty-game.txt"));

        String mismatchedLine =
                "different-game,player_1,X,top-left,2026-09-04T09:00:00Z";
        Files.write(
                gamesDirectory.resolve("mismatched-game.txt"),
                Collections.singletonList(mismatchedLine),
                StandardCharsets.UTF_8);

        String validLine =
                "valid-game,player_2,O,bottom-right,2026-09-04T10:00:00Z";
        Files.write(
                gamesDirectory.resolve("valid-game.txt"),
                Collections.singletonList(validLine),
                StandardCharsets.UTF_8);

        List<String> gameIds = repository.findAllGameIds();

        assertAll(
                () -> assertEquals(Collections.singletonList("valid-game"), gameIds),
                () -> assertTrue(logHandler.contains(
                        Level.WARNING,
                        "empty-game.txt")),
                () -> assertTrue(logHandler.contains(
                        Level.SEVERE,
                        "mismatched-game.txt",
                        mismatchedLine)));
    }

    private static class RecordingLogHandler extends Handler {

        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        private boolean contains(Level level, String... messageParts) {
            for (LogRecord record : records) {
                if (!level.equals(record.getLevel())) {
                    continue;
                }

                boolean containsAllParts = true;
                for (String messagePart : messageParts) {
                    if (!record.getMessage().contains(messagePart)) {
                        containsAllParts = false;
                        break;
                    }
                }

                if (containsAllParts) {
                    return true;
                }
            }

            return false;
        }
    }
}
