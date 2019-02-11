package com.yangrufeng.mud.mudserver.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pc on 2018/11/7.
 */

public class Common {
    public static  int HOST_PORT = 55555;
    public final static String SPLIT_SEPARATOR = "::";
    public final static String CMD_SEPARATOR = "\\|";
    public final static String ITEM_SEPARATOR = "/";
    public final static String ORDER_SEPARATOR = " ";
    public final static String CLIENT_NICKNAME = "NICKNAME";
    public final static String CLIENT_INPUT = "INPUT";
    public final static String CLIENT_USERS = "GET_USER";
    public final static String CLIENT_ROLES = "GET_ROLE";
    public final static String CLIENT_PICKROLE = "PICK_ROLE";
    public final static String CLIENT_PICK= "PICK_ITEM"; // 玩家选定选项
    public final static String CLIENT_MOVE = "MOVE";
    public final static String CLIENT_TRIGGER = "GET_TRIGGER";
    public final static String CLIENT_MONSTERDETAIL = "GET_MONSTERDETAIL";
    public final static String CLIENT_PERSIONDETAIL = "GET_PERSIONDETAIL";
    public final static String CLIENT_ESCAPE = "REQUEST_ESCAPE";
    public final static String CLIENT_FIGHT= "CLIENT_FIGHT_MONSTER";
    public final static String CLIENT_ITEMDETAIL = "GET_ITEMDETAIL"; // 玩家请求查询物品详情
    public final static String CLIENT_USE_ITEM = "USE_ITEM"; // 玩家请求使用物品
    public final static String CLIENT_EVENT = "ANSWER_EVENT"; // 玩家响应事件
    public final static String CLIENT_TALK = "TALK_TO"; // 与NPC交谈
    public final static String CLIENT_FIGHT_PERSION = "FIGHT_PERSION"; // 玩家与其它玩家或者NPC战斗
    public final static String CLIENT_WAIT = "WAIT";
    public final static String CLIENT_GETMAP = "GETMAP"; // 请求获取最新地图

    public final static String SERVER_ORDER = "~"; // 服务器给玩家发送指令
    public final static String SERVER_OUTPUT = "OUTPUT";
    public final static String SERVER_PIKER = "PIKER";
    public final static String SERVER_ACTION="ACTION";
    public final static String SERVER_USERS= "USERS";
    public final static String SERVER_ROLES= "ROLES";
    public final static String SERVER_SHOWPLAYER= "SHOW_PLAYER";
    public final static String SERVER_SHOWDESCRIPT= "SHOW_DES";
    public final static String SERVER_FIGHT= "START_FIGHT";
    public final static String SERVER_EVENT= "SHOW_EVENT";
    public final static String SERVER_TASK= "TASK"; // 查看玩家的任务状况
    public final static String SERVER_MONSTERDETAIL= "MONSTERDETAIL"; // 将怪物详情发送给玩家
    public final static String SERVER_PERSIONDETAIL = "PERSIONDETAIL"; // 服务器返回人物详情
    public final static String SERVER_ITEMDETAIL= "ITEMDETAIL"; // 将物品详情发送给玩家
    public final static String SERVER_ESCAPERESULT="ESCAPERESULT"; // 玩家脱离战斗结果（成功/失败）
    public final static String SERVER_TURNON = "TURNON"; // 允许玩家可以进行UI操作
    public final static String SERVER_REBIRTH = "REBIRTH"; // 复活一名玩家
    public final static String SERVER_MONSTER_FIGHT= "MONSTER_FIGHT_CLIENT"; // 怪物攻击玩家
    public final static String SERVER_REFRESH_FIGHT = "REFRESH_FIGHT"; // 刷新玩家看到的怪物列表
    public final static String SERVER_DROPOUT = "DROPOUT"; // 掉落物品
    public final static String SERVER_REFRESHPLAYER = "REFRESHPLAYER"; // 刷新玩家状态
    public final static String SERVER_MOVE= "MOVE_RESPONSE"; // 响应MOVE
    public final static String SERVER_NPC= "SHOW_NPC"; // 展示NPC信息
    public final static String SERVER_REFRESHPERSIONS= "REFRESHPERSIONS"; // 刷新人物列表
    public final static String SERVER_REFRESH_FIGHT_PERSION = "REFRESH_FIGHT_PERSION"; // 刷新玩家看到的人物列表
    public final static String SERVER_REFRESH_FIGHT_MONSTER = "REFRESH_FIGHT_MONSTER"; // 刷新玩家看到的怪物列表
    public final static String SERVER_REFRESH_MAP = "REFRESH_MAP"; // 刷新玩家地图
    public final static String SERVER_MOD = "MOD"; // 服务器通知玩家当前游戏使用的MOD
    public final static String SERVER_PICKROLE = "PICKROLE"; // 服务器响应玩家选择角色
    public final static String SERVER_WAIT = "WAIT"; // 服务器控制玩家等待其他玩家操作
    public final static String SERVER_FIGHT_PERSION = "FIGHT_PERSION"; // 服务器响应玩家攻击玩家或NPC的结果

    public final static int SERVER_OUTPUT_FLAG = 0;
    public final static int SERVER_SHOW_FLAG = 1;
    public final static int SERVER_REFRESHMAP_FLAG = 2;
    public final static int SERVER_REFRESHPLAYER_FLAG = 3;

    public final static String CMD_EVENT = "EVENT"; // 事件
    public final static String CMD_FIGHT = "FIGHT"; // 战斗
    public final static String CMD_TALK = "TALK"; // 对话
    public final static String CMD_NPC = "NPC"; // NPC
    public final static String CMD_TASK = "TASK"; // 任务
    public final static String CMD_COST= "COST"; // 消耗回合数

    public final static String FIGHT_SRC = "SRC";
    public final static String FIGHT_TARGET = "TARGET";
    public final static String FIGHT_HURT = "HURT";

    /*
     * 行动对象的类型
     */
    public final static String ROLETYPE_PLAYER = "PLAYER";
    public final static String ROLETYPE_MONSTER = "MONSTER";
    public final static String ROLETYPE_NPC = "NPC";

    /*
     * MOD
     */
    public final static String MOD_NONE = "NONE"; // 无MOD
    public final static String MOD_SURVIVAL = "SURVIVAL"; // 大逃杀MOD(大逃杀)

    /*
     * SURVIVAL MOD(大逃杀)
     */
    public final static String SURVIVAL_POISON_INTERVAL_TURN = "POISON_INTERVAL_TURN"; // 间隔回合数（投毒间隔）
    public final static String SURVIVAL_RANDOM_NUM = "RANDOM_NUM"; // 每轮随机投毒地点个数
    public final static String SURVIVAL_POISON_NUM = "POISON_NUM"; // 投毒结束后地点造成伤害（投毒数量）
    public final static String SURVIVAL_POISON_TURN = "POISON_TURN"; // 投毒回合数（投毒次数）
    public final static String SURVIVAL_POISON_SCOPE = "POISON_SCOPE"; // 投毒范围（地点ID列表）
    public final static String SURVIVAL_POISONING = "POISONING"; // 地点是否处于毒雾范围
    public final static String SURVIVAL_DROPOUT_INTERVAL_TURN = "DROPOUT_INTERVAL_TURN"; // 间隔回合数（补给品投放间隔）
    public final static String SURVIVAL_RANDOM_TURN = "RANDOM_TURN"; // 投放回合数（投放次数）
    public final static String SURVIVAL_PROFITEER_INTERVAL_TURN = "PROFITEER_INTERVAL_TURN"; // 奸商出现回合数（每隔几个回合随机出现在某地）
    public final static String SURVIVAL_PROFITEER_TURN = "PROFITEER_TURN"; //  奸商出现次数

    /*
     * JSON文件中的KEY
     */
    public final static String JSON_ACTION = "ACTION";
    public final static String JSON_TRIGGER = "TRIGGER";
    public final static String JSON_DESC = "DESC";
    public final static String JSON_COST_WHILE_SLEEP = "COST_WHILE_SLEEP";
    public final static String JSON_DRUG_EFFECT_PERCENT = "DRUG_EFFECT_PERCENT";
    public final static String JSON_NPC_FAVOR_PERCENT = "NPC_FAVOR_PERCENT";
    public final static String JSON_PHYSICAL_INJURY_PERCENT = "PHYSICAL_INJURY_PERCENT";
    public final static String JSON_THREAT_FROM_A_WARRIOR = "THREAT_FROM_A_WARRIOR";
    public final static String JSON_NAME = "NAME";
    public final static String JSON_ID = "ID";
    public final static String JSON_TALKER = "TALKER";
    public final static String JSON_CONTENT = "CONTENT";
    public final static String JSON_CHOICES = "CHOICES";
    public final static String JSON_HINT = "HINT";
    public final static String JSON_REQIURE = "REQIURE";
    public final static String JSON_PLACE = "PLACE";
    public final static String JSON_TERRAIN = "TERRAIN";
    public final static String JSON_ISFINISHED = "ISFINISHED";
    public final static String JSON_REQUIRE = "REQUIRE";
    public final static String JSON_MONSTER = "MONSTER";
    public final static String JSON_PERCENT = "PERCENT";
    public final static String JSON_CON = "CON";
    public final static String JSON_STR = "STR";
    public final static String JSON_DEX = "DEX";
    public final static String JSON_HUNGRY = "HUNGRY";
    public final static String JSON_DROPOUT = "DROPOUT";
    public final static String JSON_THING = "THING";
    public final static String JSON_TYPE = "TYPE";
    public final static String JSON_REWARD = "REWARD";
    public final static String JSON_UNIT = "UNIT";
    public final static String JSON_GOT = "GOT";
    public final static String JSON_CONDITION = "CONDITION";
    public final static String JSON_TEXT = "text";
    public final static String JSON_VALUE = "value";
    public final static String JSON_TASK = "TASK";
    public final static String JSON_NPC = "NPC";
    public final static String JSON_EFFECT = "EFFECT";
    public final static String JSON_MAXCON = "MAXCON";
    public final static String JSON_MAXHUNGRY = "MAXHUNGRY";
    public final static String JSON_POSITION = "POSITION";
    public final static String JSON_NOTE = "NOTE";
    public final static String JSON_PREFIX = "PREFIX";
    public final static String JSON_OWNTHINGS = "OWNTHINGS";
    public final static String JSON_DEFEAT = "DEFEAT";
    public final static String JSON_FEATURE = "FEATURE";
    public final static String JSON_PRICE = "PRICE";

    /*
     * 物品类型
     */
    public final static String ITEM_TYPE_EFFECT = "EFFECT"; // 效果物品
    public final static String ITEM_TYPE_TASK = "TASK"; // 任务物品
    public final static String ITEM_TYPE_MEDICINE = "MEDICINE"; // 药物物品
    public final static String ITEM_TYPE_CURSE = "CURSE"; // 诅咒物品
    public final static String ITEM_TYPE_REDEMPTION = "REDEMPTION"; // 救赎物品
    public final static String ITEM_TYPE_NOTE = "NOTE"; // 消息物品
    public final static String ITEM_TYPE_FOOD = "FOOD"; // 食物物品
    public final static String ITEM_TYPE_EQUIPMENT_ON = "EQUIPMENT_ON"; // 已装备物品
    public final static String ITEM_TYPE_EQUIPMENT_OFF = "EQUIPMENT_OFF"; // 未装备物品
    public final static String ITEM_TYPE_THE_END = "THE_END"; // 结局象征物品
    public final static String ITEM_TYPE_SOUL = "SOUL"; // 灵魂物品
    public final static String ITEM_TYPE_SUDDEN = "SUDDEN"; // 突发物品
    public final static String ITEM_TYPE_RECRUIT = "RECRUIT"; // 补给物品
    public final static String ITEM_TYPE_GIFT = "GIFT"; // 礼包物品

    /*
     * 效果
     */
    public final static String EFFECT_FULL_HEALTH = "FULL_HEALTH";

    /*
     * ACTION中的KEY
     */
    public final static String ACTION_EAST = "东";
    public final static String ACTION_NORTH = "北";
    public final static String ACTION_WEST = "西";
    public final static String ACTION_SOUTH = "南";

    public final static Double CURSE_RATIO = 0.2;

    public static boolean isIp(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    /*
     * 按概率返回遭遇的怪物/获得的物品
     */
    public static JSONArray random(JSONArray data,String type) throws JSONException {
        List<JSONArray> temp = new ArrayList<JSONArray>();

        // 按照概率组成可能遭遇到的怪物列表
        for(int i = 0;i<data.length();i++) {
            JSONObject item = data.getJSONObject(i);
            int percent = Integer.parseInt(item.getString(JSON_PERCENT));
            for(int j=0;j<percent;j++) {
                temp.add(item.getJSONArray(type));
            }
        }

        // 随机取出列表中的一组怪物
        Random random = new Random();
        int n = random.nextInt(temp.size());
        return temp.get(n);
    }

    public static String randomString(JSONArray data,String type) throws JSONException {
        List<String> temp = new ArrayList<String>();

        // 按照概率组成可能遭遇到的怪物列表
        for(int i = 0;i<data.length();i++) {
            JSONObject item = data.getJSONObject(i);
            int percent = Integer.parseInt(item.getString(JSON_PERCENT));
            for(int j=0;j<percent;j++) {
                temp.add(item.getString(type));
            }
        }

        // 随机取出列表中的一组怪物
        Random random = new Random();
        int n = random.nextInt(temp.size());
        return temp.get(n);
    }

    public static JSONObject randomJSONArray(JSONArray data) throws JSONException {
        List<JSONObject> temp = new ArrayList<JSONObject>();

        // 按照概率组成可能遭遇到的怪物列表
        for(int i = 0;i<data.length();i++) {
            JSONObject item = data.getJSONObject(i);
            int percent = Integer.parseInt(item.getString(JSON_PERCENT));
            for(int j=0;j<percent;j++) {
                temp.add(item);
            }
        }

        // 随机取出列表中的一个补给品
        Random random = new Random();
        int n = random.nextInt(temp.size());
        return temp.get(n);
    }

    public static JSONArray randomJSONArray(JSONArray data,String type) throws JSONException {
        List<JSONArray> temp = new ArrayList<JSONArray>();

        // 按照概率组成可能遭遇到的怪物列表
        for(int i = 0;i<data.length();i++) {
            JSONObject item = data.getJSONObject(i);
            int percent = Integer.parseInt(item.getString(JSON_PERCENT));
            for(int j=0;j<percent;j++) {
                temp.add(item.getJSONArray(type));
            }
        }

        // 随机取出列表中的一组怪物
        Random random = new Random();
        int n = random.nextInt(temp.size());
        return temp.get(n);
    }

    public static boolean randomDropout(int percent) { // 基于物品掉落百分比返回是否掉落
        Random random = new Random();
        return percent > random.nextInt(100);
    }

    // 低版本下利用反射实现API19后新增的JSONArray的remove功能
    public static void removeJSONArray(int index, JSONArray array) throws Exception{
        if(index < 0)
            return;
        Field valuesField=JSONArray.class.getDeclaredField("values");
        valuesField.setAccessible(true);
        List<Object> values=(List<Object>)valuesField.get(array);
        if(index >= values.size())
            return;
        values.remove(index);
    }

    /*
     * 从String列表中随机取出指定数目的值
     */
    public static List<String> randomPosition(List<String> ids,int num) {
        if(ids.size() < num) {
            return ids;
        }
        Random r = new Random();
        List<String> ret = new ArrayList<String>();
        int index = 0;
        for (int i = 0; i < num; i++) {
            index = r.nextInt(ids.size() - i);
            ret.add(ids.get(index));
            ids.remove(index);
        }
        return ret;
    }
}
