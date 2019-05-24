package com.ledong.lib.minigame;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.bean.DataRefreshEvent;
import com.ledong.lib.minigame.view.dialog.CustomDialog;
import com.leto.game.base.util.DataCleanManager;
import com.leto.game.base.util.DialogUtil;
import com.leto.game.base.util.GameUtil;
import com.leto.game.base.util.IntentConstant;
import com.leto.game.base.util.MResource;
import com.leto.game.base.util.StorageUtil;
import com.leto.game.base.util.ToastUtil;
import com.leto.game.base.view.tablayout.CommonTabLayout;
import com.leto.game.base.view.tablayout.entity.TabEntity;
import com.leto.game.base.view.tablayout.listener.CustomTabEntity;
import com.leto.game.base.view.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */

public class FavoriteActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = FavoriteActivity.class.getSimpleName();

    ImageView ivBack;
    ListView listView;
    TextView tvRight;

    CommonTabLayout indicator;
    ViewPager viewPager;

    String srcAppId;
    String srcAppPath;
    int tabIndex =0;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] mTitles =new String[2];

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(MResource.getIdByName(this, "R.layout.leto_gamecenter_favorite_activity"));

        if (null != getIntent()) {
            srcAppId = getIntent().getStringExtra(IntentConstant.SRC_APP_ID);
            srcAppPath = getIntent().getStringExtra(IntentConstant.SRC_APP_PATH);
            tabIndex = getIntent().getIntExtra(IntentConstant.FAVORITE_TAB_INDEX, 0);
        }

        ivBack = findViewById(MResource.getIdByName(this, "R.id.iv_back"));
        tvRight = findViewById(MResource.getIdByName(this, "R.id.tv_right"));
        tvRight.setOnClickListener(this);

        if (null != ivBack) {
            ivBack.setOnClickListener(this);
        }

        mTitles[0] = getString(MResource.getIdByName(this, "R.string.leto_title_favorite_my_favorite"));
        mTitles[1] = getString(MResource.getIdByName(this, "R.string.leto_title_favorite_latest_play"));

        fragments.add(SingleGameListFragment.getInstance(Constant.GAME_LIST_FAVORITE, null, "portrait", srcAppId, srcAppPath, 0, 0));
        fragments.add(SingleGameListFragment.getInstance(Constant.GAME_LIST_RECENT_PLAYED, null, "portrait", srcAppId, srcAppPath, 0, 0));

        viewPager = findViewById(MResource.getIdByName(this, "R.id.viewPager"));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }

        indicator = findViewById(MResource.getIdByName(this, "R.id.indicator"));
        indicator.setTabData(mTabEntities);
        indicator.setCurrentTab(0);
        indicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(tabIndex==0?0:1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View v) {
        if (null != tvRight && v.getId() == tvRight.getId()) {
            new CustomDialog().showClearCacheDialog(FavoriteActivity.this, new CustomDialog.ConfirmDialogListener() {
                @Override
                public void onConfirm() {

                    try {
                        clearCache();


                    }catch (Exception e){

                    }
                }

                @Override
                public void onCancel() {

                }
            });
        } else if (null != ivBack && v.getId() == ivBack.getId()) {
            finish();
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }

    public static void start(Context context, String srcAppId, String srcAppPath) {
        Intent intent = new Intent(context, FavoriteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(IntentConstant.SRC_APP_ID, srcAppId);
        intent.putExtra(IntentConstant.SRC_APP_PATH, srcAppPath);
        context.startActivity(intent);
    }

    public static void start(Context context, String srcAppId, String srcAppPath, int type) {
        Intent intent = new Intent(context, FavoriteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(IntentConstant.SRC_APP_ID, srcAppId);
        intent.putExtra(IntentConstant.SRC_APP_PATH, srcAppPath);
        intent.putExtra(IntentConstant.FAVORITE_TAB_INDEX, type);
        context.startActivity(intent);
    }

    private void clearCache() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        new AsyncTask<String, Object, Void>() {
            @Override
            protected void onPreExecute() {
                DialogUtil.showDialog(FavoriteActivity.this, getResources().getString(MResource.getIdByName(FavoriteActivity.this, "R.string.leto_toast_begin_to_clear_up")));
            }

            @Override
            protected Void doInBackground(String... params) {
                DataCleanManager.cleanInternalCache(FavoriteActivity.this);
                DataCleanManager.cleanExternalCache(FavoriteActivity.this);
                DataCleanManager.cleanWebview(FavoriteActivity.this);
                File appFile = StorageUtil.getLetoAppDir(FavoriteActivity.this);
                DataCleanManager.cleanCustomCache(appFile.getAbsolutePath());

                GameUtil.clearAllFiles(FavoriteActivity.this);

                //send refresh message
                EventBus.getDefault().post(new DataRefreshEvent());

                return null;
            }

            @Override
            protected void onPostExecute(Void data) {

                DialogUtil.dismissDialog();

                ToastUtil.s(FavoriteActivity.this, getResources().getString(MResource.getIdByName(FavoriteActivity.this, "R.string.leto_toast_clear_up")));
            }
        }.executeOnExecutor(executorService);
    }

}
