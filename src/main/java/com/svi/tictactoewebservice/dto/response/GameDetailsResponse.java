package com.svi.tictactoewebservice.dto.response;

import com.svi.tictactoewebservice.model.MoveRecord;
import java.util.List;

public class GameDetailsResponse {

    private List<MoveRecord> list;
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