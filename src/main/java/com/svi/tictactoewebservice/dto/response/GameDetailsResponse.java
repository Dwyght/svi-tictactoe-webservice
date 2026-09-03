package com.svi.tictactoewebservice.dto.response;

import com.svi.tictactoewebservice.model.Game;
import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class GameDetailsResponse {

    @JsonbProperty("list")
    private List<Game> list;

    @JsonbProperty("msg")
    private String msg;

    public GameDetailsResponse() {
    }

    public GameDetailsResponse(List<Game> list, String msg) {
        this.list = list;
        this.msg = msg;
    }

    public List<Game> getList() {
        return list;
    }

    public void setList(List<Game> list) {
        this.list = list;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
