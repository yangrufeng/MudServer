package com.yangrufeng.mud.mudserver.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2019/1/2.
 */

public interface Thing {
    public String getName();

    public void setName(String name);

    public String getDesc();

    public void setDesc(String desc);

    public int getUnit();

    public void setUnit(int unit);

    public String getType();

    public void setType(String type);

    public String getId();

    public void setId(String id);

    public JSONObject toJSONObject() throws JSONException;

    public int getPrice();

    public void setPrice(int price);
}
