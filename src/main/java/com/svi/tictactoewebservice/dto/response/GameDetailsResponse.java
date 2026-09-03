package com.svi.tictactoewebservice.dto.response;

import com.svi.tictactoewebservice.model.MoveRecord;
import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class GameDetailsResponse {

    @JsonbProperty("list")
    private List<MoveRecord> list;

    @JsonbProperty("msg")
    private String msg;

    public GameDetailsResponse() {
    }

    public GameDetailsResponse(List<MoveRecord> list, String msg) {
        this.list = list;
        this.msg = msg;
    }

    public List<MoveRecord> getList() {
        return list;
    }

    public void setList(List<MoveRecord> list) {
        this.list = list;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
