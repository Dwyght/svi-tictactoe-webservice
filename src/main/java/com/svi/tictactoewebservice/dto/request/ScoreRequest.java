package com.svi.tictactoewebservice.dto.request;

import javax.validation.constraints.Min;

public class ScoreRequest {

    @Min(value = 0, message = "Score cannot be negative.")
    private int xscore;

    @Min(value = 0, message = "Score cannot be negative.")
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
