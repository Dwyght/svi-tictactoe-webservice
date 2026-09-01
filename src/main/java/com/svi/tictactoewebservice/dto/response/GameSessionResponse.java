package com.svi.tictactoewebservice.dto.response;

import com.svi.tictactoewebservice.model.GameSession;

public class GameSessionResponse {

    private String gamecode;
    private String gameid;

    private String xplayerid;
    private String xsushiid;

    private String oplayerid;
    private String osushiid;

    private int xscore;
    private int oscore;

    private String xemoteid;
    private long xemoteeventid;

    private String oemoteid;
    private long oemoteeventid;

    public GameSessionResponse() {
    }

    public GameSessionResponse(GameSession session) {

        this.gamecode = session.getGameCode();
        this.gameid = session.getCurrentGameId();

        this.xplayerid = session.getXPlayerId();
        this.xsushiid = session.getXSushiId();

        this.oplayerid = session.getOPlayerId();
        this.osushiid = session.getOSushiId();

        this.xscore = session.getXScore();
        this.oscore = session.getOScore();

        this.xemoteid = session.getXEmoteId();
        this.xemoteeventid =
                session.getXEmoteEventId();

        this.oemoteid = session.getOEmoteId();
        this.oemoteeventid =
                session.getOEmoteEventId();
    }

    public String getGamecode() {
        return gamecode;
    }

    public void setGamecode(String gamecode) {
        this.gamecode = gamecode;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getXplayerid() {
        return xplayerid;
    }

    public void setXplayerid(String xplayerid) {
        this.xplayerid = xplayerid;
    }

    public String getXsushiid() {
        return xsushiid;
    }

    public void setXsushiid(String xsushiid) {
        this.xsushiid = xsushiid;
    }

    public String getOplayerid() {
        return oplayerid;
    }

    public void setOplayerid(String oplayerid) {
        this.oplayerid = oplayerid;
    }

    public String getOsushiid() {
        return osushiid;
    }

    public void setOsushiid(String osushiid) {
        this.osushiid = osushiid;
    }

    public int getXscore() {
        return xscore;
    }

    public void setXscore(int xscore) {
        this.xscore = xscore;
    }

    public int getOscore() {
        return oscore;
    }

    public void setOscore(int oscore) {
        this.oscore = oscore;
    }

    public String getXemoteid() {
        return xemoteid;
    }

    public void setXemoteid(String xemoteid) {
        this.xemoteid = xemoteid;
    }

    public long getXemoteeventid() {
        return xemoteeventid;
    }

    public void setXemoteeventid(long xemoteeventid) {
        this.xemoteeventid = xemoteeventid;
    }

    public String getOemoteid() {
        return oemoteid;
    }

    public void setOemoteid(String oemoteid) {
        this.oemoteid = oemoteid;
    }

    public long getOemoteeventid() {
        return oemoteeventid;
    }

    public void setOemoteeventid(long oemoteeventid) {
        this.oemoteeventid = oemoteeventid;
    }
}