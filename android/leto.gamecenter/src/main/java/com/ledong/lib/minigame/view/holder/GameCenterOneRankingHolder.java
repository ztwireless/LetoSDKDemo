package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.FavoriteActivity;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.SingleGameListActivity;
import com.ledong.lib.minigame.SingleGameListAdapter;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameCenterData_Rank;
import com.leto.game.base.util.ColorUtil;
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.recycleview.HorizontalDividerItemDecoration;
import com.leto.game.base.view.tablayout.CommonTabLayout;
import com.leto.game.base.view.tablayout.entity.TabEntity;
import com.leto.game.base.view.tablayout.listener.CustomTabEntity;

import java.util.ArrayList;
import java.util.List;

public class GameCenterOneRankingHolder extends CommonViewHolder<GameCenterData> {
	// views
	private CommonTabLayout _tabs;
	private RecyclerView _listView;
	private TextView _moreLabel;

	// title of tabs
	private List<String> _titles;

	// model
	private GameCenterData _model;
	private List<GameCenterData_Game> _gameList = new ArrayList<>();

	public static GameCenterOneRankingHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_one_ranking"), parent, false);
		return new GameCenterOneRankingHolder(convertView, switchListener);
	}

	public GameCenterOneRankingHolder(View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// init
		_titles = new ArrayList<>();

		// views
		Context ctx = itemView.getContext();
		_tabs = itemView.findViewById(MResource.getIdByName(ctx, "R.id.tabs"));
		_listView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.list"));
		_moreLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.more"));

		// setup list view
		int sepMargin = DensityUtil.dip2px(ctx, 15);
		_listView.setLayoutManager(new LinearLayoutManager(ctx));
		_listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ctx)
			.margin(sepMargin, sepMargin)
			.color(ColorUtil.parseColor("#dddddd"))
			.build());
		_listView.setAdapter(new SingleGameListAdapter(ctx, _gameList, Constant.GAME_LIST_ONE_RANKING, switchListener));
	}

	@Override
	public void onBind(GameCenterData model, int position) {
		// save
		_model = model;

		// setup tabs
		ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();

		// add title
		final GameCenterData_Rank rank = _model.getRankList().get(0);
		String title = rank.getName();
		_titles.add(rank.getName());

		// add tab
		tabEntities.add(new TabEntity(title, 0, 0));

		// set tabs
		_tabs.setTabData(tabEntities);
		_tabs.setCurrentTab(0);

		// more
		_moreLabel.setVisibility(model.isShowMore() ? View.VISIBLE : View.INVISIBLE);
		_moreLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (_model.getName().equalsIgnoreCase(view.getContext().getResources().getString(MResource.getIdByName(view.getContext(), "R.string.leto_recently_played")))) {
					FavoriteActivity.start(view.getContext(), "", "", 1);
				} else {
					SingleGameListActivity.start(view.getContext(), _model, rank.getId(),
						Constant.GAME_LIST_SINGLE, rank.getName(), _orientation, _srcAppId, _srcAppPath);
				}
			}
		});

		// reload
		_gameList.clear();
		if(null!=_model.getRankList() && model.getRankList().size()>0) {
			_gameList.addAll(_model.getRankList().get(0).getGameList());
		}
		_listView.getAdapter().notifyDataSetChanged();
	}
}
