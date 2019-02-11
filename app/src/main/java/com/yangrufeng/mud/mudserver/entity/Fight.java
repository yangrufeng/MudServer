package com.yangrufeng.mud.mudserver.entity;

import java.util.List;

/**
 * Created by pc on 2018/12/1.
 */

public class Fight {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMonsterList() {
        return monsterList;
    }

    public void setMonsterList(List<String> monsterList) {
        this.monsterList = monsterList;
    }

    private List<String> monsterList;
}
