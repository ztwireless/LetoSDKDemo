package com.ledong.lib.minigame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kymjs.rxvolley.RxVolley;
import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.leto.utils.JsonUtil;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameCenterData_Rank;
import com.leto.game.base.bean.GameCenterRequestBean;
import com.leto.game.base.bean.GameCenterResultBean;
import com.leto.game.base.http.HttpCallbackDecode;
import com.leto.game.base.http.SdkApi;
import com.leto.game.base.util.IntentConstant;
import com.leto.game.base.util.MResource;
import com.leto.game.base.util.ToastUtil;
import com.leto.game.base.view.tablayout.CommonTabLayout;
import com.leto.game.base.view.tablayout.entity.TabEntity;
import com.leto.game.base.view.tablayout.listener.CustomTabEntity;
import com.leto.game.base.view.tablayout.listener.OnTabSelectListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllRankingActivity extends FragmentActivity implements OnTabSelectListener, ViewPager.OnPageChangeListener {
	private static final String MODEL = "model";

	// views
	private CommonTabLayout _tabs;
	private ViewPager _viewPager;
	private TextView _titleLabel;
	private ImageView _backView;

	// title of tabs
	private List<String> _titles;

	// model
	private GameCenterData _model;

	// extras
	private String _orientation = "portrait";
	private String _srcAppId;
	private String _srcAppPath;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content
		setContentView(MResource.getIdByName(this, "R.layout.leto_all_ranking_activity"));

		// get extras
		Bundle bundle = getIntent().getExtras();
		_orientation = bundle.getString(IntentConstant.ACTION_APP_ORIENTATION, "portrait");
		_srcAppId = bundle.getString(IntentConstant.SRC_APP_ID);
		_srcAppPath = bundle.getString(IntentConstant.SRC_APP_PATH);
		Serializable model = bundle.getSerializable(MODEL);
		if(model != null) {
			_model = (GameCenterData)model;
		}

		// views
		_tabs = findViewById(MResource.getIdByName(this, "R.id.tabs"));
		_viewPager = findViewById(MResource.getIdByName(this, "R.id.viewPager"));
		_titleLabel = findViewById(MResource.getIdByName(this, "R.id.tv_title"));
		_backView = findViewById(MResource.getIdByName(this, "R.id.iv_back"));

		// back
		_backView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		// title
		_titleLabel.setText(getString(MResource.getIdByName(this, "R.string.leaderboard")));

		// setup view pager
		_titles = new ArrayList<>();
		_viewPager.setOffscreenPageLimit(2);
		_viewPager.addOnPageChangeListener(this);

		// setup tabs
		_tabs.setOnTabSelectListener(this);

		// if model is null, need get model first, otherwise continue setup
		if(_model == null) {
			loadModel();
		} else {
			bindModel();
		}
	}

	private void loadModel() {
		// build request
		GameCenterRequestBean bean = new GameCenterRequestBean();
		bean.setDevice(null);
		bean.setTimestamp(0);
		bean.setDt(GameCenterRequestBean.DATA_TYPE_RANK);
		String params = JsonUtil.getMapParams(new Gson().toJson(bean));
		String url = SdkApi.getMinigameList() + "?" + params;

		// request
		HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<GameCenterResultBean>(this, null) {
			@Override
			public void onDataSuccess(GameCenterResultBean data) {
				if(null != data && data.getGameCenterData().size() > 0) {
					// save model
					_model = data.getGameCenterData().get(0);

					// continue setup ui
					bindModel();
				}
			}

			@Override
			public void onFailure(String code, String msg) {
				ToastUtil.s(AllRankingActivity.this, msg);
			}
		};
		httpCallbackDecode.setShowTs(false);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(true);
		httpCallbackDecode.setLoadMsg(getResources().getString(MResource.getIdByName(this, "R.string.loading")));
		(new RxVolley.Builder()).shouldCache(false).url(url).callback(httpCallbackDecode).doTask();
	}

	private void bindModel() {
		// setup tabs
		int size = Math.min(3, _model.getRankList().size());
		ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			// add title
			GameCenterData_Rank c = _model.getRankList().get(i);
			String title = c.getName();
			_titles.add(c.getName());

			// add tab
			tabEntities.add(new TabEntity(title, 0, 0));
		}

		// setup view pager
		_viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));

		// set tabs
		_tabs.setTabData(tabEntities);
		_tabs.setCurrentTab(0);
	}

	@Override
	public void onTabSelect(int position) {
		if(_viewPager.getCurrentItem() != position) {
			_viewPager.setCurrentItem(position);
		}
	}

	@Override
	public void onTabReselect(int position) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if(_tabs.getCurrentTab() != position) {
			_tabs.setCurrentTab(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private class TabPagerAdapter extends FragmentStatePagerAdapter {
		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return _titles.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return _titles.get(position);
		}

		@Override
		public Fragment getItem(int position) {
			GameCenterData_Rank rank = _model.getRankList().get(position);
			return SingleGameListFragment.getInstance(Constant.GAME_LIST_TAB_RANKING,
				_model.getId(), rank.getId(), (ArrayList<GameCenterData_Game>)rank.getGameList(),
				_orientation, _srcAppId, _srcAppPath, 15, 15);
		}
	}

	public static void start(Context context, GameCenterData model, String orientation, String srcAppId, String srcAppPath) {
		Intent intent = new Intent(context, AllRankingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(IntentConstant.SRC_APP_ID, srcAppId);
		intent.putExtra(IntentConstant.SRC_APP_PATH, srcAppPath);
		intent.putExtra(IntentConstant.ACTION_APP_ORIENTATION, orientation);
		if(model != null) {
			intent.putExtra(MODEL, model);
		}
		context.startActivity(intent);
	}
}
