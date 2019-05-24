package com.ledong.lib.minigame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kymjs.rxvolley.RxVolley;
import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.leto.utils.JsonUtil;
import com.ledong.lib.minigame.view.CustomViewPager;
import com.ledong.lib.minigame.view.holder.CategoryTabHolder;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Category;
import com.leto.game.base.bean.GameCenterRequestBean;
import com.leto.game.base.bean.GameCenterResultBean;
import com.leto.game.base.http.HttpCallbackDecode;
import com.leto.game.base.http.SdkApi;
import com.leto.game.base.util.IntentConstant;
import com.leto.game.base.util.MResource;
import com.leto.game.base.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllCategoryActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
	private static final String MODEL = "model";
	private static final String INIT_CAT_INDEX = "init_cat_index";

	// views
	private RecyclerView _categoryListView;
	private CustomViewPager _viewPager;
	private TextView _titleLabel;
	private ImageView _backView;

	// title of tabs
	private List<String> _titles;

	// model
	private GameCenterData _model;
	private int _activeCatIndex = 0;

	// extras
	private String _orientation = "portrait";
	private String _srcAppId;
	private String _srcAppPath;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content
		setContentView(MResource.getIdByName(this, "R.layout.leto_all_category_activity"));

		// get extras
		Bundle bundle = getIntent().getExtras();
		_orientation = bundle.getString(IntentConstant.ACTION_APP_ORIENTATION, "portrait");
		_srcAppId = bundle.getString(IntentConstant.SRC_APP_ID);
		_srcAppPath = bundle.getString(IntentConstant.SRC_APP_PATH);
		_activeCatIndex = bundle.getInt(INIT_CAT_INDEX, 0);
		Serializable model = bundle.getSerializable(MODEL);
		if(model != null) {
			_model = (GameCenterData)model;
		}

		// views
		_categoryListView = findViewById(MResource.getIdByName(this, "R.id.category_list"));
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
		_titleLabel.setText(getString(MResource.getIdByName(this, "R.string.category")));

		// setup view pager
		_titles = new ArrayList<>();
		_viewPager.setOffscreenPageLimit(2);
		_viewPager.addOnPageChangeListener(this);
		_viewPager.disableSwipe(true);

		// set up category list
		_categoryListView.setLayoutManager(new LinearLayoutManager(this));

		// if model is null, load model
		// if not, continue setup
		if(_model == null) {
			loadModel();
		} else {
			bindModel();
		}
	}

	private void bindModel() {
		// build title array
		for(GameCenterData_Category c : _model.getCategoryList()) {
			_titles.add(c.getName());
		}

		// setup category list
		_categoryListView.setAdapter(new CategoryAdapter());

		// setup pager
		_viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
	}

	private void loadModel() {
		// build request
		GameCenterRequestBean bean = new GameCenterRequestBean();
		bean.setDevice(null);
		bean.setTimestamp(0);
		bean.setDt(GameCenterRequestBean.DATA_TYPE_CATEGORY);
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
				ToastUtil.s(AllCategoryActivity.this, msg);
			}
		};
		httpCallbackDecode.setShowTs(false);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(true);
		httpCallbackDecode.setLoadMsg(getResources().getString(MResource.getIdByName(this, "R.string.loading")));
		(new RxVolley.Builder()).shouldCache(false).url(url).callback(httpCallbackDecode).doTask();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if(_activeCatIndex != position) {
			_activeCatIndex = position;
			_categoryListView.getAdapter().notifyDataSetChanged();
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	public static void start(Context context, GameCenterData model, int initCatIndex, String orientation, String srcAppId, String srcAppPath) {
		Intent intent = new Intent(context, AllCategoryActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(INIT_CAT_INDEX, initCatIndex);
		intent.putExtra(IntentConstant.SRC_APP_ID, srcAppId);
		intent.putExtra(IntentConstant.SRC_APP_PATH, srcAppPath);
		intent.putExtra(IntentConstant.ACTION_APP_ORIENTATION, orientation);
		if(model != null) {
			intent.putExtra(MODEL, model);
		}
		context.startActivity(intent);
	}

	private class CategoryAdapter extends RecyclerView.Adapter<CategoryTabHolder> {
		@NonNull
		@Override
		public CategoryTabHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return CategoryTabHolder.create(AllCategoryActivity.this, parent);
		}

		@Override
		public void onBindViewHolder(@NonNull final CategoryTabHolder holder, final int position) {
			holder.onBind(_model.getCategoryList().get(position), position);
			holder.setActive(_activeCatIndex == position);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(_viewPager.getCurrentItem() != position) {
						_activeCatIndex = position;
						_categoryListView.getAdapter().notifyDataSetChanged();
						_viewPager.setCurrentItem(position, false);
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return _titles.size();
		}
	}

	private class PagerAdapter extends FragmentStatePagerAdapter {
		public PagerAdapter(FragmentManager fm) {
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
			return SingleGameListFragment.getInstance(Constant.GAME_LIST_SINGLE,
				_model.getId(), _model.getCategoryList().get(position).getId(), null,
				_orientation, _srcAppId, _srcAppPath, 15, 0);
		}
	}
}
