package com.ledong.lib.minigame.view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ledong.lib.minigame.IGameSwitchListener;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.ClickUtil;
import com.leto.game.base.util.MResource;
import com.leto.game.base.util.ToastUtil;

public class PlayNowButton extends AppCompatButton {
    private static final String TAG = PlayNowButton.class.getSimpleName();
    private GameCenterData_Game gameBean;
    IGameSwitchListener mGameSwitchListener;

    public PlayNowButton(Context context) {
        super(context);
        initUI();
    }

    public PlayNowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public PlayNowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.isFastClick()){
                    Log.d(TAG, "Is double click");
                    return;
                }

                Log.d(TAG, "click game=" + gameBean.getId());
                if (TextUtils.isEmpty(gameBean.getPackageurl())) {
                    ToastUtil.s(getContext(), MResource.getIdByName(getContext(), "R.string.leto_game_not_online"));
                    return;
                }

                if (null != mGameSwitchListener) {
                    mGameSwitchListener.onJump(gameBean);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setGameBean(GameCenterData_Game gameBean) {
        this.gameBean = gameBean;
    }

    public GameCenterData_Game getGameBean() {
        return gameBean;
    }

    public void setGameSwitchListener(IGameSwitchListener switchListener) {
        mGameSwitchListener = switchListener;
    }
}
