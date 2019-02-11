package com.yangrufeng.mud.mudserver.entity;

/**
 * Created by pc on 2018/11/29.
 */

public class Place {
    private String terrain; // 地形
    private String id; // 对应地图节点ID

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
