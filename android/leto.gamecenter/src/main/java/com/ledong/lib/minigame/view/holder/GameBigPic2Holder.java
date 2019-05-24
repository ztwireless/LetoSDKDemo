package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.graphics.Outline;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.view.PlayNowButton;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.ColorUtil;
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.MResource;

public class GameBigPic2Holder extends CommonViewHolder<GameCenterData_Game> {
	// views
	private ImageView _pictureView;
	private ImageView _iconView;
	private TextView _nameLabel;
	private TextView _playNumLabel;
	private PlayNowButton _playButton;
	private View _infoBar;

	public static GameBigPic2Holder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_list_item_big_pic_2"), parent, false);
		return new GameBigPic2Holder(convertView, switchListener);
	}

	public GameBigPic2Holder(View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// views
		final Context ctx = itemView.getContext();
		_infoBar = itemView.findViewById(MResource.getIdByName(ctx, "R.id.info_bar"));
		_pictureView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.picture"));
		_iconView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.icon"));
		_nameLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.name"));
		_playNumLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.play_num"));
		_playButton = itemView.findViewById(MResource.getIdByName(ctx, "R.id.open_btn"));
		View outline = itemView.findViewById(MResource.getIdByName(ctx, "R.id.outline"));

		// round rect
		outline.setOutlineProvider(new ViewOutlineProvider() {
			@Override
			public void getOutline(View view, Outline outline) {
				outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), DensityUtil.dip2px(ctx, 5));
			}
		});
		outline.setClipToOutline(true);
	}

	@Override
	public void onBind(GameCenterData_Game model, int position) {
		// context
		Context ctx = itemView.getContext();

		// name
		_nameLabel.setText(model.getName());

		// color
		_infoBar.setBackgroundColor(ColorUtil.parseColor(model.getBackgroundcolor()));

		// play number
		_playNumLabel.setText(model.getPlay_num() + ctx.getString(MResource.getIdByName(ctx, "R.string.w_play_num")));

		// play button
		_playButton.setGameBean(model);
		_playButton.setGameSwitchListener(_switchListener);

		// icon
		Glide.with(ctx)
			.load(model.getIcon())
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
			.load(model.getPic())
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
