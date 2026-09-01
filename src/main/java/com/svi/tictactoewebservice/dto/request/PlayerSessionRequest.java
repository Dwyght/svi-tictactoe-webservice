package com.svi.tictactoewebservice.dto.request;

public class PlayerSessionRequest {

    private String playerid;
    private String symbol;
    private String sushiid;

    public PlayerSessionRequest() {
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

    public String getSushiid() {
        return sushiid;
    }

    public void setSushiid(String sushiid) {
        this.sushiid = sushiid;
    }
}