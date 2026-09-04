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
- Payara Server compatible with Jakarta EE 8
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

The relative directory paths resolve from the working directory where the application-server process is launched. Ensure that location is writable by the server process. `FRONTEND_URLS` is a comma-separated allowlist; whitespace around entries is ignored.

At application startup, `AppStartup` loads this file through the `CONFIG_INI_LOCATION` context parameter in `web.xml`. It then creates the players, rooms, and games directories beneath `./records` if they do not already exist.

## Build

From the project root on Windows:

```powershell
.\mvnw.cmd clean package
```

On macOS or Linux:

```bash
./mvnw clean package
```

The generated artifact is:

```text
target/tictactoe-webservice-1.0.war
```

## Deploy to Payara

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

## Endpoints

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
  "roomid": "AB12CD34",
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

### Response shapes

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
  "gamecode": "AB12CD34",
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
| 400 | Bean Validation rejected the request |
| 401 | A move record could not be saved |
| 402 | A requested game, player, room, or runtime session was not found |
| 409 | The player name is already in use in the room |
| 500 | An unexpected server error occurred |

Statuses `401` and `402` are intentional parts of this application's existing API contract.

## Persistence

The application uses three file-backed repositories:

- `games/{gameId}.txt` stores one comma-separated move per line in the order `gameid,playerid,symbol,location,datesave`.
- `players/{playerId}.txt` stores one game ID per line.
- `rooms/{roomId}.txt` stores one game ID per line.

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
