package com.svi.tictactoewebservice.model;

/**
 * Holds the live, in-memory match state keyed by {@code gameCode}, including players, scores,
 * and emotes. A room is the persistent lobby identified by {@code roomid} and tracked by the
 * room-record repository, while a game is one round identified by {@code gameid} whose moves
 * are persisted by the game-record repository; a session connects the room to its current round.
 */
public class GameSession {

    private String gameCode;
    private String currentGameId;

    private String xPlayerId;
    private String xSushiId;

    private String oPlayerId;
    private String oSushiId;

    private int xScore;
    private int oScore;

    private String xEmoteId;
    private long xEmoteEventId;

    private String oEmoteId;
    private long oEmoteEventId;

    private long emoteSequence;

    public GameSession() {
    }

    public GameSession(String gameCode) {
        this.gameCode = gameCode;
        this.xScore = 0;
        this.oScore = 0;
        this.emoteSequence = 0;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }

    public String getXPlayerId() {
        return xPlayerId;
    }

    public void setXPlayerId(String xPlayerId) {
        this.xPlayerId = xPlayerId;
    }

    public String getXSushiId() {
        return xSushiId;
    }

    public void setXSushiId(String xSushiId) {
        this.xSushiId = xSushiId;
    }

    public String getOPlayerId() {
        return oPlayerId;
    }

    public void setOPlayerId(String oPlayerId) {
        this.oPlayerId = oPlayerId;
    }

    public String getOSushiId() {
        return oSushiId;
    }

    public void setOSushiId(String oSushiId) {
        this.oSushiId = oSushiId;
    }

    public int getXScore() {
        return xScore;
    }

    public void setXScore(int xScore) {
        this.xScore = xScore;
    }

    public int getOScore() {
        return oScore;
    }

    public void setOScore(int oScore) {
        this.oScore = oScore;
    }

    public String getXEmoteId() {
        return xEmoteId;
    }

    public void setXEmoteId(String xEmoteId) {
        this.xEmoteId = xEmoteId;
    }

    public long getXEmoteEventId() {
        return xEmoteEventId;
    }

    public void setXEmoteEventId(long xEmoteEventId) {
        this.xEmoteEventId = xEmoteEventId;
    }

    public String getOEmoteId() {
        return oEmoteId;
    }

    public void setOEmoteId(String oEmoteId) {
        this.oEmoteId = oEmoteId;
    }

    public long getOEmoteEventId() {
        return oEmoteEventId;
    }

    public void setOEmoteEventId(long oEmoteEventId) {
        this.oEmoteEventId = oEmoteEventId;
    }

    public long getEmoteSequence() {
        return emoteSequence;
    }

    public void setEmoteSequence(long emoteSequence) {
        this.emoteSequence = emoteSequence;
    }
}
