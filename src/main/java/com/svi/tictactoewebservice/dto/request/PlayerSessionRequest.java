package com.svi.tictactoewebservice.dto.request;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class PlayerSessionRequest {

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

    @JsonbProperty("sushiid")
    @NotBlank(message = "Sushi ID is required.")
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
