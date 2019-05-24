package com.ledong.lib.minigame;

import android.support.annotation.Keep;

import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameModel;

/**
 * Create by zhaozhihui on 2018/11/9
 **/
@Keep
public interface IGameSwitchListener {
    void onJump(String appId, String appPath, int isCollect, String apkUrl, String apkPackageName, String gameName, int isCps, String splashUrl, int is_kp_ad, int is_more, String icon, String share_url, String share_msg, String package_url, int game_type, String orientation);
    void onJump(GameCenterData_Game gameBean);
}
