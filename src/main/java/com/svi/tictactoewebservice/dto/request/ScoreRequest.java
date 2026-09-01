package com.svi.tictactoewebservice.dto.request;

public class ScoreRequest {

    private int xscore;
    private int oscore;

    public ScoreRequest() {
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
}