package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.view.PlayNowButton;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.MResource;

public class GameCenterBigPicHolder extends CommonViewHolder<GameCenterData> {
	// views
	private TextView _titleLabel;
	private ImageView _pictureView;
	private ImageView _iconView;
	private TextView _nameLabel;
	private TextView _playNumLabel;
	private PlayNowButton _playButton;

	public static GameCenterBigPicHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_big_pic"), parent, false);
		return new GameCenterBigPicHolder(convertView, switchListener);
	}

	public GameCenterBigPicHolder(View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// views
		Context ctx = itemView.getContext();
		_titleLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.title"));
		_pictureView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.picture"));
		_iconView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.icon"));
		_nameLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.name"));
		_playNumLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.play_num"));
		_playButton = itemView.findViewById(MResource.getIdByName(ctx, "R.id.open_btn"));
	}

	@Override
	public void onBind(GameCenterData model, int position) {
		// context
		Context ctx = itemView.getContext();

		if(model !=null && model.getGameList().size()>0) {

			// get model
			GameCenterData_Game game = model.getGameList().get(0);

			// title
			_titleLabel.setText(model.getName());

			// name
			_nameLabel.setText(game.getName());

			// play number
			_playNumLabel.setText(game.getPlay_num() + ctx.getString(MResource.getIdByName(ctx, "R.string.w_play_num")));

			// play button
			_playButton.setGameBean(game);
			_playButton.setGameSwitchListener(_switchListener);

			// icon
			Glide.with(ctx)
					.load(game.getIcon())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(_iconView);
			_iconView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					_playButton.callOnClick();
				}
			});

			// picture
			Glide.with(ctx)
					.load(game.getPic())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(_pictureView);
			_pictureView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					_playButton.callOnClick();
				}
			});

		}
	}
}
