package com.yangrufeng.mud.mudserver.entity;

import com.yangrufeng.mud.mudserver.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2018/12/8.
 */

public class Item implements Thing {
    public String getId() {
        return id;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String name;
    private String desc;
    private int unit;
    private String type;
    private int price;

    public Item() {
        super();
    }

    public Item(JSONObject obj) throws JSONException {
        super();
        this.id = obj.getString(Common.JSON_ID);
        this.name = obj.getString(Common.JSON_NAME);
        this.desc = obj.getString(Common.JSON_DESC);
        this.unit = obj.getInt(Common.JSON_UNIT);
        this.type = obj.getString(Common.JSON_TYPE);
        this.price = obj.getInt(Common.JSON_PRICE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put(Common.JSON_ID,this.id);
        obj.put(Common.JSON_NAME,this.name);
        obj.put(Common.JSON_DESC,this.desc);
        obj.put(Common.JSON_UNIT,this.unit);
        obj.put(Common.JSON_TYPE,this.type);
        obj.put(Common.JSON_PRICE,this.price);
        return obj;
    }
}
