package com.yangrufeng.mud.mudserver.entity;

import com.yangrufeng.mud.mudserver.common.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pc on 2018/11/30.
 */

public class Task {
    private  String id;
    private String hint;
    private Place place;

    public String getNpc() {
        return npc;
    }

    public void setNpc(String npc) {
        this.npc = npc;
    }

    private String npc; // 任务NPC的ID

    public List<Item> getReward() {
        return reward;
    }

    public void setReward(List<Item> reward) {
        this.reward = reward;
    }

    private List<Item> reward;

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    private boolean isFinished = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Item getRequire() {
        return require;
    }

    public void setRequire(Item require) {
        this.require = require;
    }

    private Item require;

    public JSONObject toJSONObject() throws JSONException {
        JSONObject task = new JSONObject();
        task.put(Common.JSON_ID,this.id);
        task.put(Common.JSON_HINT,this.hint);
        task.put(Common.JSON_NPC,this.npc);
        JSONObject place = new JSONObject();
        place.put(Common.JSON_TERRAIN,this.place.getTerrain());
        place.put(Common.JSON_ID,this.place.getId());
        task.put(Common.JSON_PLACE,place);
        task.put(Common.JSON_HINT,this.hint);
        task.put(Common.JSON_ISFINISHED,this.isFinished);
        JSONObject requireObj = new JSONObject();
        requireObj.put(Common.JSON_ID,this.require.getId());
        requireObj.put(Common.JSON_UNIT,this.require.getUnit());
        task.put(Common.JSON_REQUIRE,requireObj);
        JSONArray rewardArray = new JSONArray();
        for(Item item : this.reward) {
            JSONObject temp = new JSONObject();
            temp.put(Common.JSON_ID,item.getId());
            temp.put(Common.JSON_UNIT,item.getUnit());
            rewardArray.put(temp);
        }
        task.put(Common.JSON_REWARD,rewardArray);
        return task;
    }
}
