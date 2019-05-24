package com.ledong.lib.minigame;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kymjs.rxvolley.RxVolley;
import com.ledong.lib.leto.Leto;
import com.ledong.lib.leto.LetoScene;
import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.leto.utils.JsonUtil;
import com.ledong.lib.leto.utils.StringUtil;
import com.ledong.lib.minigame.bean.DataRefreshEvent;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameCenterRequestBean;
import com.leto.game.base.bean.GameCenterResultBean;
import com.leto.game.base.bean.GameCenterVersionRequestBean;
import com.leto.game.base.bean.GameCenterVersionResultBean;
import com.leto.game.base.bean.GameModel;
import com.leto.game.base.http.HttpCallbackDecode;
import com.leto.game.base.http.SdkApi;
import com.leto.game.base.listener.IJumpListener;
import com.leto.game.base.listener.JumpError;
import com.leto.game.base.login.LoginManager;
import com.leto.game.base.util.BaseAppUtil;
import com.leto.game.base.util.GameUtil;
import com.leto.game.base.util.MResource;
import com.leto.game.base.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class GameCenterHomeFragment extends Fragment implements IGameSwitchListener {
	private static final String TAG = "GameCenter";

	// views
	private RecyclerView _listView;
	private SwipeRefreshLayout _refreshLayout;
	private View _rootView;

	// adapter
	private GameCenterHomeAdapter _adapter;

	// models
	private GameCenterResultBean _model;

	// extras
	private String _orientation = "portrait";
	private String _srcAppId;
	private String _srcAppPath;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// load layout
		_rootView = inflater.inflate(MResource.getIdByName(getActivity(), "R.layout.leto_gamecenter_home_fragment"), container, false);

		// find views
		_refreshLayout = _rootView.findViewById(MResource.getIdByName(getContext(), "R.id.refreshLayout"));
		_listView = _rootView.findViewById(MResource.getIdByName(getContext(), "R.id.list"));

		// setup views
		_listView.setLayoutManager(new LinearLayoutManager(getContext()));
		_refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadRemoteGames(true);
			}
		});

		// register for event bus
		if(!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}

		// return root
		return _rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// remove event bus listener
		if(EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// get extras
		_orientation = getActivity().getIntent().getStringExtra("app_orientation");
		_srcAppId = getActivity().getIntent().getStringExtra("src_app_id");
		_srcAppPath = getActivity().getIntent().getStringExtra("src_app_path");

		//读取本地游戏列表作为后备
		loadLocalGames();
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onDataRefresh(DataRefreshEvent event) {
		loadRemoteGames(false);
	}

	private void loadLocalGames() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					_model = GameUtil.loadGameList(getContext());
					if(_model == null) {
						loadRemoteGames(true);
					} else {
						loadRecentPlayedGames();
						updateListView();
						checkUpdate();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void voids) {
			}
		}.execute();
	}

	/**
	 * 检查游戏中心数据是否有更新
	 */
	private void checkUpdate() {
		// build request
		GameCenterVersionRequestBean bean = new GameCenterVersionRequestBean();
		String appId = BaseAppUtil.getChannelID(getContext());
		bean.setApp_id(Integer.parseInt(appId));
		String params = JsonUtil.getMapParams(new Gson().toJson(bean));
		String url = SdkApi.getGameCenterVersion() + "?" + params;

		// request
		HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<GameCenterVersionResultBean>(getContext(), null) {
			@Override
			public void onDataSuccess(GameCenterVersionResultBean data) {
				if(null != data) {
					// compare version, if has update, load game list from server
					String v1 = data.getVersion();
					String v2 = (_model == null || _model.getGameCenterVersion() == null) ? "0" : _model.getGameCenterVersion();
					if(StringUtil.compareVersion(v1, v2) > 0) {
						loadRemoteGames(true);
					}
				}
			}

			@Override
			public void onFailure(String code, String msg) {
				ToastUtil.s(getContext(), msg);
			}
		};
		httpCallbackDecode.setShowTs(false);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(false);
		httpCallbackDecode.setLoadMsg(getResources().getString(MResource.getIdByName(getContext(), "R.string.loading")));
		(new RxVolley.Builder()).shouldCache(false).url(url).callback(httpCallbackDecode).doTask();
	}

	private void loadRemoteGames(final boolean showRefresh) {
		// show refresh loading indicator
		if(showRefresh) {
			_refreshLayout.setRefreshing(true);
		}

		// build request
		GameCenterRequestBean bean = new GameCenterRequestBean();
		bean.setApp_id(BaseAppUtil.getChannelID(getContext()));
		bean.setDevice(null);
		bean.setTimestamp(0);
		bean.setDt(GameCenterRequestBean.DATA_TYPE_ALL);
		String params = JsonUtil.getMapParams(new Gson().toJson(bean));
		String url = SdkApi.getMinigameList() + "?" + params;

		// request
		HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<GameCenterResultBean>(getContext(), null) {
			@Override
			public void onDataSuccess(GameCenterResultBean data) {
				if(null != data) {
					// save model
					_model = data;

					// 确保一下banner位于第一的位置
					int size = data.getGameCenterData().size();
					int bannerIndex = -1;
					for(int i = 0; i < size; i++) {
						if(data.getGameCenterData().get(i).getCompact() == Constant.GAME_LIST_ROTATION_CHART) {
							bannerIndex = i;
							break;
						}
					}
					if(bannerIndex > 0) {
						GameCenterData banner = data.getGameCenterData().remove(bannerIndex);
						data.getGameCenterData().add(0, banner);
					}

					// 确保按钮条位于第二的位置(如果有banner), 或者第一(如果没banner)
					int buttonIndex = -1;
					for(int i = 0; i < size; i++) {
						if(data.getGameCenterData().get(i).getCompact() == Constant.GAME_LIST_BUTTON) {
							buttonIndex = i;
							break;
						}
					}
					if(bannerIndex >= 0) {
						if(buttonIndex > 1) {
							GameCenterData button = data.getGameCenterData().remove(buttonIndex);
							data.getGameCenterData().add(1, button);
						}
					} else if(buttonIndex > 0) {
						GameCenterData button = data.getGameCenterData().remove(buttonIndex);
						data.getGameCenterData().add(0, button);
					}

					// save model to local as json file so that it can be used as fallback next time
					GameUtil.saveJson(getContext(), new Gson().toJson(data), GameUtil.MORE_GAME_LIST);

					// load recent played games
					loadRecentPlayedGames();

					// update list
					updateListView();
				}
			}

			@Override
			public void onFailure(String code, String msg) {
				ToastUtil.s(getContext(), msg);
			}

			@Override
			public void onFinish() {
				if(showRefresh) {
					_refreshLayout.setRefreshing(false);
				}
			}
		};
		httpCallbackDecode.setShowTs(false);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(false);
		httpCallbackDecode.setLoadMsg(getResources().getString(MResource.getIdByName(getContext(), "R.string.loading")));
		(new RxVolley.Builder()).shouldCache(false).url(url).callback(httpCallbackDecode).doTask();
	}

	private void loadRecentPlayedGames() {
		// don't load if model is null
		if(_model == null) {
			return;
		}

		// load game list
		String userId = LoginManager.getUserId(getContext());
		List<GameCenterData_Game> gameList = GameModel.toNewList(GameUtil.loadGameList(getContext(), userId, GameUtil.USER_GAME_TYPE_PLAY));

		// if has
		if(null != gameList && gameList.size() > 0) {
			// create a game list for recent played game
			GameCenterData recentPlayedData = new GameCenterData();
			recentPlayedData.setName(getResources().getString(MResource.getIdByName(getContext(), "R.string.leto_recently_played")));
			recentPlayedData.setGameList(gameList);
			recentPlayedData.setCompact(Constant.GAME_LIST_SIMPLE_GRID);
			recentPlayedData.setId(-1); // 设置为-1则不会从服务器去取完整列表, 最近玩过的游戏是存在本地的不需要去服务器

			// if no data, init data list
			if(null == _model.getGameCenterData()) {
				List<GameCenterData> gameModel = new ArrayList<>();
				_model.setGameCenterData(gameModel);
			}

			// 插入最近玩的游戏, 不能在banner和按钮条的上面
			int insertionIndex = 0;
			if(_model.getGameCenterData() != null && !_model.getGameCenterData().isEmpty()) {
				while(_model.getGameCenterData().get(insertionIndex).getCompact() == Constant.GAME_LIST_ROTATION_CHART ||
					_model.getGameCenterData().get(insertionIndex).getCompact() == Constant.GAME_LIST_BUTTON) {
					insertionIndex++;
				}
			}
			_model.getGameCenterData().add(insertionIndex, recentPlayedData);
		}
	}

	private void updateListView() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if(null == _model || null == _model.getGameCenterData()) {
					return;
				}

				_adapter = new GameCenterHomeAdapter(getContext(), _model, GameCenterHomeFragment.this);
				_adapter.setSourceGame(_orientation, _srcAppId, _srcAppPath);
				_listView.setAdapter(_adapter);
			}
		});
	}

	@Override
	public void onJump(String appId, String appPath, int isCollect, String url, String packageName, String gameName, int isCps, String splashPic, int is_kp_ad, int is_more, String icon, String share_url, String share_msg, String package_url, int game_type, String orientation) {
		GameModel gameModel = new GameModel();
		gameModel.setId(Integer.parseInt(appId));
		gameModel.setIs_collect(isCollect);
		gameModel.setPackageurl(appPath);
		gameModel.setApkpackagename(packageName);
		gameModel.setApkurl(url);
		gameModel.setName(gameName);
		gameModel.setIs_cps(isCps);
		gameModel.setSplash_pic(splashPic);
		gameModel.setIs_kp_ad(is_kp_ad);
		gameModel.setIs_more(is_more);
		gameModel.setIcon(icon);
		gameModel.setShare_msg(share_msg);
		gameModel.setShare_url(share_url);
		gameModel.setClassify(game_type);
		gameModel.setDeviceOrientation(orientation);

		Leto.getInstance().jumpGameWithGameInfo(getContext(), _srcAppId, appId, gameModel, LetoScene.BANNER, new IJumpListener() {
			@Override
			public void onDownloaded(String path) {
				Log.d(TAG, "download complete");
			}

			@Override
			public void onLaunched() {
				Log.d(TAG, "start complete");
			}

			@Override
			public void onError(JumpError code, String msg) {
				ToastUtil.s(getContext(), msg);
			}
		});
	}

	@Override
	public void onJump(GameCenterData_Game gameBean) {
		Leto.getInstance().jumpMiniGameWithAppId(getContext(), _srcAppId, String.valueOf(gameBean.getId()), LetoScene.GAMECENTER);
	}
}
