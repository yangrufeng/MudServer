package com.yangrufeng.mud.mudserver.entity;

import com.yangrufeng.mud.mudserver.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2018/12/30.
 */

public class Equipment extends Item implements Thing {
    private EquipPosition position;
    private Feature feature;
    private Prefix prefix;
    private int maxcon;
    private int dex;
    private int str;

    public Equipment() {

    }

    public Equipment(JSONObject obj) throws JSONException {
        super(obj);
        this.prefix = Prefix.randomPrefix();
        obj.put(Common.JSON_PREFIX,this.prefix.getValue());
        setId(obj.getString(Common.JSON_TYPE)+obj.getString(Common.JSON_PREFIX)+obj.getString(Common.JSON_ID));
        setName(obj.getString(Common.JSON_PREFIX)+obj.getString(Common.JSON_NAME));

        for(EquipPosition ep : EquipPosition.values()) {
            if(ep.getKey().equals(obj.getString(Common.JSON_POSITION))) {
                this.position = ep;
                break;
            }
        }
        this.maxcon = obj.getInt(Common.JSON_MAXCON)==0?0:obj.getInt(Common.JSON_MAXCON)+Integer.parseInt(this.prefix.getKey());
        this.dex = obj.getInt(Common.JSON_DEX)==0?0:obj.getInt(Common.JSON_DEX)+Integer.parseInt(this.prefix.getKey());
        this.str = obj.getInt(Common.JSON_STR)==0?0:obj.getInt(Common.JSON_STR)+Integer.parseInt(this.prefix.getKey());
        this.feature = Feature.valueOf(obj.getString(Common.JSON_FEATURE));
        setDesc(getDesc()+"\n"+"最大体质值增加："+this.maxcon+"|"+"力量增加："+this.str+"|"+"敏捷增加："+this.dex+"|特性："+this.feature.getValue()+"("+this.feature.getDesc()+")");
    }
    public EquipPosition getPosition() {
        return this.position;
    }

    public void setPostion(EquipPosition position) {
        this.position = position;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public void setPrefix(Prefix prefix) {
        this.prefix = prefix;
    }

    public int getMaxcon() {
        return maxcon;
    }

    public void setMaxcon(int maxcon) {
        this.maxcon = maxcon;
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

    public void setType(String type) {
        super.setType(type);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject obj = super.toJSONObject();
        obj.put(Common.JSON_POSITION,position.getKey());
        obj.put(Common.JSON_PREFIX,Integer.parseInt(this.prefix.getKey()));
        obj.put(Common.JSON_MAXCON,this.maxcon);
        obj.put(Common.JSON_DEX,this.dex);
        obj.put(Common.JSON_STR,this.str);
        return obj;
    }

    public Equipment clone() {
        Equipment e = new Equipment();
        e.setId(this.getId());
        e.setPrice(this.getPrice());
        e.setName(this.getName());
        e.setDesc(this.getDesc());
        e.setUnit(this.getUnit());
        e.setType(this.getType());
        e.setPostion(this.getPosition());
        e.setFeature(this.getFeature());
        e.setPrefix(this.getPrefix());
        e.setMaxcon(this.getMaxcon());
        e.setDex(this.getDex());
        e.setStr(this.getStr());
        return e;
    }
}
