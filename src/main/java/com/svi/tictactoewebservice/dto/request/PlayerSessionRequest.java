package com.svi.tictactoewebservice.dto.request;

import com.svi.tictactoewebservice.annotation.PlayerId;
import com.svi.tictactoewebservice.annotation.Symbol;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class PlayerSessionRequest {

    @JsonbProperty("playerid")
    @NotBlank(message = "Player ID is required.")
    @PlayerId
    private String playerid;

    @JsonbProperty("symbol")
    @NotBlank(message = "Symbol is required.")
    @Symbol
    private String symbol;

    @JsonbProperty("sushiid")
    @NotBlank(message = "Sushi ID is required.")
    @Pattern(
            regexp = "^(x-sushi-1|x-sushi-2|x-sushi-3|x-sushi-4|x-sushi-5|"
                    + "o-sushi-1|o-sushi-2|o-sushi-3|o-sushi-4|o-sushi-5)$",
            message = "Invalid sushi ID."
    )
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
