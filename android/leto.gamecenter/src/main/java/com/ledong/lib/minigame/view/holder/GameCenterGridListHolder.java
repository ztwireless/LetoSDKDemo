package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.SingleGameListActivity;
import com.ledong.lib.minigame.SingleGameListAdapter;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.recycleview.ScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameCenterGridListHolder extends CommonViewHolder<GameCenterData> {
	// views
	private TextView _titleLabel;
	private ScrollRecyclerView _recyclerView;
	private TextView _moreLabel;

	// models
	private GameCenterData _model;
	private List<GameCenterData_Game> _gameList;

	public static GameCenterGridListHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_triple_row_list"), parent, false);
		return new GameCenterGridListHolder(convertView, switchListener);
	}

	public GameCenterGridListHolder(View view, IGameSwitchListener switchListener) {
		super(view, switchListener);

		// get views
		Context ctx = view.getContext();
		_titleLabel = view.findViewById(MResource.getIdByName(ctx, "R.id.title"));
		_recyclerView = view.findViewById(MResource.getIdByName(ctx, "R.id.recyclerView"));
		_moreLabel = view.findViewById(MResource.getIdByName(ctx, "R.id.more_textview"));

		// setup views
		_gameList = new ArrayList<>();
		_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
			StaggeredGridLayoutManager.HORIZONTAL));
	}

	@Override
	public void onBind(final GameCenterData model, int position) {
		// update model
		_model = model;
		_gameList.clear();
		_gameList.addAll(model.getGameList());

		// reload list
		_recyclerView.setAdapter(new SingleGameListAdapter(itemView.getContext(), _gameList, _model.getCompact(), _switchListener));

		// update ui
		_titleLabel.setText(model.getName());
		_moreLabel.setVisibility(model.isShowMore() ? View.VISIBLE : View.INVISIBLE);
		_moreLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SingleGameListActivity.start(itemView.getContext(), model, 0, Constant.GAME_LIST_SINGLE,
					model.getName(), _orientation, _srcAppId, _srcAppPath);
			}
		});
	}
}