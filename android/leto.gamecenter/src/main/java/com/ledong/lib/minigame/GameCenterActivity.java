package com.ledong.lib.minigame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ledong.lib.leto.Leto;
import com.ledong.lib.leto.LetoConst;
import com.ledong.lib.leto.LetoScene;
import com.ledong.lib.leto.main.LetoActivity;
import com.leto.game.base.so.SdkNative;
import com.leto.game.base.statistic.GameStatisticManager;
import com.leto.game.base.statistic.ReportTaskManager;
import com.leto.game.base.util.IntentConstant;
import com.leto.game.base.util.MResource;

/**
 * Created by DELL on 2018/8/4.
 */

public class GameCenterActivity extends FragmentActivity {
    private static final String TAG = "GameCenterActivity";

    ImageView ivBack;
    TextView tvTitle;

    TextView tv_favorite;

    String orientation = "portrait";
    String srcAppId;
    String srcAppPath;

    AlertDialog alertDialog;

    //服务器上报返回的标签，可以在暂停，退出上报时用。 如果未返回，则使用当前时间戳
    private String mServiceKey;
    private String mClientKey;

    boolean isStartReported =false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init leto
        Leto.init(this);

        // set content view
        setContentView(MResource.getIdByName(this, "R.layout.leto_gamecenter_activity"));

        orientation = getIntent().getStringExtra(IntentConstant.ACTION_APP_ORIENTATION);
        srcAppId = getIntent().getStringExtra(IntentConstant.SRC_APP_ID);
        srcAppPath = getIntent().getStringExtra(IntentConstant.SRC_APP_PATH);

        ivBack = findViewById(MResource.getIdByName(this, "R.id.iv_back"));

        tv_favorite = findViewById(MResource.getIdByName(this, "R.id.tv_favorite"));
        tvTitle = findViewById(MResource.getIdByName(this, "R.id.tv_title"));

        if (null != ivBack) {
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (null != tv_favorite) {
            tv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavoriteActivity.start(GameCenterActivity.this, srcAppId, srcAppPath);
                }
            });
        }
        tvTitle.setText(getResources().getString(MResource.getIdByName(GameCenterActivity.this, "R.string.leto_title_gamecenter")));

        // detect system version, if too low, give a hint
        checkSystemVersion();

        // install content fragment
        GameCenterHomeFragment fragment = new GameCenterHomeFragment();
        getSupportFragmentManager().beginTransaction()
            .add(MResource.getIdByName(this, "R.id.home_content"), fragment)
            .commit();


        if(TextUtils.isEmpty(mClientKey)){
            mClientKey = String.valueOf(System.currentTimeMillis());
        }

        ReportTaskManager.getInstance(GameCenterActivity.this);
        ReportTaskManager.getInstance(GameCenterActivity.this).setClientKey(mClientKey);
        ReportTaskManager.getInstance(GameCenterActivity.this).setServiceKey(null);
        isStartReported = true;

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void checkSystemVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            //创建AlertDialog的构造器的对象
            AlertDialog.Builder builder = new AlertDialog.Builder(GameCenterActivity.this);
            //设置构造器标题
//            builder.setTitle("提示");
            //构造器对应的图标
//            builder.setIcon(R.mipmap.ic_launcher);
            //构造器内容,为对话框设置文本项(之后还有列表项的例子)
            builder.setCancelable(false);
            builder.setMessage(getString(MResource.getIdByName(GameCenterActivity.this, "R.string.leto_toast_the_system_version_low")));
            //为构造器设置确定按钮,第一个参数为按钮显示的文本信息，第二个参数为点击后的监听事件，用匿名内部类实现
            builder.setPositiveButton(getString(MResource.getIdByName(GameCenterActivity.this, "R.string.leto_know_it")), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            //为构造器设置取消按钮,若点击按钮后不需要做任何操作则直接为第二个参数赋值null
//            builder.setNegativeButton("不呀",null);
            //为构造器设置一个比较中性的按钮，比如忽略、稍后提醒等
//            builder.setNeutralButton("稍后提醒",null);
            //利用构造器创建AlertDialog的对象,实现实例化
            alertDialog = builder.create();
            alertDialog.show();
        }

    }
    @Keep
    public static void start(Context context, String orientation, String appId, String appPath) {
        if (null != context) {
            Intent intent = new Intent(context, GameCenterActivity.class);
            intent.putExtra(IntentConstant.ACTION_APP_ORIENTATION, orientation);
            intent.putExtra(IntentConstant.SRC_APP_ID, appId);
            intent.putExtra(IntentConstant.SRC_APP_PATH, appPath);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        }
    }
    @Override
    public void onResume(){
        super.onResume();

        //report
        ReportTaskManager.getInstance(GameCenterActivity.this).setClientKey(mClientKey);
        ReportTaskManager.getInstance(GameCenterActivity.this).setServiceKey(mServiceKey);
        ReportTaskManager.getInstance(GameCenterActivity.this).sendStartLog(GameCenterActivity.this, "", LetoConst.STATISTIC_GAMECENTER_IN, 0, false,null);

    }


    @Override
    public void onPause(){
        super.onPause();

        //report
        ReportTaskManager.getInstance(GameCenterActivity.this).sendEndLog(GameCenterActivity.this, "", LetoConst.STATISTIC_GAMECENTER_OUT, 0);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

}
