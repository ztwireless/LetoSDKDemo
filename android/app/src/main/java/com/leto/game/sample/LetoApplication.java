package com.leto.game.sample;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.ledong.lib.leto.Leto;


public class LetoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //
        MultiDex.install(this);

        //SDK 初始化
        //LetoManager.init(this);

        //SDK 初始化 指定接入渠道
        Leto.init(this);


    }

}
