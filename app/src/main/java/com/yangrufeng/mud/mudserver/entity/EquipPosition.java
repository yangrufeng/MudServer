package com.yangrufeng.mud.mudserver.entity;

/**
 * Created by pc on 2018/12/30.
 * 仅用于限制装备装配位置，及相同装配位置下不重复装备
 */

public enum EquipPosition {
    HEAD("HEAD","头部"),LEFTHAND("LEFTHAND","左手"),RIGHTHAND("RIGHTHAND","右手"),FOOT("FOOD","脚"),BODY("BODY","身体");

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    private final String key;
    private final String value;

    private EquipPosition(String key,String value){
        this.key = key;
        this.value = value;
    }
}
