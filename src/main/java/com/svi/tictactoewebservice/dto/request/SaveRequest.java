package com.svi.tictactoewebservice.dto.request;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class SaveRequest {

    @JsonbProperty("roomid")
    @NotBlank(message = "Room ID is required.")
    private String roomid;

    @JsonbProperty("gameid")
    @NotBlank(message = "Game ID is required.")
    private String gameid;

    @JsonbProperty("playerid")
    @NotBlank(message = "Player ID is required.")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]{1,10}$",
            message = "Invalid player ID."
    )
    private String playerid;

    @JsonbProperty("symbol")
    @NotBlank(message = "Symbol is required.")
    @Pattern(
            regexp = "^[XO]$",
            message = "Symbol must be X or O."
    )
    private String symbol;

    @JsonbProperty("location")
    @NotBlank(message = "Location is required.")
    private String location;

    @JsonbProperty("datesave")
    @NotBlank(message = "Date saved is required.")
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
