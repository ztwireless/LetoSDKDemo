package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.SingleGameListAdapter;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.recycleview.ScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameCenterGalleryHolder extends CommonViewHolder<GameCenterData> {
	// views
	private TextView _titleLabel;
	private ScrollRecyclerView _recyclerView;

	// models
	private List<GameCenterData_Game> _gameList;

	// adapter
	private SingleGameListAdapter _adapter;

	public static GameCenterGalleryHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_gallery"), parent, false);
		return new GameCenterGalleryHolder(convertView, switchListener);
	}

	public GameCenterGalleryHolder(View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// get views
		Context ctx = itemView.getContext();
		_titleLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.title"));
		_recyclerView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.list"));

		// setup views
		_gameList = new ArrayList<>();
		_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
			StaggeredGridLayoutManager.HORIZONTAL));
		_adapter = new SingleGameListAdapter(ctx, _gameList, Constant.GAME_LIST_GALLERY, switchListener);
		_recyclerView.setAdapter(_adapter);
	}

	@Override
	public void onBind(GameCenterData model, int position) {
		// update model
		_gameList.clear();
		_gameList.addAll(model.getGameList());

		// reload list
		_adapter.notifyDataSetChanged();

		// update ui
		_titleLabel.setText(model.getName());
	}
}
