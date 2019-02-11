package com.yangrufeng.mud.mudserver.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pc on 2018/12/30.
 * 基于前缀随机给装备增加不同的品质
 */

public enum Prefix {
    LEVEL0("0","损坏的",20),LEVEL1("1","破旧的",30),LEVEL2("2","崭新的",15),LEVEL3("3","超凡的",10),LEVEL4("4","被祝福的",10),
    LEVEL5("5","辉煌的",5),LEVEL6("6","神圣的",4),LEVEL7("7","神赐的",3),LEVEL8("8","无可匹敌的",2),LEVEL9("9","独一无二的",1);
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getPercent() {
        return percent;
    }

    private final String key;
    private final String value;
    private final int percent;

    private Prefix(String key, String value, int percent){
        this.key = key;
        this.value = value;
        this.percent = percent;
    }

    public static Prefix randomPrefix() {
        int random=(int)(Math.random()*10);// 生成种子
        Random rand = new Random(random);
        List<Prefix> tempList = new ArrayList<Prefix>();
        for(int i = 0;i < Prefix.values().length;i++){
            Prefix temp = Prefix.values()[i];
            for(int j = 0; j < temp.getPercent(); j++) {
                tempList.add(temp);
            }
        }
        return tempList.get(rand.nextInt(tempList.size()));
    }
}
