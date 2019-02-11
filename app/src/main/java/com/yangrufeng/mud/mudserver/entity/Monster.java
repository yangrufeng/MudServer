package com.yangrufeng.mud.mudserver.entity;

import com.yangrufeng.mud.mudserver.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2018/12/1.
 */

public class Monster {
    private String id;
    private String name;
    private int con; // 体质（生命）
    private int dex; // 敏捷（行动速度）
    private int str; // 力量（攻击力）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public String[] getDropout() {
        return dropout;
    }

    public void setDropout(String[] dropout) {
        this.dropout = dropout;
    }

    private String[] dropout; // 掉落

    public String[] getFeature() {
        return feature;
    }

    public void setFeature(String[] feature) {
        this.feature = feature;
    }

    private String[] feature; // 特性

    private JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(Common.JSON_ID,this.id);
        jo.put(Common.JSON_NAME,this.name);
        jo.put(Common.JSON_CON,this.con);
        jo.put(Common.JSON_DEX,this.dex);
        jo.put(Common.JSON_STR,this.str);
        return jo;
    }
}
