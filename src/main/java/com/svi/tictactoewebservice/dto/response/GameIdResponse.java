package com.svi.tictactoewebservice.dto.response;

import javax.json.bind.annotation.JsonbProperty;

public class GameIdResponse {

    @JsonbProperty("gameid")
    private String gameid;

    public GameIdResponse() {
    }

    public GameIdResponse(String gameid) {
        this.gameid = gameid;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }
}
