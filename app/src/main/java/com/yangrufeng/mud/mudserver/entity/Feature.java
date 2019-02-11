package com.yangrufeng.mud.mudserver.entity;

/**
 * Created by pc on 2018/12/30.
 * 装备
 * 的特性
 */

public enum Feature {
    NONE("NONE","无","没有特性"),
    CRIT("CRIT","重击","30%机率伤害翻倍"),
    HEMOPHAGIA("HEMOPHAGIA","吸血","造成敌人失血的30%增加到体质"),
//    CAROM("CAROM","连击","30%机率进行再次攻击"),
    REDUCEINJURY("REDUCEINJURY","减伤","减少来自敌人的伤害30%"),
//    DIVIDE("DIVIDE","分裂","对其他敌人造成30%伤害"),
    MULTIPLE("MULTIPLE","多重射","对所有敌人均造成伤害"),
    PENETRATION("PENETRATION","穿透","无视对方力量造成固定伤害"),
    ACCURATE("ACCURATE","精准","攻击无法被对方闪躲");

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static double ratio = 0.3;
    public static int percent = 30;
    private final String key;
    private final String value;
    private final String desc;

    private Feature(String key,String value,String desc){
        this.key = key;
        this.value = value;
        this.desc = desc;
    }
}
