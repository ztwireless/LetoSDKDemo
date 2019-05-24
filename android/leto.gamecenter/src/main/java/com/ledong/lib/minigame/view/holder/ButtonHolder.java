package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.AllCategoryActivity;
import com.ledong.lib.minigame.AllRankingActivity;
import com.ledong.lib.minigame.SingleGameListActivity;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Category;
import com.leto.game.base.bean.GameCenterRequestBean;
import com.leto.game.base.util.BaseAppUtil;
import com.leto.game.base.util.MResource;

public class ButtonHolder extends CommonViewHolder<GameCenterData> {
	// views
	private ImageView _iconView;
	private TextView _label;

	public ButtonHolder(View itemView) {
		super(itemView, null);

		// views
		Context ctx = itemView.getContext();
		_iconView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.icon"));
		_label = itemView.findViewById(MResource.getIdByName(ctx, "R.id.label"));
	}

	public static ButtonHolder create(Context ctx, ViewGroup parent) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_list_item_button"), parent, false);
		ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
		layoutParams.width = BaseAppUtil.getDeviceWidth(ctx) / 4;
		convertView.setLayoutParams(layoutParams);
		return new ButtonHolder(convertView);
	}

	@Override
	public void onBind(final GameCenterData model, int position) {
		final Context ctx = itemView.getContext();
		final GameCenterData_Category btn = model.getCategoryList().get(position);
		switch(btn.getId()) {
			case GameCenterData_Category.BUTTON_CATEGORY:
				_label.setText(TextUtils.isEmpty(btn.getName()) ? ctx.getString(MResource.getIdByName(ctx, "R.string.category")) : btn.getName());
				Glide.with(ctx)
					.load(btn.getIcon())
					.placeholder(MResource.getIdByName(ctx, "R.drawable.leto_btn_category"))
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(_iconView);
				break;
			case GameCenterData_Category.BUTTON_RANKING:
				_label.setText(TextUtils.isEmpty(btn.getName()) ? ctx.getString(MResource.getIdByName(ctx, "R.string.ranking")) : btn.getName());
				Glide.with(ctx)
					.load(btn.getIcon())
					.placeholder(MResource.getIdByName(ctx, "R.drawable.leto_btn_ranking"))
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(_iconView);
				break;
			case GameCenterData_Category.BUTTON_NEW_GAME:
				_label.setText(TextUtils.isEmpty(btn.getName()) ? ctx.getString(MResource.getIdByName(ctx, "R.string.new_game")) : btn.getName());
				Glide.with(ctx)
					.load(btn.getIcon())
					.placeholder(MResource.getIdByName(ctx, "R.drawable.leto_btn_new_game"))
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(_iconView);
				break;
			case GameCenterData_Category.BUTTON_RECOMMENDED:
				_label.setText(TextUtils.isEmpty(btn.getName()) ? ctx.getString(MResource.getIdByName(ctx, "R.string.recommended")) : btn.getName());
				Glide.with(ctx)
					.load(btn.getIcon())
					.placeholder(MResource.getIdByName(ctx, "R.drawable.leto_btn_recommended"))
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(_iconView);
				break;
		}
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch(btn.getId()) {
					case GameCenterData_Category.BUTTON_CATEGORY:
						AllCategoryActivity.start(ctx, null, 0, _orientation, _srcAppId, _srcAppPath);
						break;
					case GameCenterData_Category.BUTTON_RANKING:
						AllRankingActivity.start(ctx, null, _orientation, _srcAppId, _srcAppPath);
						break;
					case GameCenterData_Category.BUTTON_NEW_GAME:
						SingleGameListActivity.start(ctx, GameCenterRequestBean.DATA_TYPE_NEW_GAME,
							Constant.GAME_LIST_SINGLE, ctx.getString(MResource.getIdByName(ctx, "R.string.must_play_new_game")),
							_orientation, _srcAppId, _srcAppPath);
						break;
					case GameCenterData_Category.BUTTON_RECOMMENDED:
						SingleGameListActivity.start(ctx, GameCenterRequestBean.DATA_TYPE_RECOMMENDED,
							Constant.GAME_LIST_RECOMMENDED, ctx.getString(MResource.getIdByName(ctx, "R.string.recommended")),
							_orientation, _srcAppId, _srcAppPath);
						break;
				}
			}
		});
	}
}
