package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.view.PlayNowButton;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.MResource;

public class GameGridHolder extends CommonViewHolder<GameCenterData_Game> {
	TextView tv_game_name;
	ImageView iv_game_icon;
	PlayNowButton open_btn;
	private TextView _playNumText;

	public static GameGridHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View view = View.inflate(ctx, MResource.getIdByName(ctx, "R.layout.leto_list_item_game_grid"), null);
		return new GameGridHolder(view, switchListener);
	}

	public GameGridHolder(View view, IGameSwitchListener switchListener) {
		super(view, switchListener);
		Context ctx = view.getContext();
		tv_game_name = view.findViewById(MResource.getIdByName(ctx, "R.id.tv_game_name"));
		iv_game_icon = view.findViewById(MResource.getIdByName(ctx, "R.id.iv_game_icon"));
		open_btn = view.findViewById(MResource.getIdByName(ctx, "R.id.open_btn"));
		_playNumText = view.findViewById(MResource.getIdByName(ctx, "R.id.play_num"));
	}

	@Override
	public void onBind(GameCenterData_Game model, int position) {
		// name
		Context ctx = itemView.getContext();
		tv_game_name.setText(model.getName());

		// icon
		Glide.with(ctx).load(model.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_game_icon);
		iv_game_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				open_btn.callOnClick();
			}
		});

		// open button
		open_btn.setGameBean(model);
		open_btn.setGameSwitchListener(_switchListener);

		// play number
		_playNumText.setText(model.getPlay_num() + ctx.getString(MResource.getIdByName(ctx, "R.string.w_play_num")));
	}
}