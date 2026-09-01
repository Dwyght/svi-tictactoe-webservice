package com.svi.tictactoewebservice.dto.response;

public class GameIdResponse {

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