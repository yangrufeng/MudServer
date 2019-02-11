package com.yangrufeng.mud.mudserver.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.yangrufeng.mud.mudserver.MainActivity;
import com.yangrufeng.mud.mudserver.R;
import com.yangrufeng.mud.mudserver.common.Common;
import com.yangrufeng.mud.mudserver.entity.Equipment;
import com.yangrufeng.mud.mudserver.entity.Feature;
import com.yangrufeng.mud.mudserver.entity.Fight;
import com.yangrufeng.mud.mudserver.entity.Item;
import com.yangrufeng.mud.mudserver.entity.Place;
import com.yangrufeng.mud.mudserver.entity.Player;
import com.yangrufeng.mud.mudserver.entity.Task;
import com.yangrufeng.mud.mudserver.entity.Thing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MainFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MainFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MainFragment extends Fragment implements MainActivity.FragmentBackListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private TextView logTextView;
    private TableLayout tableLayout;

    private String currentMoveId;
    private class CompareEntity {
        private String type;
        private String id;
        private int dex;
        private int getStr() {
            return str;
        }

        private void setStr(int str) {
            this.str = str;
        }

        private int str;

        private int xh;
        private int turn;

        private int getTurn() {
            return turn;
        }

        private void setTurn(int turn) {
            this.turn = turn;
        }

        private String getType() {
            return type;
        }

        private void setType(String type) {
            this.type = type;
        }

        private String getId() {
            return id;
        }

        private void setId(String id) {
            this.id = id;
        }

        private int getDex() {
            return dex;
        }

        private void setDex(int dex) {
            this.dex = dex;
        }

        private int getXh() {
            return xh;
        }

        private void setXh(int xh) {
            this.xh = xh;
        }
    }
    public MainFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment MainFragment.
//     */
//    public static MainFragment newInstance(String param1, String param2) {
//        MainFragment fragment = new MainFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    /*
     * 刷新主页面中用户按钮列表
     */
    private void refreshPlayer(String currentPlayerId) {
        /*
         * 在主页面中将当前行动玩家对应按钮背景色修改为绿色
         */
        int count = gridLayout.getColumnCount();
        for(int j=0;j<count;j++) {
            Button btn = (Button)gridLayout.getChildAt(j);
            String ip = (String)btn.getTag();
            if(playerMap.get(ip).getIp().equals(currentPlayerId)) {
                btn.setBackgroundColor(ContextCompat.getColor(this.getContext(),R.color.green));
            } else {
                btn.setBackgroundColor(ContextCompat.getColor(this.getContext(),R.color.gray));
            }
        }
    }

    /*
     * 刷新侧滑页面中的地图信息
     */
    private void refreshMap() {
        tableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);//设置分割线为中间显示
        int nodeSize = nodes.length();
        List<String> idList = new ArrayList<>();
        List<JSONObject> nodeList = new ArrayList<>();
        for(int i=0;i<nodeSize;i++) {
            try {
                JSONObject node = nodes.getJSONObject(i);
                String id = node.getString(Common.JSON_ID);
                idList.add(id);
                nodeList.add(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(nodeList, new Comparator<JSONObject>() { // 按照地图节点ID由大到小排序地图
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    String a = o1.getString(Common.JSON_ID);
                    String b = o2.getString(Common.JSON_ID);
                    return a.compareTo(b);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        Collections.sort(idList); // 将节点ID进行由小至大的排序
        String maxId = idList.get(idList.size()-1);
        int rowNum = Integer.parseInt(maxId.split("-")[0]);
        int columnNum = Integer.parseInt(maxId.split("-")[1]);
        tableLayout.removeAllViews();
        for (int i = 0; i < rowNum; i++) {
            TableRow tablerow = new TableRow(getActivity());
            for(int j=0;j<columnNum;j++) {
                JSONObject node = nodeList.get(i*columnNum+j);
                final Button position = new Button(getActivity());
                try {
                    if(mod.equals(Common.MOD_SURVIVAL) && 0!=node.getInt(Common.SURVIVAL_POISON_NUM)) {
                        position.setText(new StringBuilder(node.getString(Common.JSON_NAME)).append("\n").append(node.getInt(Common.SURVIVAL_POISON_NUM)).toString());
                    } else {
                        position.setText(node.getString(Common.JSON_NAME));
                    }

                    position.setTag(node.getString(Common.JSON_ID));
                    if(mod.equals(Common.MOD_SURVIVAL)) {
                        if (node.getBoolean(Common.SURVIVAL_POISONING)) {
                            position.setTextColor(Color.GREEN);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(String ip : playerMap.keySet()) {
                    Player player = playerMap.get(ip);
                    Place place = player.getPosition();
                    if(place == null) continue;
                    try {
                        if(place.getId().equals(node.getString(Common.JSON_ID))) {
                            position.setBackgroundColor(ContextCompat.getColor(this.getContext(),R.color.red));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
                layoutParams.width = 0;
                layoutParams.weight = 1;
                position.setLayoutParams(layoutParams);
                position.setOnClickListener(new View.OnClickListener() {
                    String pId = (String)position.getTag();

                    @Override
                    public void onClick(View v) {
                        JSONObject node = null;
                        int nodeSize = nodes.length();
                        for(int i=0;i<nodeSize;i++) {
                            try {
                                node = nodes.getJSONObject(i);
                                if(pId.equals(node.getString(Common.JSON_ID))) {
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        final LinearLayout l = new LinearLayout(getActivity());
                        l.setOrientation(LinearLayout.VERTICAL);
                        final TextView name = new TextView(getActivity());
                        try {
                            assert node != null;
                            name.setText(String.format(getResources().getString(R.string.address_text),node.getString(Common.JSON_NAME)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final TextView des = new TextView(getActivity());
                        try {
                            des.setText(String.format(getResources().getString(R.string.des_text),node.getString(Common.JSON_DESC)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        StringBuilder sb = new StringBuilder();
                        for(String ip : playerMap.keySet()) {
                            Player player = playerMap.get(ip);
                            Place place = player.getPosition();
                            if(place==null) continue;
                            try {
                                if(place.getId().equals(node.getString(Common.JSON_ID))) {
                                    sb.append("[").append(player.getName()).append("]");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        final TextView whosThere = new TextView(getActivity());
                        whosThere.setText(sb.toString());
                        l.addView(name);
                        l.addView(des);
                        if(mod.equals(Common.MOD_SURVIVAL)) {
                            final TextView poison = new TextView(getActivity());
                            try {
                                poison.setText(String.format(getResources().getString(R.string.poison_text),node.getInt(Common.SURVIVAL_POISON_NUM)));
                                l.addView(poison);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        l.addView(whosThere);
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle("位置信息")
                                .setIcon(R.mipmap.ic_launcher)
                                .setView(l)
                                .create();
                        alertDialog.show();
                    }
                });
                tablerow.addView(position);
            }
            tableLayout.addView(tablerow,new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        DrawerLayout drawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        tableLayout = (TableLayout) v.findViewById(R.id.tableLayout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                Log.d("wyy","侧拉菜单打开了");

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                Log.d("wyy","侧拉菜单关闭了");
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        logTextView = (TextView)v.findViewById(R.id.log);
        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        modConfig = new HashMap<>();
        gridLayout = (GridLayout) v.findViewById(R.id.playerGrid);
        toAllButton = (Button)v.findViewById(R.id.toAll);
        toAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LinearLayout l = new LinearLayout(getActivity());
                l.setOrientation(LinearLayout.VERTICAL);
                final EditText et = new EditText(getActivity());
                l.addView(et);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("发送信息")
                        .setIcon(R.mipmap.ic_launcher)
//                                        .setItems(items, new DialogInterface.OnClickListener() {//添加列表
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                Toast.makeText(getActivity(), "点的是：" + items[i], Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
                        .setView(l)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String msg = et.getText().toString();
                                switch(mod) {
                                    case Common.MOD_SURVIVAL:
                                        String[] params = msg.split(Common.ORDER_SEPARATOR);
                                        if(params.length==5) {
                                            int intervalTurn = Integer.parseInt(params[0]);
                                            int randomNum = Integer.parseInt(params[1]);
                                            int poisonNum = Integer.parseInt(params[2]);
                                            int randomTurn = Integer.parseInt(params[3]);
                                            int profiteerTurn = Integer.parseInt(params[4]);
                                            modConfig.put(Common.SURVIVAL_POISON_INTERVAL_TURN,intervalTurn);
                                            modConfig.put(Common.SURVIVAL_RANDOM_NUM,randomNum);
                                            modConfig.put(Common.SURVIVAL_POISON_NUM,poisonNum);
                                            modConfig.put(Common.SURVIVAL_DROPOUT_INTERVAL_TURN,randomTurn);
                                            modConfig.put(Common.SURVIVAL_PROFITEER_INTERVAL_TURN,profiteerTurn);
                                            msg = "大逃杀游戏配置更改如下：每"+intervalTurn+"回合开启一次投放毒雾，每次随机投放"+randomNum+"个地点，每次投放毒雾的量为"+poisonNum+"，每"+randomTurn+"回合随机掉落补给物品，奸商每"+profiteerTurn+"回合移动一次";
                                        } else {
                                            msg ="服务器向所有人发送信息："+msg;
                                        }


                                        for(Socket s : clients) {
                                            try {
                                                OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
                                                BufferedWriter bw = new BufferedWriter(osw);
                                                bw.write( Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+msg + "\n");
                                                bw.flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                logTextView.append("【"+getNowTime()+"】");
                                logTextView.append("服务器向所有玩家发送信息："+msg+"\n");
                            }
                        }).setNegativeButton("取消",null)//.show()
                        .create();
                alertDialog.show();
            }
        });
        playerNum = ((MainActivity)getActivity()).getPlayerNum();
        mod = ((MainActivity)getActivity()).getMod();
        Toast.makeText(getActivity(),"你创建了包含【"+mod+"】MOD的"+playerNum+"人游戏！",Toast.LENGTH_LONG).show();
        initTCPService();
        roles = readJsonArray("role.json");
        nodes = readJsonArray("NoviceVillage.json");
        recruits = readJsonArray("recruit.json");
        List<String> ids = new ArrayList<>(); // 地点ID列表
        if(mod.equals(Common.MOD_SURVIVAL)) { // 大逃杀MOD初始化毒圈
            for(int i=0;i<nodes.length();i++) {
                JSONObject node;
                try {
                    node = nodes.getJSONObject(i);
                    node.put(Common.SURVIVAL_POISONING,false);
                    node.put(Common.SURVIVAL_POISON_NUM,0);
                    ids.add(node.getString(Common.JSON_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /*
             * 初始化大逃杀MOD配置(默认每20回合投毒一次，每次随机五个点，投毒量为10,投毒次数初始化清零,全图随机投毒，每10回合随机掉落补给品，奸商没10回合移动一次)
             */
            modConfig.put(Common.SURVIVAL_POISON_INTERVAL_TURN,20);
            modConfig.put(Common.SURVIVAL_RANDOM_NUM,5);
            modConfig.put(Common.SURVIVAL_POISON_NUM,10);
            modConfig.put(Common.SURVIVAL_POISON_TURN,0);
            modConfig.put(Common.SURVIVAL_DROPOUT_INTERVAL_TURN,20);
            modConfig.put(Common.SURVIVAL_RANDOM_TURN,0);
            modConfig.put(Common.SURVIVAL_POISON_SCOPE,ids);
            modConfig.put(Common.SURVIVAL_PROFITEER_TURN,0);
            modConfig.put(Common.SURVIVAL_PROFITEER_INTERVAL_TURN,10);
        }
        events = readJsonObject("event.json");
        fights = readJsonObject("fight.json");
        monsters = readJsonObject("monster.json");
        dialogs = readJsonObject("talk.json");
        tasks = readJsonObject("task.json");
        things = readJsonObject("thing.json");
        npcs = readJsonObject("npc.json");
        hiddens = readJsonObject("hidden.json");
        notes = readJsonObject("note.json");
        suddens = readJsonObject("sudden.json");
        fightMap = new HashMap<>();
        onFightMap = new HashMap<>();
        turnList = new ArrayList<>();
        mServiceThread.start();
        return v;
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).setBackListener(this);
        ((MainActivity) getActivity()).setInterception(true);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
        ((MainActivity) getActivity()).setBackListener(null);
        ((MainActivity) getActivity()).setInterception(false);
    }

    @Override
    public void onBackForward() {
        dialog_Exit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    private interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }
    private static String tag="MainFragment";
    private int playerNum;
    private String mod;
    private Map<String,Object> modConfig;
    private ServerSocket mServerSocket;
    private ServiceThreadHost mServiceThread;
    private boolean closed = false;
    private List<Socket> clients;
    private Map<String,String> nickMap;
    private Map<String,String> uuidMap;
    private Map<String,String> roleMap;
    private JSONArray roles; // 角色列表
    private JSONArray nodes; // 地图节点
    private JSONArray recruits; // 补给品
    private JSONObject events; // 事件
    private JSONObject fights; // 战斗
    private JSONObject monsters; // 怪物
    private JSONObject dialogs; // 对话
    private JSONObject tasks; // 任务
    private JSONObject things; // 物品
    private JSONObject npcs; // NPC
    private JSONObject suddens; // 触发
    private JSONObject hiddens; // 隐藏选项
    private JSONObject notes; // 提示
    private Map<String,Fight> fightMap; // 玩家与战斗的映射MAP（多个玩家可以参加同一场战斗但同时只能参加一场战斗）
    private Map<String,Player> playerMap;
    private GridLayout gridLayout;
    private Button toAllButton;
    private Map<String,JSONArray> onFightMap; // 战斗与怪物的映射Map(一场战斗可以有多个怪物)
    private List<CompareEntity> turnList; // 多人行动次序列表

    /**
     * 初始化ServerSocket，buffer分配内存
     */
    public void initTCPService(){
        try {
            mServerSocket = new ServerSocket(Common.HOST_PORT);
            clients = new ArrayList<>();
            uuidMap = new HashMap<>();
            nickMap = new HashMap<>();
            nickMap.put("0.0.0.0","所有人");
            roleMap = new HashMap<>();
            playerMap = new HashMap<>();
            Log.i(tag, "Service启动监听...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mServiceThread == null){
            mServiceThread = new ServiceThreadHost();
        }
    }
    private class ServiceThreadHost extends Thread{
//--TODO:断开网络后重新链接
        @Override
        public void run() {
            while(!closed){
                Socket client;
                try {
                    Log.i(tag, "Service监听到消息...");
                    client = mServerSocket.accept();
                    for(Socket s : clients) {
                        if(s.getInetAddress().equals(client.getInetAddress())) {
                            clients.remove(s);
                            break;
                        }
                    }

                    if(clients.size() > playerNum) { // 超过人数后无法加入
                        client.close();
                        continue;
                    } else {
                        clients.add(client);
                        InputStream inputStream = client.getInputStream();
                        OutputStream outputStream = client.getOutputStream();
                        new ServerThread(client,inputStream,outputStream).start();
                    }
                    if(clients.size()==playerNum) {
                        if (mod.equals(Common.MOD_SURVIVAL)) { // 显示大逃杀默认设置
                            int intervalTurn = Integer.parseInt(modConfig.get(Common.SURVIVAL_POISON_INTERVAL_TURN).toString());
                            int randomNum = Integer.parseInt(modConfig.get(Common.SURVIVAL_RANDOM_NUM).toString());
                            int poisonNum = Integer.parseInt(modConfig.get(Common.SURVIVAL_POISON_NUM).toString());
                            int randomTurn = Integer.parseInt(modConfig.get(Common.SURVIVAL_DROPOUT_INTERVAL_TURN).toString());
                            int profiteerTurn = Integer.parseInt(modConfig.get(Common.SURVIVAL_PROFITEER_INTERVAL_TURN).toString());
                            String msg = "大逃杀游戏配置初始化如下：每" + intervalTurn + "回合开启一次投放毒雾，每次随机投放" + randomNum + "个地点，每次投放毒雾的量为" + poisonNum + "，每" + randomTurn + "回合随机掉落补给物品，奸商每"+profiteerTurn+"回合移动一次";
                            for (Socket s : clients) {
                                try {
                                    OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
                                    BufferedWriter bw = new BufferedWriter(osw);
                                    bw.write(Common.SERVER_OUTPUT + Common.SPLIT_SEPARATOR + msg + "\n");
                                    bw.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(tag, "mServerSocket异常！");
                }
            }
        }
    }
    /**
     * 获取屏幕宽度；
     */
    private int getScreenWidth(Context context) {
        return context.getApplicationContext().getResources()
                .getDisplayMetrics().widthPixels;
    }

    private JSONArray getCurrentPositionPlayers(String positionId) throws JSONException {
            /*
             * 获取当前地址所有玩家信息
             */
        JSONArray currentPlacePersions = new JSONArray();
        for(String ip : playerMap.keySet()) { // 查找所有本地玩家
            Player p = playerMap.get(ip);
            Place place = p.getPosition();
            if(place!=null&&place.getId().equals(positionId)) {
                currentPlacePersions.put(p.toJSONObject());
            }
        }
        return currentPlacePersions;
    }

    private List<String> getCurrentPositionPlayerIds(String positionId) {
            /*
             * 通知所有此地玩家的IP地址
             */
        List<String> notifyList = new ArrayList<>();
        for(String ip : playerMap.keySet()) { // 查找所有本地玩家
            Player p = playerMap.get(ip);
            Place place = p.getPosition();
            if(place!=null&&place.getId().equals(positionId)) {
                notifyList.add(ip);
            }
        }
        return notifyList;
    }
    private String getNowTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }
    private static class BlurHandler extends Handler {
        private final WeakReference<MainFragment> mTarget;
        private BlurHandler(MainFragment fragment) {
            mTarget = new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Common.SERVER_OUTPUT_FLAG: // 输出到消息列表中
                    mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                    mTarget.get().logTextView.append(msg.obj.toString().split(Common.SPLIT_SEPARATOR)[1]);
                    break;
                case Common.SERVER_REFRESHMAP_FLAG:
                    mTarget.get().refreshMap();
                    break;
                case Common.SERVER_REFRESHPLAYER_FLAG:
                    mTarget.get().refreshPlayer(msg.obj.toString());
                    break;
                case Common.SERVER_SHOW_FLAG: // 显示玩家
                    GridLayout.LayoutParams layoutParams;
                    mTarget.get().gridLayout.removeAllViews(); // 先删除后添加（防止玩家重复加入游戏时按钮显示重叠）
                    int btnIndex = 0;
                    for (String ip : mTarget.get().playerMap.keySet()) {
                        final Button btn = new Button(mTarget.get().getActivity());
                        btn.setWidth(mTarget.get().getScreenWidth(mTarget.get().getActivity()) / 2);
                        btn.setText(mTarget.get().playerMap.get(ip).getName());
                        btn.setPadding(20, 5, 5, 5);// 设置的是内边距
                        btn.setTag(ip);
                        // 设置点击按钮实现数据的回传
                        btn.setOnClickListener(new View.OnClickListener() {
                            Player p = mTarget.get().playerMap.get((String)btn.getTag());
                            @Override
                            public void onClick(View v) {
//                                final String[] items = new String[]{"角色："+p.getName(),"体质（生命）："+p.getCon(),"力量（攻击力）："+p.getStr(),"敏捷（行动速度）："+p.getDex(),
//                                       " 饥饿值（行动力）："+p.getHungry(),"休息时消耗饥饿值："+p.getCost_while_sleep(),
//                                        "物理伤害百分比："+p.getPhysical_injury_percent(),"使用回复类药物效果："+p.getDrug_effect_percent(),"NPC好感度增长效果："+p.getNpc_favor_percent(),
//                                        "怪物的仇恨值："+p.getThreat_from_a_warrior(),"描述："+p.getDes()};//创建item
                                int turnNum = 0;
                                for(CompareEntity e : mTarget.get().turnList) { // 查看回合数
                                    if(e.getId().equals(p.getIp())) {
                                        turnNum = e.getTurn();
                                        break;
                                    }
                                }
                                final LinearLayout l = new LinearLayout(mTarget.get().getActivity());
                                l.setOrientation(LinearLayout.VERTICAL);
                                final TextView turn = new TextView(mTarget.get().getActivity());
                                turn.setText(String.format(mTarget.get().getResources().getString(R.string.turn_text),turnNum));
                                final TextView name = new TextView(mTarget.get().getActivity());
                                name.setText(String.format(mTarget.get().getResources().getString(R.string.name_text),p.getName()));
                                final TextView con = new TextView(mTarget.get().getActivity());
                                con.setText(String.format(mTarget.get().getResources().getString(R.string.con_text),p.getCon()));
                                final TextView str = new TextView(mTarget.get().getActivity());
                                str.setText(String.format(mTarget.get().getResources().getString(R.string.str_text),p.getStr()));
                                final TextView dex = new TextView(mTarget.get().getActivity());
                                dex.setText(String.format(mTarget.get().getResources().getString(R.string.dex_text),p.getDex()));
                                final TextView hungry = new TextView(mTarget.get().getActivity());
                                hungry.setText(String.format(mTarget.get().getResources().getString(R.string.hungry_text),p.getHungry()));
                                final TextView cost = new TextView(mTarget.get().getActivity());
                                cost.setText(String.format(mTarget.get().getResources().getString(R.string.cost_text),p.getCost_while_sleep()));
                                final TextView physical = new TextView(mTarget.get().getActivity());
                                physical.setText(String.format(mTarget.get().getResources().getString(R.string.physical_text),p.getPhysical_injury_percent()));
                                final TextView drug = new TextView(mTarget.get().getActivity());
                                drug.setText(String.format(mTarget.get().getResources().getString(R.string.drug_text),p.getDrug_effect_percent()));
                                final TextView npc = new TextView(mTarget.get().getActivity());
                                npc.setText(String.format(mTarget.get().getResources().getString(R.string.npc_text),p.getNpc_favor_percent()));
                                final TextView thread = new TextView(mTarget.get().getActivity());
                                thread.setText(String.format(mTarget.get().getResources().getString(R.string.thread_text),p.getThreat_from_a_warrior()));
                                final TextView des = new TextView(mTarget.get().getActivity());
                                des.setText(String.format(mTarget.get().getResources().getString(R.string.des_text),p.getDes()));
                                final EditText et = new EditText(mTarget.get().getActivity());
                                l.addView(turn);
                                l.addView(name);
                                l.addView(con);
                                l.addView(str);
                                l.addView(dex);
                                l.addView(hungry);
                                l.addView(cost);
                                l.addView(physical);
                                l.addView(drug);
                                l.addView(npc);
                                l.addView(thread);
                                l.addView(des);
                                l.addView(et);
                                AlertDialog alertDialog = new AlertDialog.Builder(mTarget.get().getActivity())
                                        .setTitle("角色信息")
                                        .setIcon(R.mipmap.ic_launcher)
//                                        .setItems(items, new DialogInterface.OnClickListener() {//添加列表
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                Toast.makeText(getActivity(), "点的是：" + items[i], Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
                                        .setView(l)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                for(Socket s : mTarget.get().clients) {
                                                    if(s.getInetAddress().toString().equals(p.getIp())) {
                                                        try {
                                                            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
                                                            BufferedWriter bw = new BufferedWriter(osw);
                                                            String message = Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+"服务器向你发送信息："+et.getText() + "\n";
                                                            if(et.getText().toString().startsWith(Common.SERVER_ORDER)) { // 服务器给玩家发送指令
                                                                String order = et.getText().toString();
                                                                String[] orderContents = order.split(Common.ORDER_SEPARATOR);
                                                                if(orderContents.length == 3) { // 防止命令输入错误时出现数组下标溢出
                                                                    String thingId = orderContents[1];
                                                                    int unit = Integer.parseInt(orderContents[2]);
                                                                    JSONArray thingsArray = mTarget.get().dropout(thingId,unit,p.getIp());
                                                                    message = Common.SERVER_DROPOUT+Common.SPLIT_SEPARATOR+thingsArray.toString()+ "\n";
                                                                    mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                                    mTarget.get().logTextView.append("给与玩家"+p.getName()+"物品：【"+mTarget.get().things.getJSONObject(thingId).getString(Common.JSON_NAME)+"】"+unit+"个\n");
                                                                }
                                                            } else if(et.getText().toString().startsWith(Common.SERVER_TURNON)) { // 仅用于通信问题造成的行动玩家等待其他玩家的情况
                                                                message = Common.SERVER_TURNON+Common.SPLIT_SEPARATOR+p.getHungry()+"\n";
                                                                mTarget.get().logTextView.append(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"通知玩家"+p.getName()+"移动\n");
                                                            } else if(et.getText().toString().startsWith(Common.SERVER_REBIRTH)) {
                                                                String order = et.getText().toString();
                                                                String[] orderContents = order.split(Common.ORDER_SEPARATOR);
                                                                String positionId;
                                                                if(orderContents.length == 3) { // 修改玩家属性
                                                                    String param = orderContents[1];
                                                                    int num = Integer.parseInt(orderContents[2]);
                                                                    switch(param) {
                                                                        case Common.JSON_CON:
                                                                            p.setMaxcon(p.getMaxcon()+num);
                                                                            p.setCon(p.getMaxcon()+num);
                                                                            mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                                            mTarget.get().logTextView.append("增加玩家"+p.getName()+"体质："+num+"\n");
                                                                            break;
                                                                        case Common.JSON_DEX:
                                                                            p.setDex(p.getDex()+num);
                                                                            mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                                            mTarget.get().logTextView.append("增加玩家"+p.getName()+"敏捷："+num+"\n");
                                                                            break;
                                                                        case Common.JSON_STR:
                                                                            p.setStr(p.getStr()+num);
                                                                            mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                                            mTarget.get().logTextView.append("增加玩家"+p.getName()+"力量："+num+"\n");
                                                                            break;
                                                                        case Common.JSON_HUNGRY:
                                                                            p.setMaxhungry(p.getMaxhungry()+num);
                                                                            p.setHungry(p.getMaxhungry()+num);
                                                                            mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                                            mTarget.get().logTextView.append("增加玩家"+p.getName()+"饥饿值："+num+"\n");
                                                                            break;
                                                                    }
                                                                    mTarget.get().updateTurn(Common.ROLETYPE_PLAYER,p.getIp(),mTarget.get().playerMap.keySet().size(),p.getDex(),p.getStr(),mTarget.get().getMaxTurn());
                                                                    message = Common.SERVER_REBIRTH+Common.SPLIT_SEPARATOR+p.toJSONObject()+"\n";
                                                                    positionId = p.getPosition().getId();
                                                                } else if(orderContents.length == 2) { // 复活NPC
                                                                    String npcId = orderContents[1];
                                                                    JSONObject npc = mTarget.get().npcs.getJSONObject(npcId);
                                                                    npc.put(Common.JSON_CON,npc.getInt(Common.JSON_MAXCON));
                                                                    positionId = npc.getString(Common.JSON_POSITION);
                                                                    mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                                    mTarget.get().logTextView.append("复活NPC："+npc.getString(Common.JSON_NAME)+"\n");
                                                                } else { // 复活当前玩家
                                                                    if(p.getCon() == 0 || p.getHungry() ==0) { // 确切死亡的玩家复活后影响在线玩家数
                                                                        mTarget.get().playerNum++;
                                                                    }
                                                                    p.setCon(p.getMaxcon());
                                                                    p.setHungry(p.getMaxhungry());
                                                                    mTarget.get().addTurn(Common.ROLETYPE_PLAYER,p.getIp(),mTarget.get().playerMap.keySet().size(),p.getDex(),p.getStr(),mTarget.get().getMaxTurn());
                                                                    message = Common.SERVER_REBIRTH+Common.SPLIT_SEPARATOR+p.toJSONObject()+"\n";
                                                                    positionId = p.getPosition().getId();
                                                                    mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                                    mTarget.get().logTextView.append("复活玩家："+p.getName()+"\n");
                                                                }
                                                                List<String> notifyList = mTarget.get().getCurrentPositionPlayerIds(positionId);
                                                                JSONArray currentPlacePersions = mTarget.get().getCurrentPositionPlayers(positionId);
                                                                Iterator<String> it = mTarget.get().npcs.keys();
                                                                while(it.hasNext()){ // 遍历所有NPC查找当前地址中的NPC
                                                                    String key = it.next();
                                                                    JSONObject npc = mTarget.get().npcs.getJSONObject(key);
                                                                    if(positionId.equals(npc.getString(Common.JSON_POSITION))) {
                                                                        npc.put(Common.JSON_ID,key);
                                                                        currentPlacePersions.put(npc);
                                                                    }
                                                                }
                                                                for(String ip : notifyList) { // 通知所有这个地址的玩家刷新人物列表
                                                                    for(Socket socket : mTarget.get().clients) {
                                                                        if(socket.getInetAddress().toString().equals(ip)) {
                                                                            osw = new OutputStreamWriter(s.getOutputStream());
                                                                            bw = new BufferedWriter(osw);
                                                                            bw.write( Common.SERVER_REFRESHPERSIONS+Common.SPLIT_SEPARATOR+currentPlacePersions.toString() + "\n");
                                                                            bw.flush();
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            bw.write(message);
                                                            bw.flush();
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    }
                                                }
                                                mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                                                mTarget.get().logTextView.append("服务器向玩家"+p.getName()+"发送信息："+et.getText()+"\n");
                                            }
                                        }).setNegativeButton("取消",null)//.show()
                                        .create();
                                alertDialog.show();
                            }
                        });
                        //使用Spec定义子控件的位置和比重
                        GridLayout.Spec rowSpec = GridLayout.spec(btnIndex / 2, 1f);
                        GridLayout.Spec columnSpec = GridLayout.spec(btnIndex % 2, 1f);
                        //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
                        layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                        layoutParams.height = 40;
                        layoutParams.width = mTarget.get().getScreenWidth(mTarget.get().getActivity()) /2;
//                        layoutParams.setMargins(5, 0, 5, 5);//设置btn的间距
                        layoutParams.setGravity(Gravity.LEFT);//设置btn位置（靠左）
                        mTarget.get().gridLayout.addView(btn, layoutParams);
                        btnIndex++;
                    }
                    mTarget.get().logTextView.append("【"+mTarget.get().getNowTime()+"】");
                    mTarget.get().logTextView.append(msg.obj.toString().split(Common.SPLIT_SEPARATOR)[1]);
                    break;
                default:
                    break;
            }
        }
    }
    private Handler myHandler = new BlurHandler(this);
//    Handler myHandler = new Handler() {
//        @Override
//        public void handleMessage(Message message) { // 更新UI
//            switch(message.what) {
//                case Common.SERVER_OUTPUT_FLAG: // 输出到消息列表中
//                    logTextView.append(message.obj.toString().split(Common.SPLIT_SEPARATOR)[1]);
//                    break;
//                case Common.SERVER_REFRESHMAP_FLAG:
//                    refreshMap();
//                    break;
//                case Common.SERVER_REFRESHPLAYER_FLAG:
//                    refreshPlayer(message.obj.toString());
//                    break;
//                case Common.SERVER_SHOW_FLAG: // 显示玩家
//                    GridLayout.LayoutParams layoutParams;
//                    gridLayout.removeAllViews(); // 先删除后添加（防止玩家重复加入游戏时按钮显示重叠）
//                    int btnIndex = 0;
//                    for (String ip : playerMap.keySet()) {
//                        final Button btn = new Button(getActivity());
//                        btn.setWidth(getScreenWidth(getActivity()) / 2);
//                        btn.setText(playerMap.get(ip).getName());
//                        btn.setPadding(20, 5, 5, 5);// 设置的是内边距
//                        btn.setTag(ip);
//                        // 设置点击按钮实现数据的回传
//                        btn.setOnClickListener(new View.OnClickListener() {
//                            Player p = playerMap.get((String)btn.getTag());
//                            @Override
//                            public void onClick(View v) {
////                                final String[] items = new String[]{"角色："+p.getName(),"体质（生命）："+p.getCon(),"力量（攻击力）："+p.getStr(),"敏捷（行动速度）："+p.getDex(),
////                                       " 饥饿值（行动力）："+p.getHungry(),"休息时消耗饥饿值："+p.getCost_while_sleep(),
////                                        "物理伤害百分比："+p.getPhysical_injury_percent(),"使用回复类药物效果："+p.getDrug_effect_percent(),"NPC好感度增长效果："+p.getNpc_favor_percent(),
////                                        "怪物的仇恨值："+p.getThreat_from_a_warrior(),"描述："+p.getDes()};//创建item
//                                int turnNum = 0;
//                                for(CompareEntity e : turnList) { // 查看回合数
//                                    if(e.getId().equals(p.getIp())) {
//                                        turnNum = e.getTurn();
//                                        break;
//                                    }
//                                }
//                                final LinearLayout l = new LinearLayout(getActivity());
//                                l.setOrientation(LinearLayout.VERTICAL);
//                                final TextView turn = new TextView(getActivity());
//                                turn.setText(String.format(getResources().getString(R.string.turn_text),turnNum));
//                                final TextView name = new TextView(getActivity());
//                                name.setText(String.format(getResources().getString(R.string.name_text),p.getName()));
//                                final TextView con = new TextView(getActivity());
//                                con.setText(String.format(getResources().getString(R.string.con_text),p.getCon()));
//                                final TextView str = new TextView(getActivity());
//                                str.setText(String.format(getResources().getString(R.string.str_text),p.getStr()));
//                                final TextView dex = new TextView(getActivity());
//                                dex.setText(String.format(getResources().getString(R.string.dex_text),p.getDex()));
//                                final TextView hungry = new TextView(getActivity());
//                                hungry.setText(String.format(getResources().getString(R.string.hungry_text),p.getHungry()));
//                                final TextView cost = new TextView(getActivity());
//                                cost.setText(String.format(getResources().getString(R.string.cost_text),p.getCost_while_sleep()));
//                                final TextView physical = new TextView(getActivity());
//                                physical.setText(String.format(getResources().getString(R.string.physical_text),p.getPhysical_injury_percent()));
//                                final TextView drug = new TextView(getActivity());
//                                drug.setText(String.format(getResources().getString(R.string.drug_text),p.getDrug_effect_percent()));
//                                final TextView npc = new TextView(getActivity());
//                                npc.setText(String.format(getResources().getString(R.string.npc_text),p.getNpc_favor_percent()));
//                                final TextView thread = new TextView(getActivity());
//                                thread.setText(String.format(getResources().getString(R.string.thread_text),p.getThreat_from_a_warrior()));
//                                final TextView des = new TextView(getActivity());
//                                des.setText(String.format(getResources().getString(R.string.des_text),p.getDes()));
//                                final EditText et = new EditText(getActivity());
//                                l.addView(turn);
//                                l.addView(name);
//                                l.addView(con);
//                                l.addView(str);
//                                l.addView(dex);
//                                l.addView(hungry);
//                                l.addView(cost);
//                                l.addView(physical);
//                                l.addView(drug);
//                                l.addView(npc);
//                                l.addView(thread);
//                                l.addView(des);
//                                l.addView(et);
//                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
//                                        .setTitle("角色信息")
//                                        .setIcon(R.mipmap.ic_launcher)
////                                        .setItems(items, new DialogInterface.OnClickListener() {//添加列表
////                                            @Override
////                                            public void onClick(DialogInterface dialogInterface, int i) {
////                                                Toast.makeText(getActivity(), "点的是：" + items[i], Toast.LENGTH_SHORT).show();
////                                            }
////                                        })
//                                        .setView(l)
//                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                for(Socket s : clients) {
//                                                    if(s.getInetAddress().toString().equals(p.getIp())) {
//                                                        try {
//                                                            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
//                                                            BufferedWriter bw = new BufferedWriter(osw);
//                                                            String message = Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+"服务器向你发送信息："+et.getText() + "\n";
//                                                            if(et.getText().toString().startsWith(Common.SERVER_ORDER)) { // 服务器给玩家发送指令
//                                                                String order = et.getText().toString();
//                                                                String[] orderContents = order.split(Common.ORDER_SEPARATOR);
//                                                                String thingId = orderContents[1];
//                                                                int unit = Integer.parseInt(orderContents[2]);
//                                                                JSONArray thingsArray = dropout(thingId,unit,p.getIp());
//                                                                message = Common.SERVER_DROPOUT+Common.SPLIT_SEPARATOR+thingsArray.toString()+ "\n";
//                                                            } else if(et.getText().toString().startsWith(Common.SERVER_TURNON)) { // 仅用于通信问题造成的行动玩家等待其他玩家的情况
//                                                                message = Common.SERVER_TURNON+Common.SPLIT_SEPARATOR+0+"\n";
//                                                            } else if(et.getText().toString().startsWith(Common.SERVER_REBIRTH)) {
//                                                                String order = et.getText().toString();
//                                                                String[] orderContents = order.split(Common.ORDER_SEPARATOR);
//                                                                String positionId;
//                                                                if(orderContents.length == 2) { // 复活NPC
//                                                                    String npcId = orderContents[1];
//                                                                    JSONObject npc = npcs.getJSONObject(npcId);
//                                                                    npc.put(Common.JSON_CON,npc.getInt(Common.JSON_MAXCON));
//                                                                    positionId = npc.getString(Common.JSON_POSITION);
//                                                                } else { // 复活当前玩家
//                                                                    if(p.getCon() == 0 || p.getHungry() ==0) { // 确切死亡的玩家复活后影响在线玩家数
//                                                                        playerNum++;
//                                                                    }
//                                                                    p.setCon(p.getMaxcon());
//                                                                    p.setHungry(p.getMaxhungry());
//                                                                    addTurn(Common.ROLETYPE_PLAYER,p.getIp(),playerMap.keySet().size(),p.getDex(),p.getStr(),getMaxTurn());
//                                                                    message = Common.SERVER_REBIRTH+Common.SPLIT_SEPARATOR+p.toJSONObject()+"\n";
//                                                                    positionId = p.getPosition().getId();
//                                                                }
//                                                                List<String> notifyList = getCurrentPositionPlayerIds(positionId);
//                                                                JSONArray currentPlacePersions = getCurrentPositionPlayers(positionId);
//                                                                Iterator<String> it = npcs.keys();
//                                                                while(it.hasNext()){ // 遍历所有NPC查找当前地址中的NPC
//                                                                    String key = it.next();
//                                                                    JSONObject npc = npcs.getJSONObject(key);
//                                                                    if(positionId.equals(npc.getString(Common.JSON_POSITION))) {
//                                                                        npc.put(Common.JSON_ID,key);
//                                                                        currentPlacePersions.put(npc);
//                                                                    }
//                                                                }
//                                                                for(String ip : notifyList) { // 通知所有这个地址的玩家刷新人物列表
//                                                                    for(Socket socket : clients) {
//                                                                        if(socket.getInetAddress().toString().equals(ip)) {
//                                                                            osw = new OutputStreamWriter(s.getOutputStream());
//                                                                            bw = new BufferedWriter(osw);
//                                                                            bw.write( Common.SERVER_REFRESHPERSIONS+Common.SPLIT_SEPARATOR+currentPlacePersions.toString() + "\n");
//                                                                            bw.flush();
//                                                                            break;
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                            bw.write(message);
//                                                            bw.flush();
//                                                        } catch (IOException | JSONException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                        break;
//                                                    }
//                                                }
//                                                logTextView.append("服务器向玩家"+p.getName()+"发送信息："+et.getText()+"\n");
//                                            }
//                                        }).setNegativeButton("取消",null)//.show()
//                                        .create();
//                                alertDialog.show();
//                            }
//                        });
//                        //使用Spec定义子控件的位置和比重
//                        GridLayout.Spec rowSpec = GridLayout.spec(btnIndex / 2, 1f);
//                        GridLayout.Spec columnSpec = GridLayout.spec(btnIndex % 2, 1f);
//                        //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
//                        layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
//                        layoutParams.height = 40;
//                        layoutParams.width = getScreenWidth(getActivity()) /2;
////                        layoutParams.setMargins(5, 0, 5, 5);//设置btn的间距
//                        layoutParams.setGravity(Gravity.LEFT);//设置btn位置（靠左）
//                        gridLayout.addView(btn, layoutParams);
//                        btnIndex++;
//                    }
//                    logTextView.append(message.obj.toString().split(Common.SPLIT_SEPARATOR)[1]);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
    private JSONArray readJsonArray(String jsonFile) {
        try {
            InputStreamReader isr = new InputStreamReader(getActivity().getAssets().open(jsonFile),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                line = replacePlaceholder(line);
                builder.append(line);
            }
            br.close();
            isr.close();
            return new JSONArray(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
    private JSONObject readJsonObject(String jsonFile) {
        try {
            InputStreamReader isr = new InputStreamReader(getActivity().getAssets().open(jsonFile),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                line = replacePlaceholder(line);
                builder.append(line);
            }
            br.close();
            isr.close();
            return new JSONObject(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private String replacePlaceholder(String line) {
        return line.replaceAll("%PLAYERNUM%",(playerNum-1)+"");
    }

    private int getMaxTurn() { // 获取目前行动队列中最大行动值
        int turn = 0;
        for(CompareEntity e:turnList) {
            if(e.getTurn() > turn) {
                turn = e.getTurn();
            }
        }
        return turn;
    }

    private synchronized void addTurn(String type,String id,int xh,int dex,int str, int turn) throws JSONException { // 新行动对象插入行动队列（玩家选完角色，怪物开始战斗）
        for(CompareEntity e:turnList) {
            if(type.equals(e.getType()) && type.equals(Common.ROLETYPE_PLAYER) // 对于玩家次序改变的情况进行先删除后插入的操作
                    && id.equals(e.getId())
                    && xh==e.getXh()) {
                turnList.remove(e);
                break;
            } else if(type.equals(e.getType()) && type.equals(Common.ROLETYPE_MONSTER) // 对于怪物已经存在的情况（证明有玩家加入了其他玩家的战斗），不再更新该怪物在行动列表中的信息
                    && id.equals(e.getId())
                    && xh==e.getXh()) {
                return;
            }
        }
        CompareEntity e = new CompareEntity();
        e.setId(id);
        e.setType(type);
        e.setDex(dex);
        e.setStr(str);
        e.setXh(xh);
        e.setTurn(turn);
        turnList.add(e);
    }
    private synchronized void updateTurn(String type,String id,int xh,int dex,int str,int turn) throws JSONException { // 行动对象敏捷/力量变更需要更新行动列表
        for(CompareEntity e:turnList) {
            if(e.getId().equals(id) && e.getXh() == xh) {
                turnList.remove(e);
                break;
            }
        }
        CompareEntity e = new CompareEntity();
        e.setId(id);
        e.setType(type);
        e.setDex(dex);
        e.setStr(str);
        e.setXh(xh);
        e.setTurn(turn);
        turnList.add(e);

    }
    private synchronized void removeFightMonster(String fightId) { // 战斗结束后从行动列表中删除怪物行动对象
        List<CompareEntity> removelist = new ArrayList<>();
        for(CompareEntity e:turnList) {
            if(e.getType().equals(Common.ROLETYPE_MONSTER) && e.getId().contains(fightId)) { // 基于怪物Id中包含战斗ID的特性，从行动列表中删除已经不再进行的战斗关联的怪物
                removelist.add(e);
            }
        }
        for(CompareEntity e : removelist)
        turnList.remove(e);
    }

    private synchronized void removeTurnObj(String id) {
        List<CompareEntity> temp = new ArrayList<>();
        for(CompareEntity e:turnList) {
            if(e.getId().contains(id)) {
                temp.add(e);
            }
        }
        for(CompareEntity e:temp) {
            turnList.remove(e);
        }
        if(turnList.size()==1 && mod.equals(Common.MOD_SURVIVAL)) {
            for(Socket socket : clients) {
                if(socket.getInetAddress().toString().equals(turnList.get(0).getId()))
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(Common.SERVER_SHOWDESCRIPT+Common.SPLIT_SEPARATOR+"WINNER WINNER ,CHICKEN DINNER!" + "\n");
                    bw.flush();
                } catch (Exception ex) {
                    System.out.println("发送消息发生异常");
                }
            }

        }
    }

    private synchronized CompareEntity nextTurnon() {
        // 按敏捷排序
        Collections.sort(turnList, new Comparator<CompareEntity>() {
            @Override
            public int compare(CompareEntity h1, CompareEntity h2) {
                return h1.turn-h2.turn==0?-(h1.getDex() - h2.getDex()==0?h1.getXh()-h2.getXh():h1.getDex() - h2.getDex()):-(h2.turn-h1.turn); // 同一回合数下敏捷越高的越先行动（回合数和敏捷均相等时以序号作为排序依据），反之回合数少的优先行动
            }
        });
        return turnList.get(0);
    }

    /*
     * 获取玩家是否有装备特性
     */
    private boolean gotFeature(Map<String,Thing> playerOwnThings,Feature feature) {
        for(String id : playerOwnThings.keySet()) {
            Thing thing = playerOwnThings.get(id);
            if(thing.getType().equals(Common.ITEM_TYPE_EQUIPMENT_ON) ||
                    thing.getType().equals(Common.ITEM_TYPE_EQUIPMENT_OFF)) {
                Equipment e = (Equipment) thing;
                if(e.getFeature().equals(feature)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * 获取NPC或怪物是否有装备特性
     */
    private boolean gotFeature(JSONArray freatures,Feature feature) throws JSONException {
        for(int i=0;i<freatures.length();i++) {
            String temp = freatures.getString(i);
            if(temp.equals(feature.getKey())) {
                return true;
            }
        }
        return false;
    }
    private void gotThing(Map<String,Thing> playerOwnThings,String thingId,int unit) throws JSONException {
        if(thingId.equals("MONEY")) { // 贪婪诅咒，让你获得金钱减少20%
            for(String id : playerOwnThings.keySet()) {
                if(id.equals("GREED")) {
                    unit = (int) Math.floor(unit*(1-Common.CURSE_RATIO));
                }
            }
        }
        if(thingId.contains("FAVOR")) { // 傲慢诅咒，让你获得NPC好感减少20%
            for(String id : playerOwnThings.keySet()) {
                if(id.equals("PRIDE")) {
                    unit = (int) Math.floor(unit*(1-Common.CURSE_RATIO));
                }
            }
        }
        if(playerOwnThings.containsKey(thingId)) {
            Thing item = playerOwnThings.get(thingId);
            item.setUnit(item.getUnit()+unit); // 同类物品堆叠
            playerOwnThings.put(thingId,item);
        } else {
            Thing item;
            if(things.getJSONObject(thingId).getString(Common.JSON_TYPE).equals(Common.ITEM_TYPE_EQUIPMENT_ON)
                    || things.getJSONObject(thingId).getString(Common.JSON_TYPE).equals(Common.ITEM_TYPE_EQUIPMENT_OFF) ) {
                JSONObject obj = things.getJSONObject(thingId);
                obj.put(Common.JSON_ID,thingId);
                item = new Equipment(things.getJSONObject(thingId));
                things.put(item.getId(),item.toJSONObject());
            } else {
                JSONObject obj = things.getJSONObject(thingId);
                obj.put(Common.JSON_ID,thingId);
                item = new Item(things.getJSONObject(thingId));
            }
            item.setUnit(unit);
            playerOwnThings.put(thingId,item);

        }
    }
    /*
     * 服务器向玩家刷物品（单一类型物品掉落）
     */
    private JSONArray dropout(String thingId,int unit,String ip) throws JSONException {
        Map<String,Thing> playerOwnThings = playerMap.get(ip).getOwnThings();
        JSONArray thingsArray = new JSONArray();
        gotThing(playerOwnThings,thingId,unit);
        for( String id : playerOwnThings.keySet()) {
            JSONObject thing = new JSONObject();
            Thing item = playerOwnThings.get(id);
            thing.put(Common.JSON_ID,id);
            thing.put(Common.JSON_NAME,item.getName());
            thing.put(Common.JSON_UNIT,item.getUnit());
            thing.put(Common.JSON_DESC,item.getDesc());
            thing.put(Common.JSON_TYPE,item.getType());
            thingsArray.put(thing);
        }
        return thingsArray;
    }

    private class ServerThread extends Thread{

        /*
         * 线程中的全局变量
         */
        private String msg;
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private List<String> triggers = new ArrayList<>();
        private JSONObject actions = new JSONObject();

        private ServerThread(Socket socket,InputStream inputStream,OutputStream outputStream){
            this.socket = socket;
            this.inputStream = inputStream;
            this.outputStream =  outputStream;
            msg = Common.SERVER_MOD+Common.SPLIT_SEPARATOR+mod;
            sendMsg(Common.SERVER_MOD);
        }

        @Override
        public void run() {
            String s;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                while(null != (s = in.readLine())){
                    try {
                        distributeMsg(s);
                    } catch(Exception e) {
                        Log.e("消息分发","错误发生");
                        showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+e.toString());
                        e.printStackTrace();
                    }
                }
                //当这个异常发生时，说明客户端那边的连接已经断开
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    inputStream.close();
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }

        /*
         * 通知一个地点的玩家更新人物列表
         */
        private void notifyRefreshPersionList(String positionId) throws JSONException {
//            JSONArray npcArray = new JSONArray();
//            for(int i=0;i<nodes.length();i++) {
//                JSONObject node = nodes.getJSONObject(i);
//                if(node.getString(Common.JSON_ID).equals(positionId)) {
//                    JSONArray triggerArray = node.getJSONArray(Common.JSON_TRIGGER); // 查找指定节点的触发事项
//                    for(int j = 0; j < triggerArray.length();j++) {
//                        //triggers.add(triggerArray.getString(j));
//                        if(triggerArray.getString(j).split((Common.CMD_SEPARATOR))[0].equals(Common.CMD_NPC)) {
//                            npc = npcs.getJSONObject(triggerArray.getString(j).split((Common.CMD_SEPARATOR))[1]);
//                            npc.put(Common.JSON_ID,triggerArray.getString(j).split((Common.CMD_SEPARATOR))[1]);
//                        }
//                    }
//                    break;
//                }
//            }
            List<String> notifyList = getCurrentPositionPlayerIds(positionId);
            JSONArray currentPlacePersions = getCurrentPositionPlayers(positionId);
//            if(npcArray.length()!=0) // 当前地址中的NPC
//                currentPlacePersions.put(npc);
            Iterator<String> it = npcs.keys();
            while(it.hasNext()){ // 遍历所有NPC查找当前地址中的NPC
                String key = it.next();
                JSONObject npc = npcs.getJSONObject(key);
                if(positionId.equals(npc.getString(Common.JSON_POSITION))) {
                    npc.put(Common.JSON_ID,key);
                    currentPlacePersions.put(npc);
                }
            }
            msg = Common.SERVER_REFRESHPERSIONS+Common.SPLIT_SEPARATOR+currentPlacePersions.toString();
            for(String ip : notifyList) { // 通知所有这个地址的玩家刷新人物列表
                sendMsg(ip);
            }
        }

        /*
         * 大逃杀模式下判断是否达成投毒条件
         */
        private boolean isPoison(int turn) {
            int poisonTurn = (int)modConfig.get(Common.SURVIVAL_POISON_TURN);
            int intervalTurn = (int)modConfig.get(Common.SURVIVAL_POISON_INTERVAL_TURN);
            if(turn >= intervalTurn*(poisonTurn+1)) {
                return true;
            } else {
                return false;
            }
        }

        /*
         * 随机投毒
         */
        private void randomPoison() throws JSONException {
            List<String> ids = (List<String>)modConfig.get(Common.SURVIVAL_POISON_SCOPE);
            int randomNum = (int)modConfig.get(Common.SURVIVAL_RANDOM_NUM);
            int poisonNum = (int)modConfig.get(Common.SURVIVAL_POISON_NUM);
            List<String> randomIds = Common.randomPosition(ids,randomNum);
            StringBuilder sb = new StringBuilder("本次投毒地点为：");
            for(int i=0;i<nodes.length();i++) {
                JSONObject node = nodes.getJSONObject(i);
                if(randomIds.contains(node.getString(Common.JSON_ID))) {
                    node.put(Common.SURVIVAL_POISONING,true);
                    node.put(Common.SURVIVAL_POISON_NUM,node.getInt(Common.SURVIVAL_POISON_NUM) + poisonNum);
                    sb.append("【").append(node.getString(Common.JSON_NAME)).append(Common.CMD_SEPARATOR).append(node.getString(Common.JSON_ID)).append("】");
                }
            }
            //showAllDes(sb.toString());
            msg = Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+ sb.toString();
            sendMsg();
            modConfig.put(Common.SURVIVAL_POISON_TURN,Integer.parseInt(modConfig.get(Common.SURVIVAL_POISON_TURN).toString())+1); // 记录投毒次数
    }

        private int getPoisonHurt(Player player) throws JSONException {
            String positionId = player.getPosition().getId();
            for(int i=0;i<nodes.length();i++) {
                JSONObject node = nodes.getJSONObject(i);
                if(node.getString(Common.JSON_ID).equals(positionId)) {
                    if(node.getBoolean(Common.SURVIVAL_POISONING)) {
                        return node.getInt(Common.SURVIVAL_POISON_NUM);
                    } else {
                        return 0;
                    }
                }
            }
            return 0;
        }

        private void poisonPersion(Player player) throws JSONException {
            int hurt = getPoisonHurt(player);
            if(hurt==0) {
                return;
            }
            for(String thingId : player.getOwnThings().keySet()) { // 暴怒诅咒，使你受到伤害增加20%
                if(thingId.equals("WRATH")) {
                    hurt = (int) Math.floor(hurt*(1+Common.CURSE_RATIO));
                    break;
                }
            }
            player.setCon(player.getCon()-hurt > 0 ? player.getCon()-hurt : 0);
            JSONObject fightTarget = new JSONObject();
            fightTarget.put(Common.FIGHT_SRC,player.toJSONObject());
            fightTarget.put(Common.FIGHT_TARGET,player.toJSONObject());
            fightTarget.put(Common.FIGHT_HURT,hurt);
            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"毒雾对"+player.getName()+"造成了"+hurt+"点伤害"+(player.getCon()==0?"造成其死亡":""));
            showDes("毒雾对你造成了"+hurt+"点伤害"+(player.getCon()==0?"造成你死亡":""));
            if(player.getCon()==0) { // 被攻击玩家死亡，将其移除出行动队列，杀死他的玩家获得其所有物品
                removeTurnObj(player.getIp());
                playerNum--; // 更新玩家数
            }
            List<String> notifyList = getCurrentPositionPlayerIds(player.getPosition().getId());
            for(String ip : notifyList) { // 通知所有这个地址的玩家刷新人物列表
                sendMsg(ip);
            }
            notifyRefreshPersionList(player.getPosition().getId());
        }

        private boolean isDropout(int turn) {
            int dropoutTurn = (int)modConfig.get(Common.SURVIVAL_RANDOM_TURN);
            int intervalTurn = (int)modConfig.get(Common.SURVIVAL_DROPOUT_INTERVAL_TURN);
            if(turn >= intervalTurn*(dropoutTurn+1)) {
                return true;
            } else {
                return false;
            }
        }

        private boolean isProfiteerMove(int turn) {
            int dropoutTurn = (int)modConfig.get(Common.SURVIVAL_PROFITEER_TURN);
            int intervalTurn = (int)modConfig.get(Common.SURVIVAL_PROFITEER_INTERVAL_TURN);
            if(turn >= intervalTurn*(dropoutTurn+1)) {
                return true;
            } else {
                return false;
            }
        }

        private void dropoutRecruit() throws JSONException {
            List<String> ids = (List<String>)modConfig.get(Common.SURVIVAL_POISON_SCOPE);
            String id = Common.randomPosition(ids,1).get(0);
            for(int i=0;i<nodes.length();i++) {
                JSONObject node = nodes.getJSONObject(i);
                if(node.getString(Common.JSON_ID).equals(id)) {
                    JSONArray triggers = node.getJSONArray(Common.JSON_TRIGGER);
                    triggers.put(triggers.length(),"EVENT|RECRUIT");
                    msg = Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+"补给品已经投放，投放位置为【"+node.getString(Common.JSON_NAME)+Common.CMD_SEPARATOR+node.getString(Common.JSON_ID)+"】";
                    sendMsg();
                    modConfig.put(Common.SURVIVAL_RANDOM_TURN,Integer.parseInt(modConfig.get(Common.SURVIVAL_RANDOM_TURN).toString())+1); // 记录补给品投放次数
                    break;
                }
            }
        }

        /*
         * 玩家重新加入游戏
         */
        private void joinGame(String ip) throws JSONException {
            Player currentPlayer = playerMap.get(ip);
            msg = Common.SERVER_REFRESHPLAYER+Common.SPLIT_SEPARATOR+currentPlayer.toJSONObject();
            sendMsg(Common.SERVER_REFRESHPLAYER); // 刷新玩家信息

            if(fightMap.containsKey(ip)) {
                Fight f = fightMap.get(ip); // 获取当前这场战斗
                if(onFightMap.containsKey(f.getId())) {
                    JSONObject result = new JSONObject();
                    result.put(Common.JSON_ID,f.getId());
                    result.put(Common.JSON_MONSTER,onFightMap.get(f.getId()));
                    msg = Common.SERVER_FIGHT+Common.SPLIT_SEPARATOR+result.toString();
                    sendMsg(Common.SERVER_FIGHT);
                    notifyRefreshFightList(f.getId()); // 刷新战斗列表
                }
            } else { // 不处于战斗中，给与玩家移动的选项
                action();
            }

            refreshPlayerItemList(currentPlayer); // 刷新物品列表

            notifyRefreshPersionList(currentPlayer.getPosition().getId()); // 刷新可互动的玩家及NPC列表

            Map<String,Task> taskMap = currentPlayer.getAcceptedTask();
            JSONArray tasks = new JSONArray();
            for(String id:taskMap.keySet()) {
                Task t = taskMap.get(id);
                tasks.put(t.toJSONObject());
            }
            msg = Common.SERVER_TASK+Common.SPLIT_SEPARATOR+tasks.toString();
            sendMsg(Common.SERVER_TASK); // 刷新任务列表

            if(!isMyTurn(ip)) { // 不是行动对象则等待其他玩家行动
                msg = Common.SERVER_WAIT+Common.SPLIT_SEPARATOR+0;
                sendMsg(Common.SERVER_WAIT);
            }
            showDes("重新链接到游戏");
        }

        private void profiteerMove() throws JSONException {
            JSONObject profiteer = npcs.getJSONObject("PROFITEER");
            if(profiteer.getInt(Common.JSON_CON) > 0) { // 只要奸商还活着就随机移动
                List<String> ids = (List<String>)modConfig.get(Common.SURVIVAL_POISON_SCOPE);
                String id = Common.randomPosition(ids,1).get(0);
                profiteer.put(Common.JSON_POSITION,id);

                /*
                 * 随着奸商移动，更新奸商提供任务的任务提交地址
                 */
                JSONObject giftTask = tasks.getJSONObject("0-0");
                giftTask.put(Common.JSON_PLACE,id);
                for(int i=0;i<nodes.length();i++) {
                    JSONObject node = nodes.getJSONObject(i);
                    if(node.getString(Common.JSON_ID).equals(id)) {
                        msg = Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+"奸商出现在"+node.getString(Common.JSON_NAME)+Common.CMD_SEPARATOR+node.getString(Common.JSON_ID);
                        sendMsg();
                        modConfig.put(Common.SURVIVAL_PROFITEER_TURN,Integer.parseInt(modConfig.get(Common.SURVIVAL_PROFITEER_TURN).toString())+1); // 记录奸商移动次数
                        break;
                    }
                }
            }
        }

        private boolean isMyTurn(String ip) {
            return ip.equals(currentMoveId);
        }
        /*
         * 处理客户端请求信息
         */
        private void distributeMsg(String s) throws Exception {
            String[] arry = s.split(Common.SPLIT_SEPARATOR);
            if(arry.length==2) {
                if(arry[0].equals(Common.CLIENT_WAIT) || arry[0].equals(Common.CLIENT_MOVE) || arry[0].equals(Common.CLIENT_ESCAPE)  // 消耗玩家行动力的操作
                        || arry[0].equals(Common.CLIENT_FIGHT) || arry[0].equals(Common.CLIENT_FIGHT_PERSION)) {
                    for(CompareEntity e : turnList) {
                        if(e.getId().equals(socket.getInetAddress().toString())) {
                            e.setTurn(e.getTurn()+1); // 回合数加一
                            Player player = null;
                            if(mod.equals(Common.MOD_SURVIVAL)) {
                                if(isDropout(e.getTurn())) { // 大逃杀模式下判断是否达成投放补给品条件
                                    dropoutRecruit();
                                }
                                if(isProfiteerMove(e.getTurn())) { // 大逃杀模式下判断是否达成奸商移动条件
                                    profiteerMove();
                                }
                                if(e.getType().equals(Common.ROLETYPE_PLAYER)) {
                                    for(String playerId:playerMap.keySet()) {
                                        if(e.getId().equals(playerId)) {
                                            player = playerMap.get(playerId);
                                        }
                                    }
                                    if(player==null) {
                                        break;
                                    }
                                    if(player.getPosition()==null) { // 首次移动时
                                        Place place = new Place();
                                        place.setId(arry[1]);
                                        player.setPosition(place);
                                    }
                                    poisonPersion(player);
                                    if(isPoison(e.getTurn())) { // 大逃杀模式下判断是否达成投毒条件
                                        randomPoison();
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                switch(arry[0]) {
                    case Common.CLIENT_INPUT: // 处理用户输入消息请求
                        String[] chats = arry[1].split(Common.CMD_SEPARATOR);
                        String messageString = "";
                        if(chats.length==2) {
                            messageString = chats[1];
                        }
                        msg = Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+"【" + nickMap.get(socket.getInetAddress().toString()) + "】对【" + nickMap.get(chats[0]) + "】 说：" + messageString;
                        if(chats[0].equals("0.0.0.0")) {
                            sendMsg();
                        } else {
                            sendMsg(chats[0]);
                        }

                        break;
                    case Common.CLIENT_NICKNAME: // 处理用户提交昵称请求
                        String[] markers = arry[1].split(Common.ITEM_SEPARATOR);
                        nickMap.put(socket.getInetAddress().toString(),markers[0]);

                        /*
                         * 遍历所有链接到服务器的玩家的手机UUID，防止由于玩家断线重连IP变化导致的玩家数量异常
                         */

                        for(String uuid:uuidMap.keySet()) {
                            if(uuid.equals(markers[1])) { // 玩家手机已经登陆过这次游戏中
                                for(Socket temp :clients) {
                                    if(temp.getInetAddress().toString().equals(uuidMap.get(uuid))
                                            && !socket.getInetAddress().toString().equals(temp.getInetAddress().toString())) { // UUID相同使用了不同IP登陆
                                        clients.remove(socket);
                                        break;
                                    }
                                }
                            }
                        }
                        uuidMap.put(markers[1],socket.getInetAddress().toString());

                        msg = Common.SERVER_OUTPUT+Common.SPLIT_SEPARATOR+"欢迎【" + markers[0] + "】进入游戏！当前游戏中玩家有【"
                            + clients.size() + "】人";
                        sendMsg();
                        break;
                    case Common.CLIENT_USERS: // 处理用户查询其它用户昵称请求
                        JSONArray users = new JSONArray();
                        JSONObject all = new JSONObject();
                        all.put("value","0.0.0.0");
                        all.put("text","所有人");
                        users.put(all);
                        for(Socket socket : clients) {
                            if(socket.getInetAddress().toString().equals(this.socket.getInetAddress().toString())) {
                                continue;
                            }
                            JSONObject user = new JSONObject();

                            user.put("value",socket.getInetAddress().toString());
                            user.put("text",nickMap.get(socket.getInetAddress().toString()));
                            users.put(user);
                        }
                        msg = Common.SERVER_USERS+Common.SPLIT_SEPARATOR+users.toString();
                        sendMsg(Common.SERVER_USERS);
                        break;
                    case Common.CLIENT_ROLES: // 用户查看可选角色，基于用户名返回所有角色信息（隐藏用户只有在角色名称包含用户名时才返回）
                        if(roleMap.containsKey(socket.getInetAddress().toString())) { // 玩家已经加入过游戏，当前为退出后再次加入
                            joinGame(socket.getInetAddress().toString());
                            break;
                        }
                        JSONArray tempArray = new JSONArray();
                        for(int i=0;i<roles.length();i++) {
                            JSONObject obj = (JSONObject)roles.get(i);
                            if(obj.getString("NAME").contains(arry[1])) {
                                obj.put("IP",socket.getInetAddress().toString());
                                tempArray.put(obj);
                                break;
                            }
                        }
                        if(tempArray.length()==0) {
                            for(int i=0;i<roles.length();i++) {
                                JSONObject obj = (JSONObject)roles.get(i);
                                if(!obj.getString("NAME").contains("隐藏")) {
                                    obj.put("IP",socket.getInetAddress().toString());
                                    tempArray.put(obj);
                                }
                            }
                        }
                        msg = Common.SERVER_ROLES+Common.SPLIT_SEPARATOR+tempArray.toString();
                        sendMsg(Common.SERVER_ROLES);
                        break;
                    case Common.CLIENT_PICKROLE: // 用户选择了角色，更新界面上的用户显示区域
                        JSONObject role = null;
                        for(int k=0;k<roles.length();k++) {
                            role = (JSONObject)roles.get(k);
                            if(role.getString("ID").equals(arry[1])) {

                                /*
                                 * 已经被选择了的角色不能被重复选择
                                 */
                                for(String ip : roleMap.keySet()) {
                                    if(roleMap.get(ip).equals(role.getString("NAME")) && !socket.getInetAddress().toString().equals(ip)) {
                                        msg = Common.SERVER_PICKROLE+Common.SPLIT_SEPARATOR+false;
                                        sendMsg(Common.SERVER_PICKROLE);
                                        return;
                                    }
                                }
                                msg = Common.SERVER_PICKROLE+Common.SPLIT_SEPARATOR+arry[1];
                                sendMsg(Common.SERVER_PICKROLE);
                                roleMap.put(socket.getInetAddress().toString(),role.getString("NAME"));
                                Player player = new Player();
                                player.setIp(socket.getInetAddress().toString());
                                player.setName("【"+nickMap.get(socket.getInetAddress().toString())+"|"+roleMap.get(socket.getInetAddress().toString())+"】");
                                player.setCon(Integer.parseInt(role.getString(Common.JSON_CON)));
                                player.setMaxcon(Integer.parseInt(role.getString(Common.JSON_CON))); // 初始化最大血量
                                player.setStr(Integer.parseInt(role.getString(Common.JSON_STR)));
                                player.setDex(Integer.parseInt(role.getString(Common.JSON_DEX)));
                                player.setHungry(Integer.parseInt(role.getString(Common.JSON_HUNGRY)));
                                player.setMaxhungry(Integer.parseInt(role.getString(Common.JSON_HUNGRY))); // 初始化最大饥饿值
                                player.setCost_while_sleep(Integer.parseInt(role.getString(Common.JSON_COST_WHILE_SLEEP)));
                                player.setDrug_effect_percent(Float.parseFloat(role.getString(Common.JSON_DRUG_EFFECT_PERCENT)));
                                player.setNpc_favor_percent(Float.parseFloat(role.getString(Common.JSON_NPC_FAVOR_PERCENT)));
                                player.setPhysical_injury_percent(Float.parseFloat(role.getString(Common.JSON_PHYSICAL_INJURY_PERCENT)));
                                player.setThreat_from_a_warrior(Float.parseFloat(role.getString(Common.JSON_THREAT_FROM_A_WARRIOR)));
                                player.setDes(role.getString(Common.JSON_DESC));
                                playerMap.put(socket.getInetAddress().toString(),player);
                                addTurn(Common.ROLETYPE_PLAYER,socket.getInetAddress().toString(),playerMap.keySet().size(),Integer.parseInt(role.getString(Common.JSON_DEX)),Integer.parseInt(role.getString(Common.JSON_STR)),0);
                                if(playerMap.keySet().size() == playerNum) { // 所有玩家都选择完角色，游戏开始
                                    notifyNext();
                                }
                                break;
                            }
                        }
                        if(role!=null) {
                            msg = Common.SERVER_SHOWPLAYER+Common.SPLIT_SEPARATOR+"玩家【"+nickMap.get(socket.getInetAddress().toString())+"】选择了角色："+role.getString("NAME");
                            sendMsg(Common.SERVER_SHOWPLAYER);
                        }

                        break;
                    case Common.CLIENT_MOVE: // 用户移动了位置，进一步进行触发事项
                        triggers = new ArrayList<>();
                        actions = new JSONObject();
                        String des = "";
                        for(int i=0;i<nodes.length();i++) {
                            JSONObject node = nodes.getJSONObject(i);
                            if(node.getString(Common.JSON_ID).equals(arry[1])) {
                                JSONArray triggerArray = node.getJSONArray(Common.JSON_TRIGGER); // 查找指定节点的触发事项
                                for(int j = 0; j < triggerArray.length();j++) {
                                    triggers.add(triggerArray.getString(j));
                                }
                                actions = node.getJSONObject(Common.JSON_ACTION); // 查找指定节点可执行移动
                                des = "["+node.getString(Common.JSON_NAME)+"]"+node.getString(Common.JSON_DESC);
                                Player p = playerMap.get(socket.getInetAddress().toString());
                                String originalPositionId = p.getPosition()==null?null:p.getPosition().getId(); // 初次移动时没有原地址
                                showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+p.getName()+"移动到了"+node.getString(Common.JSON_NAME));
                                Place place = new Place();
                                place.setId(arry[1]);
                                p.setPosition(place);
                                if(null!=originalPositionId)
                                notifyRefreshPersionList(originalPositionId); // 通知原地址其他玩家刷新人物列表（防止当前玩家移动后仍能被原地址玩家指定）
                                break;
                            }
                        }
                        msg = Common.SERVER_MOVE+Common.SPLIT_SEPARATOR+des; // 区分其它描述，触发下一个玩家行动
                        sendMsg(Common.SERVER_MOVE);
                        notifyRefreshPersionList(arry[1]);

                        Message message = myHandler.obtainMessage();
                        message.what = Common.SERVER_REFRESHMAP_FLAG; //消息标识
                        message.obj = des+"\n";
                        myHandler.sendMessage(message);
                        break;
                    case Common.CLIENT_TALK:
                        if(!arry[1].equals("NOACTION")) {
                            JSONObject npc = npcs.getJSONObject(arry[1]);
                            JSONArray triggerArray = npc.getJSONArray(Common.JSON_TRIGGER); // 查找指定节点的触发事项
                            for(int j = 0; j < triggerArray.length();j++) {
                                triggers.add(triggerArray.getString(j));
                            }
                        }else {
                            showDes("你一言不发转身便走！");
                        }
                        doNext();
                        break;
                    case Common.CLIENT_TRIGGER: // 用户触发处理
                        doNext();
                        break;
                    case Common.CLIENT_EVENT:// 用户针对事件进行选择
                        boolean isDoAction = arry[1].contains("_");
                        if(isDoAction) { // 玩家执行了事件动作
                            String[] itemArray = arry[1].split(Common.ITEM_SEPARATOR);
                            JSONObject gotThing = things.getJSONObject(itemArray[0]);
                            showDes("你得到了"+gotThing.getString(Common.JSON_NAME)+"，肯定有什么用吧。");
                            JSONArray thingsArray = dropout(itemArray[0],Integer.parseInt(itemArray[1]), socket.getInetAddress().toString());
                            msg = Common.SERVER_DROPOUT+Common.SPLIT_SEPARATOR+thingsArray.toString();
                            sendMsg(Common.SERVER_DROPOUT);
                        } else {
                            showDes("什么也没发生，你可以离开了！");
                        }
                        doNext();
                        break;
                    case Common.CLIENT_PICK: // 用户选择了选项
                        String[] temp = arry[1].split(Common.CMD_SEPARATOR);
                        if(temp.length > 1) { // 玩家接受了NPC发布的任务
                            switch (temp[0]) {
                                case Common.CMD_TASK: // （对应任务的接受与拒绝），更新任务列表
                                    String taskId = temp[1];
                                    Player currentPlayer = playerMap.get(socket.getInetAddress().toString());
                                    Map<String,Task> taskMap = currentPlayer.getAcceptedTask();
                                    for(String id:taskMap.keySet()) {
                                        Task t = taskMap.get(id);
                                        if(t.getId().equals(taskId)) { // 防止玩家重复接受任务
                                            showDes("你已经接受过这个任务了，请在完成任务后再重新领取任务。");
                                            doNext();
                                            return;
                                        }
                                    }
                                    JSONObject taskObj = tasks.getJSONObject(taskId);
                                    Task task = new Task();
                                    task.setId(taskId);
                                    task.setHint(taskObj.getString(Common.JSON_HINT));
                                    JSONObject requireObj = taskObj.getJSONObject(Common.JSON_REQIURE);
                                    Item require = new Item();
                                    require.setId(requireObj.getString(Common.JSON_ID));
                                    require.setUnit(requireObj.getInt(Common.JSON_UNIT));
                                    task.setRequire(require);
                                    JSONArray rewards = taskObj.getJSONArray(Common.JSON_REWARD);
                                    List<Item> rewardList = new ArrayList<>();
                                    for(int i=0;i<rewards.length();i++) {
                                        JSONObject rewardObj = rewards.getJSONObject(i);
                                        Item reward = new Item();
                                        reward.setId(rewardObj.getString(Common.JSON_ID));
                                        reward.setName(things.getJSONObject(rewardObj.getString(Common.JSON_ID)).getString(Common.JSON_NAME));
                                        reward.setUnit(rewardObj.getInt(Common.JSON_UNIT));
                                        reward.setType(rewardObj.getString(Common.JSON_TYPE));
                                        rewardList.add(reward);
                                    }
                                    task.setReward(rewardList);
                                    Place place = new Place();
                                    place.setId(taskObj.getString(Common.JSON_PLACE));
                                    task.setPlace(place);
                                    task.setNpc(taskObj.getString(Common.JSON_NPC));
                                    currentPlayer = playerMap.get(socket.getInetAddress().toString());
                                    taskMap = currentPlayer.getAcceptedTask();
                                    taskMap.put(taskId,task);
                                    JSONArray tasks = new JSONArray();
                                    for(String id:taskMap.keySet()) {
                                        Task t = taskMap.get(id);
                                        tasks.put(t.toJSONObject());
                                    }
                                    showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+currentPlayer.getName()+"接受了任务"+taskObj.getString(Common.JSON_HINT));
                                    msg = Common.SERVER_TASK+Common.SPLIT_SEPARATOR+tasks.toString();
                                    sendMsg(Common.SERVER_TASK);
                                    doNext();
                                    break;
                                case Common.CMD_TALK: // 对应新的谈话分支，开启一个新的选择
                                    talkControl(temp[1]);
                                    break;
                                case Common.CMD_NPC: // 对应新的NPC分支，开启一个新的选择
                                    npcControl(temp[1]);
                                    break;
                                case Common.CMD_EVENT: // 对应新的事件分支，开启一个新的选择
                                    eventControl(temp[1]);
                                    break;
                                case Common.CMD_FIGHT: // 对应新的战斗分支，开启一个新的选择
                                    fightControl(temp[1]);
                                    break;
                                case Common.CMD_COST: // 对应回合数消耗
                                    costControl(temp[1]);
                                    break;
                            }
                        } else { // 用户选择了没有任何触发的选项
                            showDes("你可以离开了！");
                            doNext();
                        }
                        break;
                    case Common.CLIENT_WAIT:
                        Player currentPlayer = playerMap.get(socket.getInetAddress().toString());
                        showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+currentPlayer.getName()+"选择了原地等待");
                        notifyNext();
                        break;
                    case Common.CLIENT_PERSIONDETAIL: // 用户要求查询人物详情
                        String persionId = arry[1];
                        JSONObject persion;
                        if(Common.isIp(persionId.substring(1))) { // 判断查询的是玩家详情
                            persion = playerMap.get(persionId)==null?null:playerMap.get(persionId).toJSONObject();
                        } else { // 判断查询的是NPC详情
                            persion = npcs.getJSONObject(persionId);

                            /*
                             * 在NPC描述中拼接攻击特性
                             */
                            String desc = persion.getString(Common.JSON_DESC);
                            if(!desc.contains("特性")) {
                                JSONArray jArray = persion.getJSONArray(Common.JSON_FEATURE);
                                for(int i=0;i<jArray.length();i++) {
                                    String featureName = jArray.getString(i);
                                    Feature feature = Feature.valueOf(featureName);
                                    persion.put(Common.JSON_DESC,desc+"\n"+"特性："+feature.getValue()+"("+feature.getDesc()+")");
                                }
                            }
                        }
                        if(null != persion) {
                            msg = Common.SERVER_PERSIONDETAIL+Common.SPLIT_SEPARATOR+persion.toString();
                            sendMsg(Common.SERVER_PERSIONDETAIL);
                        }
                        break;
                    case Common.CLIENT_MONSTERDETAIL: // 用户要求查询怪物详情，返回怪物详情（不包含掉落物品详情）
                        String monsterId = arry[1].split(Common.ITEM_SEPARATOR)[0];
                        int xh = Integer.parseInt(arry[1].split(Common.ITEM_SEPARATOR)[1]);
                        String fightId = monsterId.substring(0,monsterId.lastIndexOf("-"));
                        JSONArray monsterArray = onFightMap.get(fightId);
                        JSONObject monster;
//                        for(int i=0;i<monsterArray.length();i++) {
                        if(monsterArray.getJSONObject(xh).getString(Common.JSON_ID).equals(monsterId)) {
                            monster = monsterArray.getJSONObject(xh);

                            /*
                             * 在怪物描述中拼接攻击特性
                             */
                            String desc = monster.getString(Common.JSON_DESC);
                            if(!desc.contains("特性")) {
                                JSONArray jArray = monster.getJSONArray(Common.JSON_FEATURE);
                                for(int i=0;i<jArray.length();i++) {
                                    String featureName = jArray.getString(i);
                                    Feature feature = Feature.valueOf(featureName);
                                    monster.put(Common.JSON_DESC,desc+"\n"+"特性："+feature.getValue()+"("+feature.getDesc()+")");
                                }
                            }
                            msg = Common.SERVER_MONSTERDETAIL+Common.SPLIT_SEPARATOR+monster.toString();
                            sendMsg(Common.SERVER_MONSTERDETAIL);
//                                break;
                        }
//                        }

                        break;
                    case Common.CLIENT_ESCAPE: // 用户在战斗中企图逃跑
                        fightId = arry[1];
                        monsterArray = onFightMap.get(fightId);
                        currentPlayer = playerMap.get(socket.getInetAddress().toString());
                        int dex = currentPlayer.getDex(); // 获取当前玩家敏捷
                        double escape_percent = 0.3; // 默认
                        for(int i=0;i<monsterArray.length();i++) {
                            monsterId = monsterArray.getJSONObject(i).getString(Common.JSON_ID);
                            JSONObject m = monsters.getJSONObject(monsterId.substring(monsterId.lastIndexOf("-")+1));
                            if(m.getInt(Common.JSON_DEX) > dex) {
                                escape_percent = 0.5; // 当玩家敏捷大于任何一个遭遇到的怪物的敏捷值时，逃脱战斗的概率提高到50%
                                break;
                            }
                        }
                        Player p = playerMap.get(socket.getInetAddress().toString());
                        if(escape_percent > Math.random()) {
                            String positionId = p.getPosition().getId();
                            msg = Common.SERVER_ESCAPERESULT+Common.SPLIT_SEPARATOR+positionId;
                            sendMsg(Common.SERVER_ESCAPERESULT);
                            p.setThreat_from_a_warrior(0); // 战斗结束清空玩家身上的仇恨值
                            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"玩家"+p.getName()+"成功逃离了战场！");
                            removeFromFight(socket.getInetAddress().toString());
                        } else {
                            // 逃脱战斗失败
                            notifyNext(); // 逃脱失败放弃一次行动机会
                            msg = Common.SERVER_ESCAPERESULT+Common.SPLIT_SEPARATOR+false;
                            sendMsg(Common.SERVER_ESCAPERESULT);
                            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"玩家"+p.getName()+"尝试逃离战场失败！");
                        }

                        break;
                    case Common.CLIENT_FIGHT_PERSION: // 对NPC或玩家的战斗不影响行动序列（NPC为瞬间结算互对一招，玩家按照既有次序即可）
                        String fightTargetId = arry[1];
                        JSONObject npc;
                        currentPlayer = playerMap.get(socket.getInetAddress().toString());
                        int hurt; // 当前玩家对其他人造成的伤害
                        int hurt2; // NPC对玩家造成的伤害

                        JSONArray fightPersionReulst = new JSONArray();
                        if(Common.isIp(fightTargetId.substring(1))) { // 被攻击的目标是玩家
                            Player player = playerMap.get(fightTargetId);
                            hurt = calculateHurt(currentPlayer,player);
                            for(String thingId : player.getOwnThings().keySet()) { // 暴怒诅咒，使你受到伤害增加20%
                                if(thingId.equals("WRATH")) {
                                    hurt = (int) Math.floor(hurt*(1+Common.CURSE_RATIO));
                                    break;
                                }
                            }
                            player.setCon(player.getCon()-hurt > 0 ? player.getCon()-hurt : 0);
                            JSONObject fightTarget = new JSONObject();
                            fightTarget.put(Common.FIGHT_SRC,currentPlayer.toJSONObject());
                            fightTarget.put(Common.FIGHT_TARGET,player.toJSONObject());
                            fightTarget.put(Common.FIGHT_HURT,hurt);
                            fightPersionReulst.put(fightTarget);
                            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+currentPlayer.getName()+"对"+player.getName()+"造成了"+hurt+"点伤害"+(player.getCon()==0?"造成其死亡":""));
                            msg = Common.SERVER_FIGHT_PERSION+Common.SPLIT_SEPARATOR+hurt+Common.CMD_SEPARATOR+player.getName();
                            sendMsg(Common.SERVER_FIGHT_PERSION);
                            if(player.getCon()==0) { // 被攻击玩家死亡，将其移除出行动队列，杀死他的玩家获得其所有物品
                                removeTurnObj(player.getIp());
                                Map<String,Thing> dropThings = player.getOwnThings();
                                JSONArray dropout = new JSONArray();
                                for(String id : dropThings.keySet()) {
                                    Thing thing = dropThings.get(id);
                                    if(thing.getType().equals(Common.ITEM_TYPE_EQUIPMENT_ON)) { // 将处于装备状态的装备置为卸下状态
                                        thing.setType(Common.ITEM_TYPE_EQUIPMENT_OFF);
                                    }
                                    dropout.put(thing.toJSONObject());
                                }
                                dropout.put(things.getJSONObject("ENVY")); // 杀死玩家固定获得“嫉妒”诅咒
                                dropout.put(things.getJSONObject("THE_SOUL_OF_A_FRIEND")); // 杀死玩家固定获得朋友的灵魂
                                dropoutSome(dropout,currentPlayer.getOwnThings());
                                refreshPlayerItemList(currentPlayer);
                                playerNum--; // 更新玩家数
                            }
                        } else { // 被攻击的目标是NPC
                            npc = npcs.getJSONObject(fightTargetId);
                            hurt = calculateHurt(currentPlayer,npc);

                            CompareEntity e = new CompareEntity();
                            e.setId(npc.getString(Common.JSON_ID));
                            e.setDex(npc.getInt(Common.JSON_DEX));
                            e.setStr(npc.getInt(Common.JSON_STR));
                            hurt2 = calculateHurt(e,currentPlayer,npc.getJSONArray(Common.JSON_FEATURE));

                            int playerSockBlood = 0;
                            int npcSockBlood = 0;
                            if(gotFeature(currentPlayer.getOwnThings(),Feature.HEMOPHAGIA)) { // 玩家具备【吸血】特性
                                playerSockBlood = (int) Math.floor(hurt*Feature.ratio);
                            }
                            if(gotFeature(npc.getJSONArray(Common.JSON_FEATURE),Feature.HEMOPHAGIA)) { // NPC具备【吸血】特性
                                npcSockBlood = (int) Math.floor(hurt2*Feature.ratio);
                            }
                            npc.put(Common.JSON_CON,npc.getInt(Common.JSON_CON)+npcSockBlood-hurt > 0 ? (npc.getInt(Common.JSON_CON)+npcSockBlood-hurt > npc.getInt(Common.JSON_MAXCON) ? npc.getInt(Common.JSON_MAXCON):npc.getInt(Common.JSON_CON)+npcSockBlood-hurt) : 0);
                            currentPlayer.setCon(currentPlayer.getCon()+playerSockBlood - hurt2 > 0 ? (currentPlayer.getCon()+playerSockBlood-hurt2 > currentPlayer.getMaxcon() ? currentPlayer.getMaxcon() : currentPlayer.getCon()+playerSockBlood-hurt2) : 0);

                            JSONObject fightTarget = new JSONObject();
                            fightTarget.put(Common.FIGHT_SRC,currentPlayer.toJSONObject());
                            fightTarget.put(Common.FIGHT_TARGET,npc);
                            fightTarget.put(Common.FIGHT_HURT,hurt);
                            fightPersionReulst.put(fightTarget);
                            JSONObject fightTarget2 = new JSONObject();
                            fightTarget2.put(Common.FIGHT_SRC,npc);
                            fightTarget2.put(Common.FIGHT_TARGET,currentPlayer.toJSONObject());
                            fightTarget2.put(Common.FIGHT_HURT,hurt2);
                            fightPersionReulst.put(fightTarget2);
                            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+currentPlayer.getName()+"对"+npc.getString(Common.JSON_NAME)+"造成了"+hurt+"点伤害"+(npc.getInt(Common.JSON_CON)==0?"造成其死亡":""));
                            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"同时"+npc.getString(Common.JSON_NAME)+"对"+currentPlayer.getName()+"造成了"+hurt2+"点伤害"+(currentPlayer.getCon()==0?"造成其死亡":""));
                            msg = Common.SERVER_FIGHT_PERSION+Common.SPLIT_SEPARATOR+hurt+Common.CMD_SEPARATOR+npc.getString(Common.JSON_NAME)+Common.CMD_SEPARATOR+hurt2;
                            sendMsg(Common.SERVER_FIGHT_PERSION);
                            if(npc.getInt(Common.JSON_CON)==0) { // 玩家杀死了NPC，基于概率获取其拥有的物品
                                JSONArray ownThings = npc.getJSONArray(Common.JSON_OWNTHINGS);
                                JSONArray dropThings = new JSONArray();
                                for(int i=0;i<ownThings.length();i++) {
                                    JSONObject thing = ownThings.getJSONObject(i);
                                    boolean isDropout = Common.randomDropout(thing.getInt(Common.JSON_PERCENT));
                                    if(isDropout) {
                                        Thing dropThing;
                                        JSONObject thingObj = things.getJSONObject(thing.getString(Common.JSON_ID));
                                        thingObj.put(Common.JSON_ID,thing.getString(Common.JSON_ID));
                                        if(thingObj.getString(Common.JSON_TYPE).equals(Common.ITEM_TYPE_EQUIPMENT_ON)
                                                || thingObj.getString(Common.JSON_TYPE).equals(Common.ITEM_TYPE_EQUIPMENT_OFF)) {
                                            dropThing = new Equipment(thingObj);
                                            things.put(dropThing.getId(),dropThing.toJSONObject());
                                        } else {
                                            dropThing = new Item(thingObj);
                                        }
                                        //thingNames.append("【"+dropThing.getName()+"】");
                                        dropThing.setUnit(thing.getInt(Common.JSON_UNIT));
                                        dropThings.put(dropThing.toJSONObject());
                                    }
                                }
                                dropoutSome(dropThings,currentPlayer.getOwnThings());
                                if(0!=dropThings.length()) { // 有物品掉落
                                    //currentPlayer.getOwnThings().putAll(dropThings);;
                                    refreshPlayerItemList(currentPlayer);
                                }
                            }
                            if(currentPlayer.getCon()==0) { // 玩家被NPC杀死，则将其移除出行动队列
                                removeTurnObj(currentPlayer.getIp());
                            }
                        }
                        Place current = currentPlayer.getPosition();
                        msg = Common.SERVER_REFRESH_FIGHT_PERSION+Common.SPLIT_SEPARATOR+fightPersionReulst.toString();
                        List<String> notifyList = getCurrentPositionPlayerIds(current.getId());
                        for(String ip : notifyList) { // 通知所有这个地址的玩家刷新人物列表
                            sendMsg(ip);
                        }
                        notifyRefreshPersionList(current.getId());
                        notifyNext();
                        break;
                    case Common.CLIENT_FIGHT:

                        // 更新战斗信息
                        monsterId = arry[1].split(Common.ITEM_SEPARATOR)[0];
                        xh = Integer.parseInt(arry[1].split(Common.ITEM_SEPARATOR)[1]);
                        String id = arry[1].substring(0,monsterId.lastIndexOf("-")); // 战斗ID
                        JSONArray mArray = onFightMap.get(id);
                        //hurt = 0;
//                        for(int i=0;i<mArray.length();i++) {
                            JSONObject m = mArray.getJSONObject(xh);
                            if(m.getString(Common.JSON_ID).equals(monsterId)) {
                                Player player = playerMap.get(socket.getInetAddress().toString());
                                hurt = calculateHurt(player,m);
                                if(hurt == 0) {
                                    showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"玩家"+player.getName()+"的攻击被怪物"+m.getString(Common.JSON_NAME)+"闪避开了！");
                                } else {
                                    m.put(Common.JSON_CON,m.getInt(Common.JSON_CON) - hurt > 0 ? m.getInt(Common.JSON_CON) - hurt : 0);
                                    showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"玩家"+player.getName()+"对"+m.getString(Common.JSON_NAME)+"造成了"+hurt+"点伤害！");
                                    player.setThreat_from_a_warrior(player.getThreat_from_a_warrior()+hurt); // 通过伤害累加仇恨值（仇恨值越高越优先被攻击）
                                }
                                msg = Common.SERVER_REFRESH_FIGHT+Common.SPLIT_SEPARATOR+m.getString(Common.JSON_NAME)+Common.CMD_SEPARATOR+hurt;
                                sendMsg(Common.SERVER_REFRESH_FIGHT);
                                if(m.getInt(Common.JSON_CON) == 0) {
                                    removeTurnObj(m.getString(Common.JSON_ID)); // 死去的怪物要移除出行动队列
                                    JSONObject monsterObj = monsters.getJSONObject(m.getString(Common.JSON_ID).substring(m.getString(Common.JSON_ID).lastIndexOf("-")+1));
                                    JSONArray randomThings = Common.random(monsterObj.getJSONArray(Common.JSON_DROPOUT),Common.JSON_THING); // 基于概率返回随机掉落物品
                                    Map<String,Thing> thingMap = player.getOwnThings();
                                    dropoutSome(randomThings,thingMap);
                                    player.setOwnThings(thingMap);
                                    refreshPlayerItemList(player);
                                }
                                int deadMonsterNum = 0;
                                for(int i=0;i<mArray.length();i++) {
                                    JSONObject monsterObj = mArray.getJSONObject(i);
                                    if(monsterObj.getInt(Common.JSON_CON) == 0) {
                                        deadMonsterNum++;
                                    }
                                }
                                List<String> ipList = new ArrayList<>();
                                if(deadMonsterNum==mArray.length()) {
                                    msg = Common.SERVER_ESCAPERESULT+Common.SPLIT_SEPARATOR+id;
                                    sendMsg(Common.SERVER_ESCAPERESULT);
                                    player.setThreat_from_a_warrior(0); // 战斗结束清空玩家身上的仇恨值
                                    showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"玩家"+player.getName()+"完成了战斗！");
                                    Fight f = fightMap.remove(socket.getInetAddress().toString()); // 将玩家从战斗中移除

                                    for(String ip:fightMap.keySet()) {
                                        if(fightMap.get(ip).getId().equals(f.getId())) { // 判断是否有其他玩家在这场战斗中
                                            ipList.add(ip);
                                        }
                                    }
                                    for(String ip :ipList) {
                                        fightMap.remove(ip);
                                        sendMsg(ip); // 通知其他玩家脱离战斗
                                        playerMap.get(ip).setThreat_from_a_warrior(0); // 战斗结束清空玩家身上的仇恨值
                                    }
                                    onFightMap.remove(f.getId());
                                    removeFightMonster(f.getId());
                                }
                                notifyRefreshFightList(id);
                                notifyNext();
                            }
//                        }
                        break;
                    case Common.CLIENT_GETMAP: // 玩家获取最新地图
                        msg = Common.SERVER_REFRESH_MAP+Common.SPLIT_SEPARATOR+nodes;
                        sendMsg(Common.SERVER_REFRESH_MAP);
                        break;
                    case Common.CLIENT_USE_ITEM: // 玩家使用物品
                        String target = arry[1].split(Common.CMD_SEPARATOR)[0].substring(0,arry[1].split(Common.CMD_SEPARATOR)[0].length()-1); // 使用对象ID
                        String itemId = arry[1].split(Common.CMD_SEPARATOR)[1]; // 使用的物品ID
                        JSONObject item = things.getJSONObject(itemId);
                        String type = item.getString(Common.JSON_TYPE);
                        currentPlayer = playerMap.get(socket.getInetAddress().toString());
                        if(mod.equals(Common.MOD_SURVIVAL)) {
                            if(!itemId.equals("MONEY") && target.equals("PROFITEER")) { // 大逃杀模式下，对奸商使用物品即为贩卖(除了金钱)
                                Map<String,Thing> itemMap = currentPlayer.getOwnThings();
                                Thing requireItem = itemMap.get(itemId); // 玩家拥有的任务物品
                                int unit = requireItem.getUnit();
                                int price = requireItem.getPrice();
                                itemMap.remove(itemId);
                                dropout("MONEY",unit*price,currentPlayer.getIp());
                                refreshPlayerItemList(currentPlayer);
                                showDes("你卖了"+unit+"个"+requireItem.getName()+"获得了"+unit*price+"文钱");
                                break;
                            }
                        }
                        switch (type) {
                            case Common.ITEM_TYPE_TASK: // 使用任务物品提交任务
                                submitTask(currentPlayer,itemId,target);
                                break;
                            case Common.ITEM_TYPE_MEDICINE: // 玩家使用药物
                                float effectPercent = currentPlayer.getDrug_effect_percent(); // 使用药品玩家发挥药效的百分比
                                int effect = (int) Math.floor(item.getInt(Common.JSON_EFFECT)*effectPercent); // 物品恢复的体质值(药效*系数向下取整)
                                for(String thingId:currentPlayer.getOwnThings().keySet()) { // 淫欲诅咒减少20%药品效果
                                    if(thingId.equals("LUST")) {
                                        effect = (int) Math.floor(effect*(1-Common.CURSE_RATIO));
                                        break;
                                    }
                                }
                                String position;
                                String targetName;
                                if(Common.isIp(target.substring(1))) { // 目标是玩家
                                    Player targetPlayer = playerMap.get(target);

                                    if(!currentPlayer.getIp().equals(targetPlayer.getIp())) { // 嫉妒诅咒减少20%其他玩家对你使用物品的效果
                                        for(String thingId:targetPlayer.getOwnThings().keySet()) {
                                            if (thingId.equals("ENVY")) {
                                                effect = (int) Math.floor(effect * (1-Common.CURSE_RATIO));
                                                break;
                                            }
                                        }
                                    }

                                    targetPlayer.setCon(targetPlayer.getCon()+effect>targetPlayer.getMaxcon()?targetPlayer.getMaxcon():targetPlayer.getCon()+effect); // 增加目标玩家体质值
                                    msg=Common.SERVER_REFRESHPLAYER+Common.SPLIT_SEPARATOR+targetPlayer.toJSONObject(); // 刷新目标玩家的人物状态
                                    sendMsg(target);
                                    position = targetPlayer.getPosition().getId();
                                    targetName = targetPlayer.getName();
                                } else { // 目标是NPC
                                    npc = npcs.getJSONObject(target);
                                    npc.put(Common.JSON_CON,npc.getInt(Common.JSON_CON)+effect>npc.getInt(Common.JSON_MAXCON) ? npc.getInt(Common.JSON_MAXCON) : npc.getInt(Common.JSON_CON)+effect); // 增加目标NPC体质值
                                    position = npc.getString(Common.JSON_POSITION);
                                    targetName = npc.getString(Common.JSON_NAME);
                                }
                                showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+currentPlayer.getName()+"对"+targetName+"使用了药品【"+item.getString(Common.JSON_NAME)+"】使其恢复了"+effect+"点体质值");
                                useItem(currentPlayer,itemId);
                                notifyRefreshPersionList(position); // 由于使用恢复药物，目标体质发生改变通知当前地区的玩家更新人物列表
                                break;
                            case Common.ITEM_TYPE_FOOD: // 玩家使用食物
                                effect = item.getInt(Common.JSON_EFFECT); // 物品恢复的饥饿值
                                for(String thingId:currentPlayer.getOwnThings().keySet()) { // 暴食诅咒减少20%食物效果
                                    if (thingId.equals("GLUTTONY")) {
                                        effect = (int) Math.floor(effect * (1-Common.CURSE_RATIO));
                                        break;
                                    }
                                }

                                if(Common.isIp(target.substring(1))) { // 目标是玩家
                                    Player targetPlayer = playerMap.get(target);

                                    if(!currentPlayer.getIp().equals(targetPlayer.getIp())) { // 嫉妒诅咒减少20%其他玩家对你使用物品的效果
                                        for(String thingId:targetPlayer.getOwnThings().keySet()) {
                                            if (thingId.equals("ENVY")) {
                                                effect = (int) Math.floor(effect * (1-Common.CURSE_RATIO));
                                                break;
                                            }
                                        }
                                    }

                                    targetPlayer.setHungry(targetPlayer.getHungry()+effect>targetPlayer.getMaxhungry()?targetPlayer.getMaxhungry():targetPlayer.getHungry()+effect); // 增加目标玩家饥饿值
                                    msg=Common.SERVER_REFRESHPLAYER+Common.SPLIT_SEPARATOR+targetPlayer.toJSONObject(); // 刷新目标玩家的人物状态
                                    sendMsg(target);
                                    position = targetPlayer.getPosition().getId();
                                    useItem(currentPlayer,itemId);
                                    showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+currentPlayer.getName()+"对"+targetPlayer.getName()+"使用了食物【"+item.getString(Common.JSON_NAME)+"】使其恢复了"+effect+"点饥饿值");
                                    notifyRefreshPersionList(position); // 由于使用食物，目标饥饿值发生改变通知当前地区的玩家更新人物列表
                                } else { // 目标是NPC或怪物不予理会
                                    showDes("对方表示不想吃你的东西~");
                                }
                                break;
                            case Common.ITEM_TYPE_EQUIPMENT_ON: // 玩家装备装备
                            case Common.ITEM_TYPE_EQUIPMENT_OFF: // 玩家卸下装备
                                useEquipment(currentPlayer,itemId);
                                position = currentPlayer.getPosition().getId();
                                notifyRefreshPersionList(position);
                                msg=Common.SERVER_REFRESHPLAYER+Common.SPLIT_SEPARATOR+currentPlayer.toJSONObject(); // 刷新玩家的人物状态
                                sendMsg(currentPlayer.getIp());
                                break;
                            case Common.ITEM_TYPE_REDEMPTION: // 玩家使用救赎物品抵消诅咒
                                defeatCurse(currentPlayer,itemId);
                                break;
                            case Common.ITEM_TYPE_EFFECT:
                                JSONObject rewardObj = things.getJSONObject(itemId);
                                JSONObject effectObj = rewardObj.getJSONObject(Common.JSON_EFFECT);
                                switch(effectObj.getString(Common.JSON_TEXT)) {
                                    case Common.JSON_CON: // 获得奖励为体质恢复效果
                                        currentPlayer.setCon(currentPlayer.getCon()+effectObj.getInt(Common.JSON_VALUE) > currentPlayer.getMaxcon() ? currentPlayer.getMaxcon() : currentPlayer.getCon()+effectObj.getInt(Common.JSON_VALUE)); // 恢复玩家体质
                                        break;
                                    case Common.JSON_HUNGRY: // 获得奖励为饥饿值恢复效果
                                        currentPlayer.setHungry(currentPlayer.getHungry()+effectObj.getInt(Common.JSON_VALUE) > currentPlayer.getMaxhungry() ? currentPlayer.getMaxhungry() : currentPlayer.getHungry()+effectObj.getInt(Common.JSON_VALUE)); // 恢复玩家饥饿值
                                        break;
                                    case Common.JSON_STR: // 获得奖励为力量值提升效果
                                        currentPlayer.setStr(currentPlayer.getStr()+effectObj.getInt(Common.JSON_VALUE)); // 提升玩家力量
                                        break;
                                    case Common.JSON_DEX: // 获得奖励为敏捷值提升效果

                                        currentPlayer.setDex(currentPlayer.getDex()+effectObj.getInt(Common.JSON_VALUE)); // 提升玩家敏捷
                                        break;
                                }
                                useItem(currentPlayer,itemId);
                                notifyRefreshPersionList(currentPlayer.getPosition().getId());
                                break;
                            case Common.ITEM_TYPE_NOTE:
                                JSONArray noteArray = notes.getJSONArray(itemId);
                                showDes(Common.randomString(noteArray,Common.JSON_NOTE));
                                currentPlayer.getOwnThings().remove(itemId); // 看完从玩家物品栏中移除
                                refreshPlayerItemList(currentPlayer);
                                break;
                            case Common.ITEM_TYPE_THE_END: // 玩家使用结局物品
                                switch(itemId) {
                                    case "EVIL_GOD'S_VICTORY": // 玩家自己作为恶魔（以杀死其他玩家为前提）获得了胜利
                                        showDes("你作为恶魔获得了胜利！恭喜你！");
                                        break;
                                    case "COMPLETE_VICTORY": // 所有人均获得胜利
                                        showAllDes("恭喜所有人都获得了胜利！");
                                        break;
                                }
                                break;
                            case Common.ITEM_TYPE_RECRUIT: // 玩家使用补给品
                                JSONObject recruit = Common.randomJSONArray(recruits);
                                dropout(recruit.getString(Common.JSON_ID),recruit.getInt(Common.JSON_UNIT), socket.getInetAddress().toString());
                                useItem(currentPlayer,itemId);
                                break;
                            case Common.ITEM_TYPE_GIFT: // 玩家打开礼包
                                JSONArray gifts = new JSONArray();
                                Iterator<String> it = things.keys();
                                while(it.hasNext()){
                                    String key = it.next();
                                    JSONObject thing = things.getJSONObject(key);
                                    String itemType = thing.getString(Common.JSON_TYPE);
                                    if(itemType.equals(Common.ITEM_TYPE_EQUIPMENT_OFF) ||
                                            itemType.equals(Common.ITEM_TYPE_MEDICINE) ||
                                            itemType.equals(Common.ITEM_TYPE_FOOD)) {
                                        thing.put(Common.JSON_PERCENT,1);
                                        thing.put(Common.JSON_ID,key);
                                        gifts.put(thing);
                                    }
                                }

                                JSONObject gift = Common.randomJSONArray(gifts);
                                dropout(gift.getString(Common.JSON_ID),gift.getInt(Common.JSON_UNIT), socket.getInetAddress().toString());
                                useItem(currentPlayer,itemId);
                                showDes("你打开了礼包，获得了"+gift.getString(Common.JSON_NAME));
                                break;
                        }
//                        doNext();
                        break;
                    case Common.CLIENT_ITEMDETAIL: // 玩家查询物品详情
                        item = new JSONObject();
                        currentPlayer = playerMap.get(socket.getInetAddress().toString());
                        Thing detail = currentPlayer.getOwnThings().get(arry[1]);
                        item.put(Common.JSON_ID,arry[1]);
                        item.put(Common.JSON_NAME,detail.getName());
                        item.put(Common.JSON_UNIT,detail.getUnit());
                        item.put(Common.JSON_DESC,detail.getDesc());
                        item.put(Common.JSON_TYPE,detail.getType());
                        item.put(Common.JSON_PRICE,detail.getPrice());
                        msg = Common.SERVER_ITEMDETAIL+Common.SPLIT_SEPARATOR+item.toString();
                        sendMsg(Common.SERVER_ITEMDETAIL);
                        break;
                }
            }
        }

        private void removeFromFight(String id) {
            Fight f = fightMap.remove(id); // 将玩家从战斗中移除
            boolean someOneInThisFight = false;
            for(String ip:fightMap.keySet()) {
                if(fightMap.get(ip).getId().equals(f.getId())) { // 判断是否有其他玩家在这场战斗中
                    someOneInThisFight = true;
                }
            }
            if(!someOneInThisFight) { // 如果没有其他玩家在这场战斗中，则从正在战斗的映射中删除战斗信息
                onFightMap.remove(f.getId());
                removeFightMonster(f.getId());
            }
        }
        private void defeatCurse(Player player,String itemId) throws JSONException {
            Map<String,Thing> itemMap = player.getOwnThings();
            Thing redemptionItem = itemMap.get(itemId); // 玩家拥有的救赎物品
            String curseId = things.getJSONObject(itemId).getString(Common.JSON_DEFEAT); // 获取救赎物品可以解除的诅咒ID
            Thing curseItem = itemMap.get(curseId); // 玩家拥有的诅咒物品
            if(curseItem == null) {
                return;
            }
            if(redemptionItem.getUnit()==curseItem.getUnit()) { // 玩家能使用的救赎物品数正好等于诅咒物品数，两种物品全部移除出物品列表
                itemMap.remove(itemId);
                itemMap.remove(curseId);
            } else if(redemptionItem.getUnit()>curseItem.getUnit()){ // 玩家能使用的救赎物品数大于诅咒物品数，将诅咒物品移除出物品列表，救赎物品减去相应消耗
                itemMap.remove(curseId);
                redemptionItem.setUnit(redemptionItem.getUnit()-curseItem.getUnit());
            } else { // 玩家能使用的诅咒物品数大于救赎物品数，将救赎物品移除出物品列表，诅咒物品减去相应消耗
                itemMap.remove(itemId);
                curseItem.setUnit(curseItem.getUnit()-redemptionItem.getUnit());
            }
            refreshPlayerItemList(player);
        }

        private void refreshPlayerItemList(Player player) throws JSONException {
            JSONArray thingsArray = new JSONArray();
            Map<String,Thing> playerOwnThings = player.getOwnThings();
            for( String thingId : playerOwnThings.keySet()) {
                JSONObject thing = new JSONObject();
                Thing item = playerOwnThings.get(thingId);
                thing.put(Common.JSON_ID,thingId);
                thing.put(Common.JSON_NAME,item.getName());
                thing.put(Common.JSON_UNIT,item.getUnit());
                thing.put(Common.JSON_DESC,item.getDesc());
                thing.put(Common.JSON_TYPE,item.getType());
                thingsArray.put(thing);
            }
            msg = Common.SERVER_DROPOUT+Common.SPLIT_SEPARATOR+thingsArray.toString();
            sendMsg(Common.SERVER_DROPOUT);
        }

        /*
         * 掉落多种物品
         */
        private void dropoutSome(JSONArray dropoutThings,Map<String,Thing> ownThings) throws JSONException {
            for(int j=0;j<dropoutThings.length();j++) {
                JSONObject thing = dropoutThings.getJSONObject(j);
                String thingId = thing.getString(Common.JSON_ID);
                gotThing(ownThings,thingId,thing.getInt(Common.JSON_UNIT));
            }
        }
        private void submitTask(Player player,String itemId,String target) throws Exception {
            Boolean isUseful = false;
            Place potion  = player.getPosition();
            Map<String, Task> taskMap = player.getAcceptedTask();
            String fnishedTaskId = null;
            for(String taskId : taskMap.keySet()) {
                Task task = taskMap.get(taskId);
                if(task.getPlace().getId().equals(potion.getId())
                        && itemId.equals(task.getRequire().getId())
                        && target.equals(task.getNpc())) { // 在任务地点对任务NPC提交相应任务物品(同类物品)
                    fnishedTaskId = taskId;
                    isUseful = true;
                    Map<String,Thing> itemMap = player.getOwnThings();
                    Thing requireItem = itemMap.get(itemId); // 玩家拥有的任务物品
                    if(requireItem.getUnit()==task.getRequire().getUnit()) { // 玩家任务物品拥有量和任务需求相等，全部用光删除物品
                        itemMap.remove(itemId);
                    } else if(requireItem.getUnit()>task.getRequire().getUnit()){ // 玩家拥有的任务物品数量超出任务需求数量，则在物品数量上减去消耗
                        requireItem.setUnit(requireItem.getUnit()-task.getRequire().getUnit());
                    } else {
                        isUseful = false; // 玩家拥有的任务物品数不够提交任务
                    }
                    //itemList.add(task.getReward());
                    StringBuffer rewardNames = new StringBuffer();
                    for(Item reward : task.getReward()) { // 结算任务奖励
                        gotItem(reward,rewardNames,socket.getInetAddress().toString());
                    }
                    if(isUseful)
                        showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+player.getName()+"向"+npcs.getJSONObject(target).getString(Common.JSON_NAME)+"提交了任务物品["+requireItem.getName()+"]得到了奖励物品"+rewardNames);
                    break;
                }
            }
            if(isUseful) {
                Task finishedTask = taskMap.get(fnishedTaskId);
                taskMap.remove(fnishedTaskId); // 将完成的任务移除
                JSONArray tasks = new JSONArray();
                for(String taskId:taskMap.keySet()) {
                    Task t = taskMap.get(taskId);
                    tasks.put(t.toJSONObject());
                }
                showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+player.getName()+"完成了任务["+finishedTask.getHint()+"]");
                msg = Common.SERVER_TASK+Common.SPLIT_SEPARATOR+tasks.toString();
                sendMsg(Common.SERVER_TASK);
                refreshPlayerItemList(player);
            } else {
                showDes("任务物品提交失败！");
            }
        }
        private void gotItem(Thing reward,StringBuffer rewardNames,String ip) throws Exception {
            Player player = playerMap.get(ip);
            Map<String,Thing> itemMap = player.getOwnThings();
            if(reward.getType().equals(Common.ITEM_TYPE_EFFECT)) { // 物品类型为“效果”类物品，不加入玩家物品列表，根据效果立即生效
                JSONObject rewardObj = things.getJSONObject(reward.getId());
                JSONObject effectObj = rewardObj.getJSONObject(Common.JSON_EFFECT);
                switch(effectObj.getString(Common.JSON_TEXT)) {
                    case Common.JSON_CON: // 获得奖励为体质恢复效果
                        player.setCon(player.getCon()+effectObj.getInt(Common.JSON_VALUE) > player.getMaxcon() ? player.getMaxcon() : player.getCon()+effectObj.getInt(Common.JSON_VALUE)); // 恢复玩家体质
                        break;
                    case Common.JSON_HUNGRY: // 获得奖励为饥饿值恢复效果
                        player.setHungry(player.getHungry()+effectObj.getInt(Common.JSON_VALUE) > player.getMaxhungry() ? player.getMaxhungry() : player.getHungry()+effectObj.getInt(Common.JSON_VALUE)); // 恢复玩家饥饿值
                        break;
                    case Common.JSON_STR: // 获得奖励为力量值提升效果
                        player.setStr(player.getStr()+effectObj.getInt(Common.JSON_VALUE)); // 提升玩家力量
                        break;
                    case Common.JSON_DEX: // 获得奖励为敏捷值提升效果

                        player.setDex(player.getDex()+effectObj.getInt(Common.JSON_VALUE)); // 提升玩家敏捷
                        break;
                }
                notifyRefreshPersionList(player.getPosition().getId());
                rewardNames.append("[").append(reward.getName()).append("]");
            } else if(reward.getType().equals(Common.ITEM_TYPE_NOTE)) { // 消息类物品，立即显示给玩家
                JSONArray noteArray = notes.getJSONArray(reward.getId());
                showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+player.getName()+"获得一条消息");
                showDes(Common.randomString(noteArray,Common.JSON_NOTE));
            } else if(reward.getType().equals(Common.ITEM_TYPE_EQUIPMENT_ON)
                    || reward.getType().equals(Common.ITEM_TYPE_EQUIPMENT_OFF) ) {
                if(itemMap.containsKey(reward.getId())) {
                    Thing rewardItem = itemMap.get(reward.getId());
                    rewardItem.setUnit(rewardItem.getUnit()+reward.getUnit());
                    rewardNames.append("[").append(rewardItem.getName()).append("]");
                } else {
                    JSONObject obj = things.getJSONObject(reward.getId());
                    obj.put(Common.JSON_ID,reward.getId());
                    Thing rewardItem = new Equipment(things.getJSONObject(reward.getId()));
                    things.put(rewardItem.getId(),rewardItem.toJSONObject());
                    rewardItem.setUnit(reward.getUnit());
                    itemMap.put(reward.getId(),rewardItem);
                    rewardNames.append("[").append(rewardItem.getName()).append("]");
                }

            } else if(reward.getType().equals(Common.ITEM_TYPE_SUDDEN)) { // 突发类物品，直接触发事项
                JSONArray dataArray = suddens.getJSONArray(reward.getId()); // 查找要触发事项的随机数组
                JSONArray triggerArray = Common.randomJSONArray(dataArray,Common.JSON_TRIGGER);
                for(int j = 0; j < triggerArray.length();j++) {
                    triggers.add(triggerArray.getString(j));
                }
                doNext();
            }else {
                if(itemMap.containsKey(reward.getId())) {
                    Thing rewardItem = itemMap.get(reward.getId());
                    rewardItem.setUnit(rewardItem.getUnit()+reward.getUnit());
                    rewardNames.append("[").append(rewardItem.getName()).append("]");
                } else {
                    Item rewardItem = new Item();
                    rewardItem.setId(reward.getId());
                    rewardItem.setName(reward.getName());
                    rewardItem.setUnit(reward.getUnit());
                    rewardItem.setDesc(things.getJSONObject(reward.getId()).getString(Common.JSON_DESC));
                    rewardItem.setType(reward.getType());
                    itemMap.put(reward.getId(),rewardItem);
                    rewardNames.append("[").append(rewardItem.getName()).append("]");
                }
            }
        }
        private void useEquipment(Player player, String itemId) throws JSONException { // 玩家使用装备（装备或卸下）
            Map<String,Thing> itemMap = player.getOwnThings();
            Equipment equipment = (Equipment) itemMap.remove(itemId); // 玩家拥有装备物品
            String typeBefore = Common.ITEM_TYPE_EQUIPMENT_OFF;
            String typeAfter = Common.ITEM_TYPE_EQUIPMENT_ON;
            if(equipment.getType().equals(Common.ITEM_TYPE_EQUIPMENT_ON)) { // 当前装备状态为已装备状态，玩家正在卸下装备
                typeBefore = Common.ITEM_TYPE_EQUIPMENT_ON;
                typeAfter = Common.ITEM_TYPE_EQUIPMENT_OFF;
                player.setMaxcon(player.getMaxcon()-equipment.getMaxcon());
                player.setDex(player.getDex()-equipment.getDex());
                player.setStr(player.getStr()-equipment.getStr());
            } else { // 当玩家装备装备时，需要先卸下其它已经位于该位置的装备
                for(String id : itemMap.keySet()) {
                    Equipment e = (Equipment)itemMap.get(id);
                    if(e.getType().equals(Common.ITEM_TYPE_EQUIPMENT_ON)) {
                        if(e.getPosition().getKey().equals(equipment.getPosition().getKey())) { // 同样位置已经装备了装备，则卸下原装备
                            itemMap.remove(id);
                            player.setMaxcon(player.getMaxcon()-e.getMaxcon());
                            player.setDex(player.getDex()-e.getDex());
                            player.setStr(player.getStr()-e.getStr());
                            e.setType(typeAfter);
                            id = e.getId().replace(Common.ITEM_TYPE_EQUIPMENT_ON,Common.ITEM_TYPE_EQUIPMENT_OFF);
                            e.setId(itemId);
                            e.setType(Common.ITEM_TYPE_EQUIPMENT_OFF);
                            itemMap.put(id,e);
                            things.put(id,e.toJSONObject());
                            break;
                        }
                    }
                }
                player.setMaxcon(player.getMaxcon()+equipment.getMaxcon());
                player.setDex(player.getDex()+equipment.getDex());
                player.setStr(player.getStr()+equipment.getStr());
            }
            if(equipment.getUnit() > 1) { // 玩家拥有超过一个该装备
                equipment.setUnit(equipment.getUnit()-1);
                itemMap.put(itemId,equipment);
                things.put(itemId,equipment.toJSONObject());
                Equipment clone = equipment.clone();
                clone.setUnit(1);
                clone.setType(typeAfter);
                itemId = clone.getId().replace(typeBefore,typeAfter);
                clone.setId(itemId);
                clone.setType(typeAfter);
                itemMap.put(itemId,clone);
                things.put(itemId,clone.toJSONObject());
            } else {
                equipment.setType(typeAfter);
                itemId = equipment.getId().replace(typeBefore,typeAfter);
                equipment.setId(itemId);
                equipment.setType(typeAfter);
                itemMap.put(itemId,equipment);
                things.put(itemId,equipment.toJSONObject());
            }
            refreshPlayerItemList(player);
        }
        private void useItem(Player player, String itemId) throws JSONException {
            Map<String,Thing> itemMap = player.getOwnThings();
            Thing requireItem = itemMap.get(itemId); // 玩家拥有的任务物品
            if(requireItem.getUnit()==things.getJSONObject(itemId).getInt(Common.JSON_UNIT)) { // 玩家任务物品拥有量和物品的单位消耗量相等，全部用光删除物品
                itemMap.remove(itemId);
            } else if(requireItem.getUnit()>things.getJSONObject(itemId).getInt(Common.JSON_UNIT)){ // 玩家拥有的物品数量超出物品的单位消耗量，则在物品数量上减去消耗
                requireItem.setUnit(requireItem.getUnit()-things.getJSONObject(itemId).getInt(Common.JSON_UNIT));
            }
            refreshPlayerItemList(player);
        }

        private int calculateHurt(Player player,Player target) throws JSONException { // 计算玩家对玩家造成的伤害
            /*
             * 计算怪物闪避
             */
            double dodgeRate = 0.1;
            if((float)target.getDex()/target.getStr() - (float)player.getDex()/player.getStr() > 0) { // 认为越小巧越容易闪避成功（敏捷/力量）
                dodgeRate = 0.3;
            }
            if(dodgeRate > Math.random() && !gotFeature(player.getOwnThings(),Feature.ACCURATE)) { // 带有【精准】特性的攻击无法被闪避
                return 0;
            }
            int hurt;
            if(gotFeature(player.getOwnThings(),Feature.PENETRATION)) { // 带有【穿透】特性的攻击直接造成攻击者力量的伤害
                hurt = player.getStr();
            } else {
                hurt = player.getStr() - target.getStr() >0?player.getStr() - target.getStr():1; // 至少造成一点伤害
            }
            if(gotFeature(target.getOwnThings(),Feature.REDUCEINJURY)) { // 被攻击者有【减伤】特性
                hurt = (int) Math.floor(hurt*(1-Feature.ratio));
            }
            if(gotFeature(player.getOwnThings(),Feature.CRIT)) { // 攻击者有【重击】特性
                if(Common.randomDropout(Feature.percent)) { // 概率达成
                    hurt = hurt*2;
                }
            }
            return hurt;
        }

        private int calculateHurt(Player player,JSONObject monster) throws JSONException { // 计算玩家对怪物造成的伤害
            /*
             * 计算怪物闪避
             */
            double dodgeRate = 0.1;
            if((float)monster.getInt(Common.JSON_DEX)/monster.getInt(Common.JSON_STR) - (float)player.getDex()/player.getStr() > 0) { // 认为越小巧越容易闪避成功（敏捷/力量）
                dodgeRate = 0.3;
            }
            if(dodgeRate > Math.random() && !gotFeature(player.getOwnThings(),Feature.ACCURATE)) { // 带有【精准】特性的攻击无法被闪避
                return 0;
            }
            int hurt;
            if(gotFeature(player.getOwnThings(),Feature.PENETRATION)) { // 带有【穿透】特性的攻击直接造成攻击者力量的伤害
                hurt = player.getStr();
            } else {
                hurt = player.getStr() - monster.getInt(Common.JSON_STR) >0?player.getStr() - monster.getInt(Common.JSON_STR):1; // 至少造成一点伤害
            }
            if(gotFeature(monster.getJSONArray(Common.JSON_FEATURE),Feature.REDUCEINJURY)) { // 被攻击者有【减伤】特性
                hurt = (int) Math.floor(hurt*(1-Feature.ratio));
            }
            if(gotFeature(player.getOwnThings(),Feature.CRIT)) { // 攻击者有【重击】特性
                if(Common.randomDropout(Feature.percent)) { // 概率达成
                    hurt = hurt*2;
                }
            }
            return hurt;
        }
        private void showLog(String content) { // 在服务端消息列表中显示信息
            Message message = myHandler.obtainMessage();
            message.what = Common.SERVER_OUTPUT_FLAG; //消息标识
            message.obj = content+"\n";
            myHandler.sendMessage(message);
        }
        private void doNext() throws Exception {
            /*
             * 触发事项处理结束则显示行动选项
             */
            if(triggers.isEmpty()) {
                action();
            } else {
                trigger();
            }
        }
        private void showDes(String des) {
            msg = Common.SERVER_SHOWDESCRIPT+Common.SPLIT_SEPARATOR+des;
            sendMsg(Common.SERVER_SHOWDESCRIPT);
        }
        private void showAllDes(String des) {
            msg = Common.SERVER_SHOWDESCRIPT+Common.SPLIT_SEPARATOR+des;
            sendMsg();
        }
        private void action() throws JSONException { // 返回能执行的移动操作
            JSONArray ja = new JSONArray();
            if(actions.length()==0) { // 为了修复可移动项在操作频繁时被刷新为空的BUG，暂时这样重新获取一下
                Player currentPlayer = playerMap.get(socket.getInetAddress().toString());
                for(int i=0;i<nodes.length();i++) {
                    JSONObject node = nodes.getJSONObject(i);
                    if (node.getString(Common.JSON_ID).equals(currentPlayer.getPosition().getId())) {
                        actions = node.getJSONObject(Common.JSON_ACTION); // 查找指定节点可执行移动
                        break;
                    }
                }
            }
            if(actions.getString(Common.ACTION_EAST).split(Common.CMD_SEPARATOR).length > 1) {
                JSONObject east = new JSONObject();
                east.put("value",actions.getString(Common.ACTION_EAST).split(Common.CMD_SEPARATOR)[1]);
                east.put("text","["+Common.ACTION_EAST+"]"+actions.getString(Common.ACTION_EAST).split(Common.CMD_SEPARATOR)[0]);
                ja.put(east);
            }
            if(actions.getString(Common.ACTION_SOUTH).split(Common.CMD_SEPARATOR).length > 1) {
                JSONObject south = new JSONObject();
                south.put("value", actions.getString(Common.ACTION_SOUTH).split(Common.CMD_SEPARATOR)[1]);
                south.put("text", "[" + Common.ACTION_SOUTH + "]" + actions.getString(Common.ACTION_SOUTH).split(Common.CMD_SEPARATOR)[0]);
                ja.put(south);
            }
            if(actions.getString(Common.ACTION_WEST).split(Common.CMD_SEPARATOR).length > 1) {
                JSONObject west = new JSONObject();
                west.put("value", actions.getString(Common.ACTION_WEST).split(Common.CMD_SEPARATOR)[1]);
                west.put("text", "[" + Common.ACTION_WEST + "]" + actions.getString(Common.ACTION_WEST).split(Common.CMD_SEPARATOR)[0]);
                ja.put(west);
            }
            if(actions.getString(Common.ACTION_NORTH).split(Common.CMD_SEPARATOR).length > 1) {
                JSONObject north = new JSONObject();
                north.put("value", actions.getString(Common.ACTION_NORTH).split(Common.CMD_SEPARATOR)[1]);
                north.put("text", "[" + Common.ACTION_NORTH + "]" + actions.getString(Common.ACTION_NORTH).split(Common.CMD_SEPARATOR)[0]);
                ja.put(north);
            }
            msg = Common.SERVER_ACTION+Common.SPLIT_SEPARATOR+ja.toString();
            sendMsg(Common.SERVER_ACTION);
        }

        private void trigger() throws Exception { // 处理移动位置后的触发
            String item = triggers.remove(0); // item类型包含EVENT、FIGHT、TALK
            String[] cmd = item.split(Common.CMD_SEPARATOR);
            switch (cmd[0]) {
                case Common.CMD_NPC:
                    npcControl(cmd[1]);
                    break;
                case Common.CMD_EVENT:
                    eventControl(cmd[1]);
                    break;
                case Common.CMD_FIGHT:
                    fightControl(cmd[1]);
                    break;
                case Common.CMD_TALK:
                    talkControl(cmd[1]);
                    break;
                case Common.CMD_COST: // 对应回合数消耗
                    costControl(cmd[1]);
                    break;
            }
        }

        private void costControl(String costNum) throws JSONException {
            Player currentPlayer = playerMap.get(socket.getInetAddress().toString());
            int cost;
            if(null==costNum || costNum.equals("")) { // 睡觉消耗回合数从玩家属性中取得，不设置
                cost = currentPlayer.getCost_while_sleep();
            } else {
                cost = Integer.parseInt(costNum);
            }

            for(String id : currentPlayer.getOwnThings().keySet()) { // 懒惰诅咒让你休息时额外消耗一回合
                if(id.equals("SLOTH")) {
                    cost = cost + 1;
                    break;
                }
            }

            CompareEntity currentEntity = null;
            for(CompareEntity e : turnList) {
                if(e.getId().equals(currentPlayer.getIp())) {
                    currentEntity = e;
                    break;
                }
            }
            costTurn(currentEntity,cost);
        }

        private void costTurn(CompareEntity e,int cost) throws JSONException {
            if(null == e) {
                return;
            }
            updateTurn(e.getType(),e.getId(),e.getXh(),e.getDex(),e.getStr(),e.getTurn()+cost);
        }

        private void npcControl(String npcId) throws Exception {
            JSONObject npc = npcs.getJSONObject(npcId);
            npc.put(Common.JSON_ID,npcId);
            msg = Common.SERVER_NPC + Common.SPLIT_SEPARATOR + npc.toString();
            sendMsg(Common.SERVER_NPC);
        }

        private void eventControl(String eventId) throws JSONException {
            JSONObject event = events.getJSONObject(eventId);
            msg = Common.SERVER_EVENT + Common.SPLIT_SEPARATOR + event.toString();
            sendMsg(Common.SERVER_EVENT);
        }

        private void fightControl(String fightId) throws JSONException {
            JSONArray resultArray = new JSONArray();
            if(onFightMap.get(fightId)==null) { // 新的战斗
                JSONArray fightArray = fights.getJSONArray(fightId);
                JSONArray randomMonsters = Common.random(fightArray,Common.JSON_MONSTER);
                for (int i = 0; i < randomMonsters.length(); i++) {
                    String monsterId = randomMonsters.getString(i);
                    JSONObject monster = monsters.getJSONObject(monsterId);
                    JSONObject temp = new JSONObject();
                    temp.put(Common.JSON_ID, fightId+"-"+monsterId);
                    temp.put(Common.JSON_NAME, monster.getString(Common.JSON_NAME));
                    temp.put(Common.JSON_CON, monster.getInt(Common.JSON_CON));
                    temp.put(Common.JSON_STR, monster.getInt(Common.JSON_STR));
                    temp.put(Common.JSON_DEX, monster.getInt(Common.JSON_DEX));
                    temp.put(Common.JSON_DESC, monster.getString(Common.JSON_DESC));
                    temp.put(Common.JSON_FEATURE, monster.getJSONArray(Common.JSON_FEATURE));
                    resultArray.put(temp);
                    int turn = 0;
                    for(CompareEntity c : turnList) {
                        if(c.getId().equals(socket.getInetAddress().toString())) {
                            turn = c.getTurn(); // 使用当前玩家的turn作为怪物的turn以保证玩家与怪物依靠dex作为行动次序依据
                            break;
                        }
                    }
                    addTurn(Common.ROLETYPE_MONSTER,fightId+"-"+monsterId,i+1,Integer.parseInt(monster.getString(Common.JSON_DEX)),Integer.parseInt(monster.getString(Common.JSON_STR)),turn); // 将遭遇到的怪物加入行动列表中（为了保证怪物ID的唯一性，拼接战斗ID到怪物ID前，则ID+XH可以唯一确定一个怪物）
                }

                onFightMap.put(fightId,resultArray); // 记录正在进行的项目
            } else { // 已经存在的战斗（战斗目标从已经存在的映射关系中取得），玩家加入了其他玩家的战斗
                resultArray = onFightMap.get(fightId);
            }

            Fight fight = new Fight();
            fight.setId(fightId);
            List<String> monsterIds = new ArrayList<>();
            for(int i=0;i<resultArray.length();i++) {
                JSONObject m = resultArray.getJSONObject(i);
                monsterIds.add(m.getString(Common.JSON_ID));
            }
            fight.setMonsterList(monsterIds);
            fightMap.put(socket.getInetAddress().toString(),fight); // 记录玩家参与的战斗

            StringBuilder monsterNames = new StringBuilder();
            for(int i=0;i<resultArray.length();i++) {
                monsterNames.append("[").append(resultArray.getJSONObject(i).getString(Common.JSON_NAME)).append("]");
            }
            Player p = playerMap.get(socket.getInetAddress().toString());
            showLog(Common.SERVER_FIGHT+Common.SPLIT_SEPARATOR+p.getName()+"与"+monsterNames.toString()+"开始进行战斗！");
            JSONObject result = new JSONObject();
            result.put(Common.JSON_ID,fightId);
            result.put(Common.JSON_MONSTER,resultArray);
            msg = Common.SERVER_FIGHT+Common.SPLIT_SEPARATOR+result.toString(); // 通过概率生成怪物，发送给玩家供选择攻击
            sendMsg(Common.SERVER_FIGHT);
        }

        private void talkControl(String taklId) throws Exception {
            JSONObject dialog = dialogs.getJSONObject(taklId);
            String talkerId = dialog.getString(Common.JSON_TALKER);
            String talker = npcs.getJSONObject(talkerId).getString(Common.JSON_NAME);
            String content = dialog.getString(Common.JSON_CONTENT);
            JSONArray choices = dialog.getJSONArray(Common.JSON_CHOICES);
            if(npcs.getJSONObject(talkerId).getInt(Common.JSON_CON) == 0) {
                showDes("此人已死");
                doNext();
                return;
            }
            if(!hiddens.isNull(taklId)) { // 当前地点有隐藏选项
                JSONArray hiddenChoices = hiddens.getJSONArray(taklId);
                for(int i=0;i<hiddenChoices.length();i++) {
                    JSONObject hiddenChoice = hiddenChoices.getJSONObject(i);
                    Player currentPlayer = playerMap.get(socket.getInetAddress().toString());
                    Map<String,Thing> ownThins = currentPlayer.getOwnThings();
                    JSONArray conditions = hiddenChoice.getJSONArray(Common.JSON_CONDITION);
                    for(int j=0;j<choices.length();j++) { // 先移除隐藏选项（曾经达成条件的情况下加入过）
                        JSONObject temp = choices.getJSONObject(j);
                        if(temp.getString(Common.JSON_VALUE).equals(hiddenChoice.getString(Common.JSON_TASK))
                                && temp.getString(Common.JSON_TEXT).equals(hiddenChoice.getString(Common.JSON_NAME))) {
                            Common.removeJSONArray(j,choices);
                            break;
                        }

                    }
                    boolean isMatch = true; // 隐藏选项触发条件达成标志
                    for(int j=0;j<conditions.length();j++) {
                        JSONObject condition = conditions.getJSONObject(j);
                        if(!condition.getString(Common.JSON_TALKER).equals(talkerId) || // 聊天对象匹配
                                null == ownThins.get(condition.getString(Common.JSON_THING)) || // 需要物品匹配
                                !(ownThins.get(condition.getString(Common.JSON_THING)).getUnit() >= condition.getInt(Common.JSON_UNIT))) { // 拥有数量大于等于需求数量
                            isMatch = false;
                            break;
                        }
                    }
                    if(isMatch) { // 显示隐藏条件的前提达成，拼接到可选选项中
                        JSONObject addChoice = new JSONObject();
                        addChoice.put(Common.JSON_TEXT,hiddenChoice.getString(Common.JSON_NAME));
                        addChoice.put(Common.JSON_VALUE,hiddenChoice.getString(Common.JSON_TASK));
                        choices.put(addChoice);
                    }
                }
            }
            msg = Common.SERVER_PIKER+Common.SPLIT_SEPARATOR+"["+talker+"]"+content+Common.ITEM_SEPARATOR+choices.toString();
            sendMsg(Common.SERVER_PIKER);
        }
        private void sendMsg(String action) {
            if(Common.isIp(action.substring(1))) { // 指定IP通信（0.0.0.0的情况在上游排除了）
                try {
                    OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                    BufferedWriter bw = new BufferedWriter(osw);
                    if(msg.split(Common.SPLIT_SEPARATOR).length > 1 && !(msg.split(Common.SPLIT_SEPARATOR)[0].equals(Common.SERVER_OUTPUT))) {
//                            && ((msg.split(Common.SPLIT_SEPARATOR)[0].equals(Common.SERVER_REFRESHPERSIONS)) || (msg.split(Common.SPLIT_SEPARATOR)[0].equals(Common.SERVER_MONSTER_FIGHT))
//                            ||(msg.split(Common.SPLIT_SEPARATOR)[0].equals(Common.SERVER_REFRESH_FIGHT_PERSION))||(msg.split(Common.SPLIT_SEPARATOR)[0].equals(Common.SERVER_REFRESH_FIGHT_MONSTER))
//                    ||(msg.split(Common.SPLIT_SEPARATOR)[0].equals(Common.SERVER_REFRESHPLAYER)))) { // 通知指定IP的玩家刷新列表
                        for(Socket s : clients) {
                            if(s.getInetAddress().toString().equals(action)) {
                                osw = new OutputStreamWriter(s.getOutputStream());
                                bw = new BufferedWriter(osw);
                                bw.write(msg + "\n");
                                bw.flush();
                                break;
                            }
                        }
                    } else {
                        /*
                         * 指定IP通信时发信方和收信方都要收到信息
                         */
                        bw.write(msg + "\n");
                        bw.flush();
                        for(Socket s : clients) {
                            if(s.getInetAddress().toString().equals(action)) {
                                osw = new OutputStreamWriter(s.getOutputStream());
                                bw = new BufferedWriter(osw);
                                bw.write(msg + "\n");
                                bw.flush();
                                break;
                            }
                        }
                        showLog(msg);
                    }
                } catch (Exception ex) {
                    System.out.println("发送消息发生异常");
                }
            }
//            else if(action.equals(Common.SERVER_USERS) || action.equals(Common.SERVER_ROLES)
//                    || action.equals(Common.SERVER_SHOWDESCRIPT) || action.equals(Common.SERVER_ACTION)
//                    || action.equals(Common.SERVER_PIKER) ) { // 获取其他玩家昵称|获取角色列表|显示玩家所在地点的描述信息|获取玩家可执行动作|获取玩家可选择选项
//                try {
//                    OutputStreamWriter osw = new OutputStreamWriter(outputStream);
//                    BufferedWriter bw = new BufferedWriter(osw);
//                    bw.write(msg + "\n");
//                    bw.flush();
//                } catch (Exception ex) {
//                }
//            }
            else if(action.equals(Common.SERVER_SHOWPLAYER)) { // 显示玩家
                try {
                    Message message = myHandler.obtainMessage();
                    message.what = Common.SERVER_SHOW_FLAG; //消息标识
                    message.obj = msg+"\n";
                    myHandler.sendMessage(message); //发送消息
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        OutputStreamWriter osw = new OutputStreamWriter(clients.get(i).getOutputStream());
                        BufferedWriter bw = new BufferedWriter(osw);
                        bw.write(msg+"\n");
                        bw.flush();
                    }
                } catch (Exception ex) {
                    System.out.println("发送消息发生异常");
                }
            }
            else {
                try {
                    OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(msg + "\n");
                    bw.flush();
                    if(action.equals(Common.SERVER_MOVE)  || action.equals(Common.SERVER_FIGHT)) // 触发其他玩家行动
                    notifyNext();
                } catch (Exception ex) {
                    System.out.println("发送消息发生异常");
                }
            }
        }
        private void notifyNext() throws IOException, JSONException { // 通知下一个行动对象行动
            if(turnList.size()==0) { // 一旦玩家全部死亡
                showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+"玩家全部死亡，一切归于混沌！");
                return;
            }
            CompareEntity e = nextTurnon();
            if(e.getType().equals(Common.ROLETYPE_PLAYER)) { // 下一个行动对象是玩家的情况下发送可以行动的消息给相应玩家
                for (int i = clients.size() - 1; i >= 0; i--) {
                    if(clients.get(i).getInetAddress().toString().equals(e.getId())) {
                        Message message = myHandler.obtainMessage();
                        message.what = Common.SERVER_REFRESHPLAYER_FLAG; //消息标识
                        message.obj = e.getId();
                        myHandler.sendMessage(message);
                        currentMoveId = e.getId(); // 记录最新的行动玩家ID
                        Player currentPlayer = playerMap.get(e.getId());
                        currentPlayer.setHungry(currentPlayer.getHungry()>1?currentPlayer.getHungry()-1:0);
                        if(currentPlayer.getHungry()==0) { // 由于饥饿死亡
                            removeTurnObj(e.getId());
                            playerNum--;
                            notifyRefreshPersionList(currentPlayer.getPosition().getId());
                        }

                        OutputStreamWriter osw = new OutputStreamWriter(clients.get(i).getOutputStream());
                        BufferedWriter bw = new BufferedWriter(osw);
                        bw.write(Common.SERVER_TURNON+Common.SPLIT_SEPARATOR+currentPlayer.getHungry()+"\n");
                        bw.flush();
                        break;
                    }
                }
            } else { // 下一个行动对象是怪物的情况下
                e.setTurn(e.getTurn()+1);
                calculateFightTarget(e);
                notifyNext();
            }
        }
        private void calculateFightTarget(CompareEntity e) throws JSONException {
            List<Player> targetList = new ArrayList<>();
            Player temp = new Player();
            JSONObject m = monsters.getJSONObject(e.getId().substring(e.getId().lastIndexOf("-")+1));
            JSONArray features = m.getJSONArray(Common.JSON_FEATURE);
            if(gotFeature(features,Feature.MULTIPLE)) { // 怪物具备【多重射】特性
                for(String ip:fightMap.keySet()) {
                    temp = playerMap.get(ip);
                    targetList.add(temp);
                }
            } else {
                for(String ip:fightMap.keySet()) {
                    if(e.getId().contains(fightMap.get(ip).getId())) { // 找到所有与这个怪物作战的玩家
                        if(playerMap.get(ip).getThreat_from_a_warrior() >= temp.getThreat_from_a_warrior()) // 查询到仇恨值最高的玩家予以攻击
                            temp = playerMap.get(ip);
                    }
                }
                targetList.add(temp);
            }

            if(targetList.size()!=0) {
                for(Player target:targetList) {
                    int hurt = calculateHurt(e,target,features);

                    if(hurt == 0) {
                        showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+m.getString(Common.JSON_NAME)+"对玩家"+target.getName()+"的攻击被玩家闪避了！");
                    } else {
                        if(gotFeature(features,Feature.HEMOPHAGIA)) { // 怪物具备【吸血】特性
                            String fightId = e.getId().substring(0,e.getId().lastIndexOf("-"));
                            JSONArray monsterArray = onFightMap.get(fightId);
                            JSONObject monster = monsterArray.getJSONObject(e.getXh());
                            int sockBlood = (int) Math.floor(hurt*Feature.ratio);
                            monster.put(Common.JSON_CON,monster.getInt(Common.JSON_CON)+sockBlood > monster.getInt(Common.JSON_MAXCON) ? monster.getInt(Common.JSON_MAXCON) : monster.getInt(Common.JSON_CON)+sockBlood);
                            notifyRefreshFightList(fightId);
                        }
                        if(target.getCon()-hurt <= 0) {

                            hurt = target.getCon();
                            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+m.getString(Common.JSON_NAME)+"对玩家"+target.getName()+"造成了"+hurt+"点伤害，导致其死亡！");
                            removeTurnObj(target.getIp());
                            playerNum--;
                            removeFromFight(target.getIp());
                        } else {
                            showLog(Common.SERVER_OUTPUT_FLAG+Common.SPLIT_SEPARATOR+m.getString(Common.JSON_NAME)+"对玩家"+target.getName()+"造成了"+hurt+"点伤害！");
                        }
                        target.setCon(target.getCon()-hurt);
                    }
                    msg = Common.SERVER_MONSTER_FIGHT+Common.SPLIT_SEPARATOR+hurt+Common.CMD_SEPARATOR+m.getString(Common.JSON_NAME);
                    sendMsg(target.getIp());
                    notifyRefreshPersionList(target.getPosition().getId());
                    if(target.getCon()==0) { // 死亡玩家自行脱离战斗，更新玩家控制面板
                        msg = Common.SERVER_ESCAPERESULT+Common.SPLIT_SEPARATOR+target.getPosition().getId();
                        sendMsg(target.getIp());
                    }

                }
            }
        }

        private void notifyRefreshFightList(String fightId) throws JSONException {
            JSONObject result = new JSONObject();
            JSONArray monsterArray = onFightMap.get(fightId);
            result.put(Common.JSON_MONSTER,monsterArray);
            result.put(Common.JSON_ID,fightId);
            msg = Common.SERVER_REFRESH_FIGHT_MONSTER+Common.SPLIT_SEPARATOR+result.toString();
            Fight f = fightMap.get(socket.getInetAddress().toString()); // 获取当前这场战斗
            List<String> ipList = new ArrayList<>();
            for(String ip:fightMap.keySet()) {
                if(fightMap.get(ip).getId().equals(f.getId())) { // 判断是否有其他玩家在这场战斗中
                    ipList.add(ip);
                }
            }
            for(String ip : ipList) { // 通知参与这场战斗的玩家刷新怪物列表
                sendMsg(ip);
            }
        }
        private int calculateHurt(CompareEntity monster,Player player,JSONArray features) throws JSONException { // 计算怪物或NPC对玩家造成的伤害

            /*
             * 计算玩家闪避
             */
            double dodgeRate = 0.1;
            if((float)monster.getDex()/monster.getStr() - (float)player.getDex()/player.getStr() > 0) { // 认为越小巧越容易闪避成功（敏捷/力量）
                dodgeRate = 0.3;
            }
            int hurt;
            if(dodgeRate > Math.random() && !gotFeature(features,Feature.ACCURATE)) { // 带有【精准】特性的攻击无法被闪避
                return 0;
            } else {
                if(gotFeature(features,Feature.PENETRATION)) { // 带有【穿透】特性的攻击直接造成攻击者力量的伤害
                    hurt = monster.getStr();
                } else {
                    hurt = monster.getStr() - player.getStr() > 0 ? monster.getStr() - player.getStr():1;// 最少造成一点伤害
                }

                for(String thingId : player.getOwnThings().keySet()) { // 暴怒诅咒，使你受到伤害增加20%
                    if(thingId.equals("WRATH")) {
                        hurt = (int) Math.floor(hurt*(1+Common.CURSE_RATIO));
                        break;
                    }
                }
            }
            if(gotFeature(player.getOwnThings(),Feature.REDUCEINJURY)) { // 被攻击者有【减伤】特性
                hurt = (int) Math.floor(hurt*(1-Feature.ratio));
            }
            if(gotFeature(features,Feature.CRIT)) { // 攻击者有【重击】特性
                if(Common.randomDropout(Feature.percent)) { // 概率达成
                    hurt = hurt*2;
                }
            }
            return hurt;
        }

        private void sendMsg() { // 发送消息给所有用户
            try {
//                System.out.println(msg)
                showLog(msg);
                for (int i = clients.size() - 1; i >= 0; i--) {
                    OutputStreamWriter osw = new OutputStreamWriter(clients.get(i).getOutputStream());
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(msg+"\n");
                    bw.flush();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void dialog_Exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        long awaitTime = 3 * 1000;
                        closed = true;
                        try {
                            pool.shutdown();

                            // (所有的任务都结束的时候，返回TRUE)
                            if(!pool.awaitTermination(awaitTime, TimeUnit.MILLISECONDS)){
                                // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                                pool.shutdownNow();
                            }
                        } catch (InterruptedException e) {
                            // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
                            System.out.println("awaitTermination interrupted: " + e);
                            pool.shutdownNow();
                        }
                        if(null != mServerSocket) {
                            try {
                                mServerSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        getActivity().finish();
                    }
                });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
}
