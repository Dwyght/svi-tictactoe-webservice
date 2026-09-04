package com.svi.tictactoewebservice.util;

import com.svi.tictactoewebservice.config.Config;
import com.svi.tictactoewebservice.exception.InvalidRecordIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("FileUtil")
class FileUtilTest {

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void configureRecordDirectories() {
        String root = tempDirectory.toString().replace('\\', '/');
        String configuration = "PLAYERS_DIRECTORY=" + root + "/players\n"
                + "ROOMS_DIRECTORY=" + root + "/rooms\n"
                + "GAMES_DIRECTORY=" + root + "/games\n"
                + "FRONTEND_URLS=http\\://localhost:5500\n";

        Config.setContext(new ByteArrayInputStream(
                configuration.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("rejects traversal sequences, path separators, and null bytes for every record type")
    void rejectsUnsafeRecordIds() {
        assertAll(
                () -> assertThrows(
                        InvalidRecordIdException.class,
                        () -> FileUtil.getPlayerFile("../../../etc/passwd")),
                () -> assertThrows(
                        InvalidRecordIdException.class,
                        () -> FileUtil.getRoomFile("..\\..\\windows\\system32")),
                () -> assertThrows(
                        InvalidRecordIdException.class,
                        () -> FileUtil.getGameFile("game..backup")),
                () -> assertThrows(
                        InvalidRecordIdException.class,
                        () -> FileUtil.getGameFile("game\0backup")),
                () -> assertThrows(
                        InvalidRecordIdException.class,
                        () -> FileUtil.getPlayerFile(null)));
    }

    @Test
    @DisplayName("constructs files normally for safe identifiers")
    void constructsFilesForSafeRecordIds() {
        File playerFile = FileUtil.getPlayerFile("player_1");
        File roomFile = FileUtil.getRoomFile("AB2CD3EF");
        File gameFile = FileUtil.getGameFile(
                "9e2f06bf-1c2d-4e2c-9c92-55d8fb1dc934");

        assertAll(
                () -> assertEquals("player_1.txt", playerFile.getName()),
                () -> assertEquals("AB2CD3EF.txt", roomFile.getName()),
                () -> assertEquals(
                        "9e2f06bf-1c2d-4e2c-9c92-55d8fb1dc934.txt",
                        gameFile.getName()));
    }
}
