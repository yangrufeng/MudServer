package com.yangrufeng.mud.mudserver;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import com.yangrufeng.mud.mudserver.common.Common;
import com.yangrufeng.mud.mudserver.common.GetLocalIpAddress;
import com.yangrufeng.mud.mudserver.fragments.CreateGameFragment;
import com.yangrufeng.mud.mudserver.fragments.MainFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView hostipTextView;

    /** 管理fragment */
    private HashMap<String,Fragment> fragments = new HashMap<>();

    //当前activity的fragment控件
    private int fragmentContentId = R.id.fragment_content;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    private int playerNum;

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    private String mod = Common.MOD_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        hostipTextView = (TextView) this.findViewById(R.id.hostip_value);
        hostipTextView.setText(GetLocalIpAddress.getLocalIpAdress());
        initFragments();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(fragmentContentId, fragments.get("CREATE"));
        transaction.commit();
    }

    private void initFragments() {
        fragments.put("CREATE",new CreateGameFragment());
        fragments.put("MAIN",new MainFragment());
    }

    public void replaceFragment(String key) {
        transaction = manager.beginTransaction();
        transaction.replace(fragmentContentId, fragments.get(key));
        transaction.commit();
    }

    //返回键监听实现
    public interface FragmentBackListener {
        void onBackForward();
    }

    private FragmentBackListener backListener;
    private boolean isInterception = false;

    public void setBackListener(FragmentBackListener backListener) {
        this.backListener = backListener;
    }
    //是否拦截
    public boolean isInterception() {
        return isInterception;
    }

    public void setInterception(boolean isInterception) {
        this.isInterception = isInterception;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isInterception()) {
                if (backListener != null) {
                    backListener.onBackForward();
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
