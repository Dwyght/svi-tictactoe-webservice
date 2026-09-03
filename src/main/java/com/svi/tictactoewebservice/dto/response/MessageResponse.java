package com.svi.tictactoewebservice.dto.response;

import javax.json.bind.annotation.JsonbProperty;

public class MessageResponse {

    @JsonbProperty("msg")
    private String msg;

    public MessageResponse() {
    }

    public MessageResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
