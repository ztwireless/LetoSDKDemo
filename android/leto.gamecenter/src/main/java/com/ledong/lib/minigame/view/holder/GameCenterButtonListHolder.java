package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ledong.lib.minigame.IGameSwitchListener;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameTypeModel;
import com.leto.game.base.util.MResource;

public class GameCenterButtonListHolder extends CommonViewHolder<GameCenterData> {
	// views
	private RecyclerView _listView;

	// model
	private GameCenterData _model;

	public static GameCenterButtonListHolder create(Context ctx, ViewGroup parent) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_button_list"), parent, false);
		return new GameCenterButtonListHolder(convertView, null);
	}

	public GameCenterButtonListHolder(View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// find views
		Context ctx = itemView.getContext();
		_listView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.list"));

		// setup views
		LinearLayoutManager layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
		_listView.setLayoutManager(layoutManager);
		_listView.setAdapter(new ListAdapter());
	}

	@Override
	public void onBind(GameCenterData model, int position) {
		_model = model;
		_listView.getAdapter().notifyDataSetChanged();
	}

	private class ListAdapter extends RecyclerView.Adapter<ButtonHolder> {
		@NonNull
		@Override
		public ButtonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return ButtonHolder.create(parent.getContext(), parent);
		}

		@Override
		public void onBindViewHolder(@NonNull ButtonHolder holder, int position) {
			holder.setSourceGame(_orientation, _srcAppId, _srcAppPath);
			holder.onBind(_model, position);
		}

		@Override
		public int getItemCount() {
			return (_model == null || _model.getCategoryList() == null) ? 0 : _model.getCategoryList().size();
		}
	}
}
