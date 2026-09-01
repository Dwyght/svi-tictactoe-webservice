package com.svi.tictactoewebservice.dto.request;

public class EmoteRequest {

    private String symbol;
    private String emoteid;

    public EmoteRequest() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getEmoteid() {
        return emoteid;
    }

    public void setEmoteid(String emoteid) {
        this.emoteid = emoteid;
    }
}