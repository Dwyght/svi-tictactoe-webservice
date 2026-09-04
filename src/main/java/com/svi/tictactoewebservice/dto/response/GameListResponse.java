package com.svi.tictactoewebservice.dto.response;

import com.svi.tictactoewebservice.model.RecordId;
import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class GameListResponse {

    @JsonbProperty("list")
    private List<RecordId> list;

    @JsonbProperty("msg")
    private String msg;

    public GameListResponse() {
    }

    public GameListResponse(List<RecordId> list, String msg) {
        this.list = list;
        this.msg = msg;
    }

    public List<RecordId> getList() {
        return list;
    }

    public void setList(List<RecordId> list) {
        this.list = list;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
