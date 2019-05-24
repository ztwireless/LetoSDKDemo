package com.leto.game.sample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ledong.lib.leto.Leto;
import com.ledong.lib.leto.LetoScene;
import com.ledong.lib.leto.listener.ILetoAdRewardListener;
import com.leto.game.base.listener.IJumpListener;
import com.leto.game.base.listener.ILoginListener;
import com.leto.game.base.listener.JumpError;
import com.leto.game.base.util.ToastUtil;

public class SampleActivity extends FragmentActivity {
    private Button btn_start_more;
    private Button btn_start_game;
    private Button btn_start_local_game;
    private Button btn_sync_mobile;
    private EditText et_game_id;

    final String appId = "364253";    //默认游戏id


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setContentView(R.layout.sample_activity);

        btn_start_more = findViewById(R.id.btn_start_more);
        btn_start_game = findViewById(R.id.btn_start_game);
        btn_sync_mobile = findViewById(R.id.btn_sync_mobile);
        et_game_id = findViewById(R.id.et_game_id);
        et_game_id.setText(appId);

        btn_start_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Leto.getInstance().startGameCenter(SampleActivity.this);
            }
        });

        btn_start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String gameId = et_game_id.getText().toString();
                if(TextUtils.isEmpty(gameId)){
                    ToastUtil.s(SampleActivity.this, "请输入游戏Id");
                    return;
                }

                Leto.getInstance().jumpMiniGameWithAppId(SampleActivity.this, gameId, LetoScene.DEFAULT, new IJumpListener() {
                    @Override
                    public void onDownloaded(String s) {

                    }

                    @Override
                    public void onLaunched() {

                    }

                    @Override
                    public void onError(JumpError jumpError, String s) {

                    }
                });

            }
        });

        btn_sync_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SampleActivity.this, SyncAccountActivity.class));
            }
        });

        //设置广告监听
        Leto.getInstance().setLetoAdRewardListener(new ILetoAdRewardListener() {
            @Override
            public void onVideoAdComplete(String adPlatform, String gameId) {

                Log.i("onVideoAdComplete", "ad platform : " + adPlatform + "------gameId:" + gameId);

            }
        });
    }
}

