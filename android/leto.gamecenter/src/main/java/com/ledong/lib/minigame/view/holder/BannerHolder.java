package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.ClickUtil;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.RoundedImageView;

public class BannerHolder extends CommonViewHolder<GameCenterData_Game> {
	private RoundedImageView _iconView;
	private View _rootView;

	public static BannerHolder create(Context ctx, IGameSwitchListener switchListener) {
		View view = View.inflate(ctx, MResource.getIdByName(ctx, "R.layout.leto_list_item_banner"), null);
		return new BannerHolder(view, switchListener);
	}

	public BannerHolder(View view, IGameSwitchListener switchListener) {
		super(view, switchListener);
		_rootView = view;
		this._iconView = view.findViewById(MResource.getIdByName(view.getContext(), "R.id.iv_banner_img"));
	}

	@Override
	public void onBind(final GameCenterData_Game model, int position) {
		Glide.with(_rootView.getContext()).load(model.getPic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(_iconView);
		_iconView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ClickUtil.isFastClick()) {
					return;
				}
				clickGame(model);
			}
		});
	}

	private void clickGame(GameCenterData_Game model) {
		if(null == model) {
			return;
		}
		if(model.getClassify() == 10 && TextUtils.isEmpty(model.getPackageurl())) {
			Toast.makeText(_rootView.getContext(), "该游戏暂未上线", Toast.LENGTH_SHORT);
			return;
		}

		if(null != _switchListener) {
			_switchListener.onJump(model);
		}
	}
}