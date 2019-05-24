package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.graphics.Outline;
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
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.MResource;

public class GalleryHolder extends CommonViewHolder<GameCenterData_Game> {
	// views
	private ImageView _iconView;
	private PlayNowButton _openButton;
	private ImageView _coverView;
	private TextView _nameLabel;
	private TextView _descLabel;
	private View _outline;

	public static GalleryHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_list_item_gallery"), parent, false);
		return new GalleryHolder(convertView, switchListener);
	}

	public GalleryHolder(final View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// init
		_switchListener = switchListener;

		// find views
		final Context ctx = itemView.getContext();
		_iconView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.icon"));
		_openButton = itemView.findViewById(MResource.getIdByName(ctx, "R.id.open_btn"));
		_coverView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.cover"));
		_nameLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.name"));
		_descLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.desc"));
		_outline = itemView.findViewById(MResource.getIdByName(ctx, "R.id.outline"));

		// round rect
		_outline.setOutlineProvider(new ViewOutlineProvider() {
			@Override
			public void getOutline(View view, Outline outline) {
				outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), DensityUtil.dip2px(ctx, 5));
			}
		});
		_outline.setClipToOutline(true);
	}

	@Override
	public void onBind(GameCenterData_Game model, int position) {
		// cover
		Context ctx = itemView.getContext();
		Glide.with(ctx).load(model.getPic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(_coverView);

		// icon
		Glide.with(ctx).load(model.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(_iconView);

		// name
		_nameLabel.setText(model.getName());

		// desc
		_descLabel.setText(model.getPlay_num() + ctx.getString(MResource.getIdByName(ctx, "R.string.w_play_num")));

		// open button data
		_openButton.setGameBean(model);
		_openButton.setGameSwitchListener(_switchListener);

		// click cover to open game
		_coverView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				_openButton.callOnClick();
			}
		});
	}
}
