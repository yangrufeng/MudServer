package com.yangrufeng.mud.mudserver.entity;

import com.yangrufeng.mud.mudserver.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2018/11/11.
 */

public class Player {
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

    public int getHungry() {
        return hungry;
    }

    public void setHungry(int hungry) {
        this.hungry = hungry;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getCost_while_sleep() {
        return cost_while_sleep;
    }

    public void setCost_while_sleep(int cost_while_sleep) {
        this.cost_while_sleep = cost_while_sleep;
    }

    public float getPhysical_injury_percent() {
        return physical_injury_percent;
    }

    public void setPhysical_injury_percent(float physical_injury_percent) {
        this.physical_injury_percent = physical_injury_percent;
    }

    public float getDrug_effect_percent() {
        return drug_effect_percent;
    }

    public void setDrug_effect_percent(float drug_effect_percent) {
        this.drug_effect_percent = drug_effect_percent;
    }

    public float getNpc_favor_percent() {
        return npc_favor_percent;
    }

    public void setNpc_favor_percent(float npc_favor_percent) {
        this.npc_favor_percent = npc_favor_percent;
    }

    public float getThreat_from_a_warrior() {
        return threat_from_a_warrior;
    }

    public void setThreat_from_a_warrior(float threat_from_a_warrior) {
        this.threat_from_a_warrior = threat_from_a_warrior;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private String ip;
    private String name; // 姓名（【昵称|角色名】）
    private int con; // 体质（生命）
    private int maxcon; // 最大体质（生命）

    public int getMaxhungry() {
        return maxhungry;
    }

    public void setMaxhungry(int maxhungry) {
        this.maxhungry = maxhungry;
    }

    private int maxhungry; // 最大饥饿值
    private int dex; // 敏捷（行动速度）

    public int getMaxcon() {
        return maxcon;
    }

    public void setMaxcon(int maxcon) {
        this.maxcon = maxcon;
    }

    private int hungry; // 饥饿值（行动力）
    private int str; // 力量（攻击力）
    private String des; // 描述
    private int cost_while_sleep = 5; // 休息时消耗饥饿值（默认休息消耗5）
    private float physical_injury_percent = 1; // 物理伤害百分比（默认100%）
    private float drug_effect_percent = 1; // 使用回复类药物效果（默认100%）
    private float npc_favor_percent = 1; // NPC好感度增长效果（默认100%）
    private float threat_from_a_warrior = 0; // 怪物的仇恨值(默认0)

    public Place getPosition() {
        return position;
    }

    public void setPosition(Place position) {
        this.position = position;
    }

    private Place position;

    public Map<String, Task> getAcceptedTask() {
        return acceptedTask;
    }

    public void setAcceptedTask(Map<String, Task> acceptedTask) {
        this.acceptedTask = acceptedTask;
    }

    private Map<String,Task> acceptedTask = new HashMap<String,Task>(); // 接受的任务

    public Map<String,Thing> getOwnThings() {
        return ownThings;
    }

    public void setOwnThings(Map<String,Thing> ownThings) {
        this.ownThings = ownThings;
    }

    private Map<String,Thing> ownThings = new HashMap<String,Thing>(); // 拥有的物品

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(Common.JSON_ID,this.ip);
        o.put(Common.JSON_NAME,this.name);
        o.put(Common.JSON_CON,this.con);
        o.put(Common.JSON_DEX,this.dex);
        o.put(Common.JSON_STR,this.str);
        o.put(Common.JSON_HUNGRY,this.hungry);
        o.put(Common.JSON_DESC,this.des);
        o.put(Common.JSON_POSITION,this.position.getId());
        return o;
    }
}
