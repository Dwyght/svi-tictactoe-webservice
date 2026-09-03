package com.svi.tictactoewebservice.dto.response;

import com.svi.tictactoewebservice.model.GameId;
import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class GameListResponse {

    @JsonbProperty("list")
    private List<GameId> list;

    @JsonbProperty("msg")
    private String msg;

    public GameListResponse() {
    }

    public GameListResponse(List<GameId> list, String msg) {
        this.list = list;
        this.msg = msg;
    }

    public List<GameId> getList() {
        return list;
    }

    public void setList(List<GameId> list) {
        this.list = list;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
