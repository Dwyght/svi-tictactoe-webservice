# Tic-Tac-Toe Webservice

A Jakarta EE 8 webservice that supports the Sushi Tic-Tac-Toe frontend. It manages runtime game-session metadata and persists room, player, game, and move history in text files.

The separate Tic-Tac-Toe game server remains responsible for the live board, turn validation, and accepting moves. This project records accepted moves and provides the frontend's history and session APIs.

## Technology

- Java 8
- Jakarta EE 8 APIs using the `javax.*` namespace
- JAX-RS for HTTP resources
- CDI for dependency injection
- JSON-B for JSON mapping
- Bean Validation for request validation
- Maven Wrapper and WAR packaging
- Payara-compatible deployment

## Requirements

- JDK 8
- Maven 3 (or the included Maven Wrapper)
- A Jakarta EE 8-compatible servlet container or application server that provides JAX-RS, CDI, JSON-B, and Bean Validation; Payara Server is the reference deployment used by this project
- Port `8080` available, or corresponding frontend URL changes

No standalone `main()` method is provided. The application runs as a WAR inside Payara.

## Configuration

Application settings are stored in [`src/main/webapp/WEB-INF/configurations/GeneralConfig.ini`](src/main/webapp/WEB-INF/configurations/GeneralConfig.ini):

```properties
PLAYERS_DIRECTORY=./records/players
ROOMS_DIRECTORY=./records/rooms
GAMES_DIRECTORY=./records/games
FRONTEND_URLS=http\://localhost:5500,http\://127.0.0.1:5500
```

| Setting | Purpose |
| --- | --- |
| `PLAYERS_DIRECTORY` | Directory containing one game-id index file per player. |
| `ROOMS_DIRECTORY` | Directory containing one game-id index file per room. |
| `GAMES_DIRECTORY` | Directory containing the persisted move records for each game. |
| `FRONTEND_URLS` | Comma-separated CORS allowlist of frontend origins permitted to call the API from a browser. |

Relative record paths resolve from the working directory where the application-server process is launched, not from the WAR or repository location. The defaults therefore create a portable `records/` tree beneath that working location. A deployment may use different relative paths or environment-specific absolute paths, but developer-specific paths must not be committed as shared defaults. Ensure that the configured location is writable by the server process. Whitespace around entries in `FRONTEND_URLS` is ignored.

At application startup, `AppStartup` loads this file through the `CONFIG_INI_LOCATION` context parameter in `web.xml`. It then creates the players, rooms, and games directories beneath `./records` if they do not already exist.

## Build

From the project root, run:

```bash
mvn clean package
```

Alternatively, use the included Maven Wrapper on Windows:

```powershell
.\mvnw.cmd clean package
```

Or on macOS and Linux:

```bash
./mvnw clean package
```

The generated artifact is:

```text
target/tictactoe-webservice-1.0.war
```

## Deploy

Deploy the generated WAR with the normal deployment mechanism for the chosen Jakarta EE 8 server. For Payara:

1. Start a Payara domain.
2. Deploy `target/tictactoe-webservice-1.0.war` through the Admin Console or `asadmin`.
3. Verify the application with:

   ```text
   GET http://localhost:8080/tictactoe-webservice-1.0/api/health-check
   ```

   Expected response:

   ```text
   Tic Tac Toe API works!
   ```

The default context root comes from the WAR filename. If Payara deploys it under a different context root, update `WEBSERVICE_BASE_URL` in the frontend.

## API base URL

With the default artifact name and Payara port, JSON endpoints use:

```text
http://localhost:8080/tictactoe-webservice-1.0/api
```

`/api` is declared by `TicTacToeApplication` using `@ApplicationPath`.

## Domain model

A **room** is a persistent lobby identified by `roomid`; its record is an index of games played in that room. A **session** is the current in-memory `GameSession`, keyed by `gameCode`, that holds live player assignments, sushi choices, scores, emotes, and the current game id. A **game** is one round identified by `gameid`, whose individual moves are persisted by the game repository. For a move to be saved, its `roomid` must identify the same live-session key used as `gameCode`, and its `gameid` must match that session's current game.

The session, room, score, and emote APIs extend the original persisted-game-history endpoints with multiplayer lobby support, live score synchronization, and emote reactions. Session state is held only in memory; the room-to-game index and game moves are persisted separately.

## API reference

All paths below are relative to the [API base URL](#api-base-url). JSON endpoints consume and produce `application/json` unless noted otherwise.

### Endpoint summary

| Method | Path | Request | Successful response |
| --- | --- | --- | --- |
| GET | `/health-check` | None | Plain text: `Tic Tac Toe API works!` |
| POST | `/game/save` | Save move JSON | `{"msg":"Record saved."}` |
| GET | `/game` | None | Game ID list |
| GET | `/game/{gameId}` | None | Recorded move list |
| GET | `/player/{playerId}/games` | None | Game ID list |
| GET | `/rooms` | None | Room ID list |
| GET | `/room/{roomId}/games` | None | Game ID list |
| POST | `/session/{gameCode}/game` | None | `{"gameid":"..."}` |
| GET | `/session/{gameCode}/game` | None | `{"gameid":"..."}` |
| POST | `/session/{gameCode}/player` | Player session JSON | `{"msg":"Player registered."}` |
| POST | `/session/{gameCode}/score` | Score JSON | `{"msg":"Score updated."}` |
| POST | `/session/{gameCode}/emote` | Emote JSON | `{"msg":"Emote sent."}` |
| GET | `/session/{gameCode}` | None | Runtime session JSON |

### Request bodies

Save an accepted move:

```json
{
  "roomid": "AB2CD3EF",
  "gameid": "9e2f06bf-1c2d-4e2c-9c92-55d8fb1dc934",
  "playerid": "Dwyght",
  "symbol": "X",
  "location": "0",
  "datesave": "2026-09-03T06:30:00.000Z"
}
```

Register or update a player:

```json
{
  "playerid": "Dwyght",
  "symbol": "X",
  "sushiid": "x-sushi-1"
}
```

Update the series score:

```json
{
  "xscore": 1,
  "oscore": 0
}
```

Send an emote:

```json
{
  "symbol": "X",
  "emoteid": "happy"
}
```

Valid emote IDs are `angry`, `cry`, `haha`, `happy`, `hm`, and `sad`.

Valid sushi IDs are `x-sushi-1` through `x-sushi-5` and `o-sushi-1` through `o-sushi-5`. Move `location` is a string containing one board index from `0` through `8`. Player IDs contain 1-10 letters, numbers, underscores, or hyphens. Room and session game codes contain exactly eight characters from `ABCDEFGHJKLMNPQRSTUVWXYZ23456789`; game IDs used in paths have canonical UUID syntax.

### Shared response shapes

Game, room, and player history lists use:

```json
{
  "list": [
    { "id": "..." }
  ],
  "msg": "Records found"
}
```

Game details use the same top-level `list` and `msg` fields. Each move contains:

```json
{
  "gameid": "...",
  "playerid": "Dwyght",
  "symbol": "X",
  "location": "0",
  "datesave": "2026-09-03T06:30:00.000Z"
}
```

A runtime session contains:

```json
{
  "gamecode": "AB2CD3EF",
  "gameid": "...",
  "xplayerid": "Dwyght",
  "xsushiid": "x-sushi-1",
  "oplayerid": "PlayerO",
  "osushiid": "o-sushi-1",
  "xscore": 1,
  "oscore": 0,
  "xemoteid": "happy",
  "xemoteeventid": 1,
  "oemoteid": "sad",
  "oemoteeventid": 2
}
```

### Endpoint details

#### `POST /game/save`

- **Purpose:** Persist one move for the room's current game, add the game id to the player's index, and add it to the room's index.
- **Request body:** The save-move JSON shown under [Request bodies](#request-bodies). All fields are required. `playerid` must contain 1-10 letters, numbers, underscores, or hyphens, `symbol` must be `X` or `O`, and `location` must be a string from `0` through `8`.
- **Success:** `200 OK` with `{"msg":"Record saved."}`.
- **Errors:** `400 Bad Request` for Bean Validation failures; `401 Unauthorized` when the room has no live session, `gameid` is not that session's current game, or the persistence operation cannot be completed; `500 Internal Server Error` for any other unhandled failure.

#### `GET /game`

- **Purpose:** List every persisted game id.
- **Request body:** None.
- **Success:** `200 OK` with the shared id-list response.
- **Errors:** `402 Payment Required` when no valid persisted game records are found; `500 Internal Server Error` for an unhandled repository failure.

#### `GET /game/{gameId}`

- **Purpose:** Return all persisted moves for `gameId`, ordered by `datesave` ascending.
- **Request body:** None.
- **Success:** `200 OK` with the shared game-details response.
- **Errors:** `400 Bad Request` when `gameId` is not a canonical UUID; `402 Payment Required` when the game record does not exist; `500 Internal Server Error` for an unhandled repository or record-parsing failure.

#### `GET /player/{playerId}/games`

- **Purpose:** List the game ids recorded for `playerId`.
- **Request body:** None.
- **Success:** `200 OK` with the shared id-list response. An existing but empty player index produces an empty `list`.
- **Errors:** `400 Bad Request` when `playerId` does not match the player-id format; `402 Payment Required` when the player's record does not exist; `500 Internal Server Error` for an unhandled repository failure.

#### `GET /rooms`

- **Purpose:** List every persisted room id.
- **Request body:** None.
- **Success:** `200 OK` with the shared id-list response.
- **Errors:** `402 Payment Required` when no persisted room records are found; `500 Internal Server Error` for an unhandled repository failure.

#### `GET /room/{roomId}/games`

- **Purpose:** List the game ids recorded for `roomId`.
- **Request body:** None.
- **Success:** `200 OK` with the shared id-list response. An existing but empty room index produces an empty `list`.
- **Errors:** `400 Bad Request` when `roomId` does not match the eight-character game-code format; `402 Payment Required` when the room record does not exist; `500 Internal Server Error` for an unhandled repository failure.

#### `POST /session/{gameCode}/game`

- **Purpose:** Create the session if necessary, generate a new UUID game id, make it the session's current game, and clear its previous emote state.
- **Request body:** None.
- **Success:** `200 OK` with `{"gameid":"550e8400-e29b-41d4-a716-446655440000"}`.
- **Errors:** `400 Bad Request` when `gameCode` does not match the eight-character game-code format; `500 Internal Server Error` for an unhandled session repository failure.

#### `GET /session/{gameCode}/game`

- **Purpose:** Return the current game id for the live session identified by `gameCode`.
- **Request body:** None.
- **Success:** `200 OK` with `{"gameid":"550e8400-e29b-41d4-a716-446655440000"}`.
- **Errors:** `400 Bad Request` when `gameCode` does not match the eight-character game-code format; `402 Payment Required` when the session does not exist or has no current game; `500 Internal Server Error` for an unhandled session repository failure.

#### `POST /session/{gameCode}/player`

- **Purpose:** Register the `X` or `O` player and sushi choice on the live session. A missing session is created automatically.
- **Request body:** The player-session JSON shown under [Request bodies](#request-bodies). All fields are required; `playerid` must contain 1-10 letters, numbers, underscores, or hyphens, `symbol` must be `X` or `O`, and `sushiid` must be one of the valid sushi IDs listed above.
- **Success:** `200 OK` with `{"msg":"Player registered."}`.
- **Errors:** `400 Bad Request` when `gameCode` or the request body fails validation; `409 Conflict` if the same player id, compared case-insensitively, is already assigned to the opposite symbol; `500 Internal Server Error` for an unhandled session repository failure.

#### `POST /session/{gameCode}/score`

- **Purpose:** Replace the live session's `X` and `O` scores so connected clients can stay synchronized.
- **Request body:** The score JSON shown under [Request bodies](#request-bodies). Both scores must be zero or greater.
- **Success:** `200 OK` with `{"msg":"Score updated."}`.
- **Errors:** `400 Bad Request` when `gameCode` is invalid, a score is negative, or another Bean Validation constraint fails; `402 Payment Required` when the session does not exist; `500 Internal Server Error` for an unhandled session repository failure.

#### `POST /session/{gameCode}/emote`

- **Purpose:** Store the latest emote for one symbol and advance that symbol's emote event id so clients can detect a new reaction.
- **Request body:** The emote JSON shown under [Request bodies](#request-bodies). `symbol` must be `X` or `O`; `emoteid` must be `angry`, `cry`, `haha`, `happy`, `hm`, or `sad`.
- **Success:** `200 OK` with `{"msg":"Emote sent."}`.
- **Errors:** `400 Bad Request` when `gameCode` or the request body fails validation; `402 Payment Required` when the session does not exist; `500 Internal Server Error` for an unhandled session repository failure.

#### `GET /session/{gameCode}`

- **Purpose:** Return the complete live state for the session identified by `gameCode`.
- **Request body:** None.
- **Success:** `200 OK` with the shared runtime-session response.
- **Errors:** `400 Bad Request` when `gameCode` does not match the eight-character game-code format; `402 Payment Required` when the session does not exist; `500 Internal Server Error` for an unhandled session repository failure.

#### `GET /health-check`

- **Purpose:** Confirm that the deployed API is reachable.
- **Request body:** None.
- **Success:** `200 OK`, `text/plain`, with `Tic Tac Toe API works!`.
- **Errors:** No application-specific error response is defined; an unexpected runtime failure is mapped to `500 Internal Server Error`.

## HTTP status and error format

JSON errors use the following shape:

```json
{
  "msg": "Error message"
}
```

| Status | Meaning |
| --- | --- |
| 200 | Request completed successfully |
| 400 | Request validation or defensive record-ID validation rejected the request |
| 401 | A move record could not be saved |
| 402 | A requested game, player, room, or runtime session was not found |
| 409 | The player name is already in use in the room |
| 500 | An unexpected server error occurred |

Statuses `401` and `402` are intentional parts of this application's existing API contract.

## Persistence

The application uses three file-backed repositories under the configured directories:

- `GAMES_DIRECTORY/<gameid>.txt` stores one comma-separated move per line in the exact order `gameid,playerid,symbol,location,datesave`.
- `PLAYERS_DIRECTORY/<playerid>.txt` stores one `gameid` per line, creating the player's game-history index.
- `ROOMS_DIRECTORY/<roomid>.txt` stores one `gameid` per line, creating the room-to-game index.

Writes and reads use per-ID locks so unrelated games, players, and rooms can proceed concurrently without interleaving writes to the same file.

Runtime `GameSession` objects are stored in an application-scoped `ConcurrentHashMap`. Runtime sessions do not survive an application restart; persisted history files do.

## Project structure

```text
src/main/java/com/svi/tictactoewebservice/
|-- config/             INI-backed configuration
|-- contextlistener/    Servlet startup lifecycle
|-- controller/         JAX-RS resources
|-- dto/                Request and response contracts
|-- exception/          API exceptions and exception mappers
|-- filter/             CORS request/response filter
|-- model/              Game and runtime-session models
|-- repository/         Repository interfaces and implementations
|-- service/            Service interfaces and CDI implementations
|-- util/               File path and directory helpers
`-- TicTacToeApplication.java

src/main/webapp/WEB-INF/
|-- beans.xml
|-- configurations/GeneralConfig.ini
`-- web.xml
```

Controllers inject service interfaces. `GameServiceImpl` and `RoomServiceImpl` are CDI `@ApplicationScoped` beans, as are the repository implementations. CDI discovers annotated beans through `WEB-INF/beans.xml`.

## CORS

`CorsFilter` echoes `Access-Control-Allow-Origin` only for origins listed in `FRONTEND_URLS`. Allowed preflight requests receive:

- Methods: `GET, POST, OPTIONS`
- Headers: `Content-Type, Accept`

Restart or redeploy the application after changing the packaged configuration.

## Testing

Run compilation and tests together with:

```powershell
.\mvnw.cmd clean package
```

For an integration check, deploy the resulting WAR, call `/api/health-check`, and then serve the frontend from one of the configured CORS origins.

## License

No license file is currently included in this repository.
