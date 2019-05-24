package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ledong.lib.minigame.IGameSwitchListener;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.recycleview.ScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameCenterRotationChartHolder extends CommonViewHolder<GameCenterData> {
	ScrollRecyclerView _recyclerView;
	List<GameCenterData_Game> gameList = new ArrayList<>();
	IGameSwitchListener _switchListener;

	public static GameCenterRotationChartHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_rotation_chart"), parent, false);
		return new GameCenterRotationChartHolder(convertView, switchListener);
	}

	public GameCenterRotationChartHolder(View view, IGameSwitchListener switchListener) {
		super(view, switchListener);

		// members
		_switchListener = switchListener;

		// views
		_recyclerView = view.findViewById(MResource.getIdByName(view.getContext(), "R.id.bannerRecyclerView"));

		// setup views
		_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
			StaggeredGridLayoutManager.HORIZONTAL));
		_recyclerView.setAdapter(new BannerAdapter());
	}

	@Override
	public void onBind(GameCenterData model, int position) {
		gameList.clear();
		gameList.addAll(model.getGameList());
		_recyclerView.getAdapter().notifyDataSetChanged();
	}

	public class BannerAdapter extends RecyclerView.Adapter<BannerHolder> {
		@Override
		public BannerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return BannerHolder.create(itemView.getContext(), _switchListener);
		}

		@Override
		public void onBindViewHolder(BannerHolder holder, int position) {
			holder.onBind(gameList.get(position), position);
		}

		@Override
		public int getItemCount() {
			return gameList == null ? 0 : gameList.size();
		}
	}
}