package com.ledong.lib.minigame;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kymjs.rxvolley.RxVolley;
import com.ledong.lib.leto.Leto;
import com.ledong.lib.leto.LetoScene;
import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.leto.utils.JsonUtil;
import com.ledong.lib.minigame.bean.DataRefreshEvent;
import com.ledong.lib.minigame.view.dialog.CustomDialog;
import com.ledong.lib.minigame.view.holder.IHolderLongClickListener;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameCenterMoreRequestBean;
import com.leto.game.base.bean.GameModel;
import com.leto.game.base.http.HttpCallbackDecode;
import com.leto.game.base.http.SdkApi;
import com.leto.game.base.http.SdkConstant;
import com.leto.game.base.listener.IJumpListener;
import com.leto.game.base.listener.JumpError;
import com.leto.game.base.login.LoginManager;
import com.leto.game.base.util.ColorUtil;
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.GameUtil;
import com.leto.game.base.util.IntentConstant;
import com.leto.game.base.util.MResource;
import com.leto.game.base.util.ToastUtil;
import com.leto.game.base.view.recycleview.EndlessRecyclerViewScrollListener;
import com.leto.game.base.view.recycleview.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示一个游戏列表, 列表内的游戏按照类型使用对应的布局形式, 这是一个通用的单列表组件, 可以嵌入到其它界面复用
 */
public class SingleGameListFragment extends Fragment implements IGameSwitchListener, IHolderLongClickListener {
	public final static String TYPE = "type";
	public final static String MODEL = "model";
	public final static String SEP_MARGIN_LEFT = "sep_margin_left";
	public final static String SEP_MARGIN_RIGHT = "sep_margin_right";
	public final static String TID = "tid"; // 模块 ID
	public final static String LID = "lid"; // 列表 ID，如果 是分类则是分 类 ID，如果是 排行榜则是排 行榜 ID，如果 是查看全部列 表则是 0

	// views
	RecyclerView _recyclerView;
	SwipeRefreshLayout _refreshLayout;
	View _emptyView;
	View _rootView;

	// model and adapter
	private int _listType = Constant.GAME_LIST_RECENT_PLAYED; //1玩过的游戏 2收藏的游戏
	private SingleGameListAdapter _adapter;
	private int _tid = -1;
	private int _lid = -1;
	private int _page = 0;
	private int _limit = 10;
	private boolean _hasMore = true;
	private boolean _loading = false;
	private List<GameCenterData_Game> _gameList = new ArrayList<>();

	// source app info
	private String _orientation;
	private String _srcAppId;
	private String _srcAppPath;

	/**
	 * create a single game list fragment
	 *
	 * @param listType 列表类型
	 * @param tid type id
	 * @param lid list id
	 * @param initModel 初始游戏模型列表
	 * @param orientation 原始朝向(这个有用吗?)
	 * @param appId 原始app id
	 * @param appPath 原始app路径
	 * @param separatorMarginLeft 列表分隔条左边空间
	 * @param separatorMarginRight 列表分隔条右边空间
	 */
	public static Fragment getInstance(int listType, int tid, int lid, ArrayList<GameCenterData_Game> initModel, String orientation, String appId, String appPath, int separatorMarginLeft, int separatorMarginRight) {
		SingleGameListFragment fragment = new SingleGameListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(TYPE, listType);
		bundle.putInt(TID, tid);
		bundle.putInt(LID, lid);
		if(initModel != null) {
			bundle.putSerializable(MODEL, initModel);
		}
		bundle.putString(IntentConstant.SRC_APP_ID, appId);
		bundle.putString(IntentConstant.SRC_APP_PATH, appPath);
		bundle.putString(IntentConstant.ACTION_APP_ORIENTATION, orientation);
		bundle.putInt(SEP_MARGIN_LEFT, separatorMarginLeft);
		bundle.putInt(SEP_MARGIN_RIGHT, separatorMarginRight);
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * create a single game list fragment
	 *
	 * @param type 列表类型
	 * @param model 游戏模型列表
	 * @param orientation 原始朝向(这个有用吗?)
	 * @param appId 原始app id
	 * @param appPath 原始app路径
	 * @param separatorMarginLeft 列表分隔条左边空间
	 * @param separatorMarginRight 列表分隔条右边空间
	 */
	public static Fragment getInstance(int type, ArrayList<GameCenterData_Game> model, String orientation, String appId, String appPath, int separatorMarginLeft, int separatorMarginRight) {
		SingleGameListFragment fragment = new SingleGameListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(TYPE, type);
		if(model != null) {
			bundle.putSerializable(MODEL, model);
		}
		bundle.putString(IntentConstant.SRC_APP_ID, appId);
		bundle.putString(IntentConstant.SRC_APP_PATH, appPath);
		bundle.putString(IntentConstant.ACTION_APP_ORIENTATION, orientation);
		bundle.putInt(SEP_MARGIN_LEFT, separatorMarginLeft);
		bundle.putInt(SEP_MARGIN_RIGHT, separatorMarginRight);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		_rootView = inflater.inflate(MResource.getIdByName(getActivity(), "R.layout.leto_fragment_single_game_list"), container, false);

		// init ui
		setupUI();

		// register for event bus
		if(!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}

		return _rootView;
	}

	public void onDestroy() {
		super.onDestroy();

		if(EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	public List<GameCenterData_Game> getGameList() {
		return _gameList;
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onDataRefresh(DataRefreshEvent event) {
		onLoadData();
	}

	private void setupUI() {
		// get arguments
		int sepMarginLeft = 0;
		int sepMarginRight = 0;
		Bundle arguments = getArguments();
		if(arguments != null) {
			_listType = arguments.getInt(TYPE, Constant.GAME_LIST_RECENT_PLAYED);
		 	Serializable data = arguments.getSerializable(MODEL);
			if(data != null) {
				_gameList.clear();
				_gameList.addAll((List<GameCenterData_Game>)data);
			}
			_tid = arguments.getInt(TID, -1);
			_lid = arguments.getInt(LID, -1);
			_srcAppId = arguments.getString(IntentConstant.SRC_APP_ID);
			_srcAppPath = arguments.getString(IntentConstant.SRC_APP_PATH);
			_orientation = arguments.getString(IntentConstant.ACTION_APP_ORIENTATION);
			sepMarginLeft = DensityUtil.dip2px(_rootView.getContext(), arguments.getInt(SEP_MARGIN_LEFT, 0));
			sepMarginRight = DensityUtil.dip2px(_rootView.getContext(), arguments.getInt(SEP_MARGIN_RIGHT, 0));
		}

		// find views
		_recyclerView = _rootView.findViewById(MResource.getIdByName(getActivity(), "R.id.recyclerView"));
		_refreshLayout = _rootView.findViewById(MResource.getIdByName(getActivity(), "R.id.refreshLayout"));
		_emptyView = _rootView.findViewById(MResource.getIdByName(getActivity(), "R.id.emptyView"));

		// setup recycler
		_adapter = new SingleGameListAdapter(getActivity(), _gameList, _listType, this);
		_adapter.setHolderLongClickListener(this);
		_recyclerView.setAdapter(_adapter);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		_recyclerView.setLayoutManager(layoutManager);
		if(_listType != Constant.GAME_LIST_RECOMMENDED) {
			_recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(_rootView.getContext())
				.margin(sepMarginLeft, sepMarginRight)
				.color(ColorUtil.parseColor("#dddddd"))
				.build());
		}
		_recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				if(_tid >= 0) {
					loadPage();
				}
			}
		});

		// XXX: refresh layout is not enabled yet
		_refreshLayout.setEnabled(false);

		// load data if needed
		onLoadData();
	}

	private void loadPage() {
		// check more flag
		if(!_hasMore || _loading) {
			return;
		}

		// set flag
		_loading = true;

		// increase page
		_page++;

		// build request
		String str = SdkConstant.userToken;
		GameCenterMoreRequestBean bean = new GameCenterMoreRequestBean();
		bean.setApp_id(_srcAppId);
		bean.setUser_token(str);
		bean.setTid(_tid);
		bean.setLid(_lid);
		bean.setPage(_page);
		bean.setOffset(_limit);
		bean.setDevice(null);
		bean.setTimestamp(0);
		String params =  JsonUtil.getMapParams(new Gson().toJson(bean));
		String url = SdkApi.getMinigameMore() + "?" + params;

		// request
		HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<List<GameCenterData_Game>>(getContext(), null, new TypeToken<List<GameCenterData_Game>>(){}.getType()) {
			@Override
			public void onDataSuccess(List<GameCenterData_Game> model) {
				if (null != model) {
					if(_page == 1) {
						_gameList.clear();
					}
					_gameList.addAll(model);
					_hasMore = model.size() >= _limit;
					getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								updateListView();
							}
						});
				} else {
					_hasMore = false;
				}
			}

			@Override
			public void onFailure(String code, String msg) {
				ToastUtil.s(getContext(), msg);
			}

			@Override
			public void onFinish() {
				super.onFinish();

				// clear flag
				_loading = false;
			}
		};
		httpCallbackDecode.setShowTs(true);
		httpCallbackDecode.setLoadingCancel(false);
		httpCallbackDecode.setShowLoading(true);
		httpCallbackDecode.setLoadMsg(getResources().getString(MResource.getIdByName(getContext(), "R.string.loading")));
		(new RxVolley.Builder()).shouldCache(false).url(url).callback(httpCallbackDecode).doTask();
	}

	private void onLoadData() {
		// if game type model is set, get whole list from server
		// if null, check list type is special or not
		if(_tid >= 0) {
			loadPage();
		} else {
			int listType = 0;
			switch(_listType) {
				case Constant.GAME_LIST_RECENT_PLAYED:
					listType = GameUtil.USER_GAME_TYPE_PLAY;
					break;
				case Constant.GAME_LIST_FAVORITE:
					listType = GameUtil.USER_GAME_TYPE_FAVORITE;
					break;
			}
			if(listType > 0) {
				new AsyncTask<Integer, Void, Void>() {
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
					}

					@Override
					protected Void doInBackground(Integer... params) {
						try {
							String userId = LoginManager.getUserId(getContext());
							_gameList.clear();
							List<GameCenterData_Game> localList = GameModel.toNewList(GameUtil.loadGameList(getContext(), userId, params[0]));
							if(localList != null) {
								_gameList.addAll(localList);
							}

							new Handler(Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() {
									updateListView();
								}
							});

						} catch(Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void voids) {
					}
				}.execute(listType);
			} else {
				updateListView();
			}
		}
	}

	private void updateListView() {
		_emptyView.setVisibility(_gameList.isEmpty() ? View.VISIBLE : View.GONE);
		_recyclerView.getAdapter().notifyDataSetChanged();
	}

	@Override
	public void onJump(String appId, String appPath, int isCollect, String url, String packageName, String gameName, int isCps, String splashUrl, int is_kp_ad, int is_more, String icon, String share_url, String share_msg, String package_url, int game_type, String orientation) {
		LetoScene scene = _listType == Constant.GAME_LIST_RECENT_PLAYED ? LetoScene.PLAYED_LIST : LetoScene.FAVORITE;

		GameModel gameModel = new GameModel();
		gameModel.setId(Integer.parseInt(appId));
		gameModel.setIs_collect(isCollect);
		gameModel.setApkpackagename(packageName);
		gameModel.setPackageurl(url);
		gameModel.setName(gameName);
		gameModel.setIs_cps(isCps);
		gameModel.setSplash_pic(splashUrl);
		gameModel.setIs_kp_ad(is_kp_ad);
		gameModel.setIs_more(is_more);
		gameModel.setIcon(icon);
		gameModel.setShare_msg(share_msg);
		gameModel.setShare_url(share_url);
		gameModel.setClassify(game_type);
		gameModel.setDeviceOrientation(orientation);

		Leto.getInstance().jumpGameWithGameInfo(getActivity(), _srcAppId, appId, gameModel, scene, new IJumpListener() {
			@Override
			public void onDownloaded(String path) {
			}

			@Override
			public void onLaunched() {
			}

			@Override
			public void onError(JumpError code, String message) {
			}
		});
	}

	@Override
	public void onJump(GameCenterData_Game gameBean) {
		Leto.getInstance().jumpMiniGameWithAppId(getActivity(), _srcAppId, String.valueOf(gameBean.getId()), LetoScene.GAMECENTER);
	}

	@Override
	public void onItemLongClick(final int position) {
		if(_listType == Constant.GAME_LIST_FAVORITE) {
			new CustomDialog().showUnFavoriteDialog(getActivity(), new CustomDialog.ConfirmDialogListener() {
				@Override
				public void onConfirm() {
					if(null != _gameList && _gameList.size() > 0 && position < _gameList.size()) {
						_gameList.remove(position);
						_adapter.notifyDataSetChanged();

						String userId = LoginManager.getUserId(getContext());
						GameUtil.saveGameList(getContext(), userId, _listType, new Gson().toJson(_gameList));
					}
				}

				@Override
				public void onCancel() {
				}
			});
		}
	}
}
