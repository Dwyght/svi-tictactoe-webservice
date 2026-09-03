package com.svi.tictactoewebservice.dto.request;

import javax.json.bind.annotation.JsonbProperty;

public class SaveRequest {

    @JsonbProperty("roomid")
    private String roomid;

    @JsonbProperty("gameid")
    private String gameid;

    @JsonbProperty("playerid")
    private String playerid;

    @JsonbProperty("symbol")
    private String symbol;

    @JsonbProperty("location")
    private String location;

    @JsonbProperty("datesave")
    private String datesave;

    public SaveRequest() {
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDatesave() {
        return datesave;
    }

    public void setDatesave(String datesave) {
        this.datesave = datesave;
    }
}
