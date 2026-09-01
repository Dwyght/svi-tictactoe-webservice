package com.svi.tictactoewebservice.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class EmoteRequest {

    @NotBlank(message = "Symbol is required.")
    @Pattern(
            regexp = "^[XO]$",
            message = "Symbol must be X or O."
    )
    private String symbol;

    @NotBlank(message = "Emote ID is required.")
    @Pattern(
            regexp = "^(angry|cry|haha|happy|hm|sad)$",
            message = "Invalid emote ID."
    )
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
