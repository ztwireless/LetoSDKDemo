package com.ledong.lib.minigame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kymjs.rxvolley.RxVolley;
import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.leto.utils.JsonUtil;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameCenterRequestBean;
import com.leto.game.base.bean.GameCenterResultBean;
import com.leto.game.base.http.HttpCallbackDecode;
import com.leto.game.base.http.SdkApi;
import com.leto.game.base.util.IntentConstant;
import com.leto.game.base.util.MResource;
import com.leto.game.base.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class SingleGameListActivity extends FragmentActivity {
	private static final String TYPE_ID = "tid";
	private static final String LIST_ID = "lid";
	private static final String TITLE = "title";
	private static final String MODEL = "model";
	private static final String DATA_TYPE = "dt";
	private static final String LIST_TYPE = "lt";

	// views
	private TextView _titleLabel;
	private ImageView _backView;

	// model
	private GameCenterData _model;
	private int _typeId = -1;
	private int _listId = -1;
	private int _listType = 0;
	private int _dataType = -1;

	// extras
	private String _orientation = "portrait";
	private String _srcAppId;
	private String _srcAppPath;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content
		setContentView(MResource.getIdByName(this, "R.layout.leto_recommend_activity"));

		// get extras
		Bundle bundle = getIntent().getExtras();
		_orientation = bundle.getString(IntentConstant.ACTION_APP_ORIENTATION, "portrait");
		_srcAppId = bundle.getString(IntentConstant.SRC_APP_ID);
		_srcAppPath = bundle.getString(IntentConstant.SRC_APP_PATH);
		_typeId = bundle.getInt(TYPE_ID, -1);
		_listId = bundle.getInt(LIST_ID, -1);
		_dataType = bundle.getInt(DATA_TYPE, -1);
		_listType = bundle.getInt(LIST_TYPE, Constant.GAME_LIST_SINGLE);
		Serializable model = bundle.getSerializable(MODEL);
		if(model != null) {
			_model = (GameCenterData)model;
			_typeId = _model.getId();
		}
		String title =  bundle.getString(TITLE);

		// views
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
		_titleLabel.setText(title);

		// depend on extra, load model, or not
		if(_dataType != -1) {
			loadModel();
		} else {
			bindModel();
		}
	}

	private void bindModel() {
		Fragment f = null;
		if(_typeId == -1) {
			f = SingleGameListFragment.getInstance(_listType,
				_model == null ? null : (ArrayList<GameCenterData_Game>)_model.getGameList(),
				_orientation, _srcAppId, _srcAppPath, 0, 0);
 		} else {
			f = SingleGameListFragment.getInstance(_listType,
				_typeId, _listId, _model == null ? null : (ArrayList<GameCenterData_Game>)_model.getGameList(),
				_orientation, _srcAppId, _srcAppPath, 0, 0);
		}
		getSupportFragmentManager()
			.beginTransaction()
			.add(MResource.getIdByName(this, "R.id.content"), f)
			.commit();
	}

	private void loadModel() {
		// build request
		GameCenterRequestBean bean = new GameCenterRequestBean();
		bean.setDevice(null);
		bean.setTimestamp(0);
		bean.setDt(_dataType);
		String params = JsonUtil.getMapParams(new Gson().toJson(bean));
		String url = SdkApi.getMinigameList() + "?" + params;

		// request
		HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<GameCenterResultBean>(this, null) {
			@Override
			public void onDataSuccess(GameCenterResultBean data) {
				if(null != data && data.getGameCenterData().size() > 0) {
					// save model
					_model = data.getGameCenterData().get(0);
					_typeId = _model.getId();
					_listId = 0;

					// continue setup ui
					bindModel();
				}
			}

			@Override
			public void onFailure(String code, String msg) {
				ToastUtil.s(SingleGameListActivity.this, msg);
			}
		};
		httpCallbackDecode.setShowTs(false);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(true);
		httpCallbackDecode.setLoadMsg(getResources().getString(MResource.getIdByName(this, "R.string.loading")));
		(new RxVolley.Builder()).shouldCache(false).url(url).callback(httpCallbackDecode).doTask();
	}

	/**
	 * 不需要载入game center data, 直接传递type id和list id给fragment
	 */
	public static void start(Context context, int typeId, int listId, int listType, String title, String orientation, String srcAppId, String srcAppPath) {
		Intent intent = new Intent(context, SingleGameListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(IntentConstant.SRC_APP_ID, srcAppId);
		intent.putExtra(IntentConstant.SRC_APP_PATH, srcAppPath);
		intent.putExtra(IntentConstant.ACTION_APP_ORIENTATION, orientation);
		intent.putExtra(TYPE_ID, typeId);
		intent.putExtra(LIST_ID, listId);
		intent.putExtra(LIST_TYPE, listType);
		intent.putExtra(TITLE, title);
		context.startActivity(intent);
	}

	/**
	 * 使用model的id作为type id
	 */
	public static void start(Context context, GameCenterData model, int listId, int listType, String title, String orientation, String srcAppId, String srcAppPath) {
		Intent intent = new Intent(context, SingleGameListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(IntentConstant.SRC_APP_ID, srcAppId);
		intent.putExtra(IntentConstant.SRC_APP_PATH, srcAppPath);
		intent.putExtra(IntentConstant.ACTION_APP_ORIENTATION, orientation);
		intent.putExtra(MODEL, model);
		intent.putExtra(LIST_ID, listId);
		intent.putExtra(LIST_TYPE, listType);
		intent.putExtra(TITLE, title);
		context.startActivity(intent);
	}

	/**
	 * 需要先根据dataType请求game center数据获得type id, 这种情况下list id是0
	 */
	public static void start(Context context, int dataType, int listType, String title, String orientation, String srcAppId, String srcAppPath) {
		Intent intent = new Intent(context, SingleGameListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(IntentConstant.SRC_APP_ID, srcAppId);
		intent.putExtra(IntentConstant.SRC_APP_PATH, srcAppPath);
		intent.putExtra(IntentConstant.ACTION_APP_ORIENTATION, orientation);
		intent.putExtra(DATA_TYPE, dataType);
		intent.putExtra(LIST_TYPE, listType);
		intent.putExtra(TITLE, title);
		context.startActivity(intent);
	}
}
