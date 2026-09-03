package com.svi.tictactoewebservice.dto.request;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.Min;

public class ScoreRequest {

    @JsonbProperty("xscore")
    @Min(value = 0, message = "Score cannot be negative.")
    private int xscore;

    @JsonbProperty("oscore")
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
