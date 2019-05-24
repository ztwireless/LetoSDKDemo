package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.view.PlayNowButton;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameModel;
import com.leto.game.base.util.BaseAppUtil;
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.MResource;

public class GameRowHolder extends CommonViewHolder<GameCenterData_Game> {
	private TextView tv_game_name;
	private TextView tv_game_desc;
	private ImageView iv_game_icon;
	private LinearLayout ll_game_tag;
	private PlayNowButton open_btn;
	private View _rankContainer;
	private ImageView _rankIcon;
	private TextView _rankLabel;

	// ui flag
	private boolean _showRank;

	// rank icons
	private static final String[] RANK_ICON_RES = new String[] {
		"R.drawable.leto_rank_first",
		"R.drawable.leto_rank_second",
		"R.drawable.leto_rank_third"
	};

	/**
	 * create holder
	 * @param ctx context
	 * @param parent parent view
	 * @param rightGap 右边的空隙, 设置大于0值则其不填满全屏宽度, 这样可以看到右边是否还有cell
	 * @param switchListener switch listener
	 */
	public static GameRowHolder create(Context ctx, ViewGroup parent, int rightGap, IGameSwitchListener switchListener) {
		return GameRowHolder.create(ctx, parent, rightGap, 15, switchListener);
	}

	public static GameRowHolder create(Context ctx, ViewGroup parent, int rightGap, int rightPadding, IGameSwitchListener switchListener) {
		// load game row, and leave a gap so that next column can be seen
		View view = LayoutInflater.from(ctx)
			.inflate(MResource.getIdByName(ctx, "R.layout.leto_list_item_game_row"), parent, false);
		if(rightGap > 0) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			layoutParams.width = (BaseAppUtil.getDeviceWidth(ctx) - DensityUtil.dip2px(ctx, rightGap));
			view.setLayoutParams(layoutParams);
			int pl = view.getPaddingLeft();
			int pr = DensityUtil.dip2px(ctx, rightPadding);
			int pt = view.getPaddingTop();
			int pb = view.getPaddingBottom();
			view.setPadding(pl, pt, pr, pb);
		}
		return new GameRowHolder(view, switchListener);
	}

	public GameRowHolder(View view, IGameSwitchListener switchListener) {
		super(view, switchListener);
		Context ctx = view.getContext();
		this.tv_game_name = view.findViewById(MResource.getIdByName(ctx, "R.id.tv_game_name"));
		this.tv_game_desc = view.findViewById(MResource.getIdByName(ctx, "R.id.tv_game_desc"));
		this.iv_game_icon = view.findViewById(MResource.getIdByName(ctx, "R.id.iv_game_icon"));
		this.open_btn = view.findViewById(MResource.getIdByName(ctx, "R.id.open_btn"));
		this.ll_game_tag = view.findViewById(MResource.getIdByName(ctx, "R.id.ll_game_tag"));
		_rankContainer = view.findViewById(MResource.getIdByName(ctx, "R.id.rank_container"));
		_rankIcon = view.findViewById(MResource.getIdByName(ctx, "R.id.rank_icon"));
		_rankLabel = view.findViewById(MResource.getIdByName(ctx, "R.id.rank_label"));
	}

	@Override
	public void onBind(GameCenterData_Game model, final int position) {
		// name & desc
		Context ctx = itemView.getContext();
		tv_game_name.setText(model.getName());
		tv_game_desc.setText(model.getPublicity());

		// icon
		Glide.with(ctx).load(model.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_game_icon);
		iv_game_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				open_btn.callOnClick();
			}
		});

		// open btn init
		open_btn.setGameBean(model);
		open_btn.setGameSwitchListener(_switchListener);

		// tags
		ll_game_tag.setVisibility(View.VISIBLE);
		ll_game_tag.removeAllViews();
		if(null != model.getTags() && model.getTags().size() > 0) {
			for(int i = 0; i < model.getTags().size(); i++) {
				TextView textView = (TextView) LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_minigame_game_tag"), ll_game_tag, false);

				GradientDrawable drawable = new GradientDrawable();
				drawable.setCornerRadius(DensityUtil.dip2px(ctx, 2));
				drawable.setColor(Color.parseColor("#FFF3F3F3"));
				textView.setBackground(drawable);
				textView.setText(model.getTags().get(i));

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.rightMargin = DensityUtil.dip2px(ctx, 8);
				ll_game_tag.addView(textView, params);
			}
		}

		// rank
		if(_showRank) {
			_rankContainer.setVisibility(View.VISIBLE);
			if(position > 2) {
				_rankLabel.setVisibility(View.VISIBLE);
				_rankIcon.setVisibility(View.GONE);
				_rankLabel.setText(String.valueOf(position + 1));
			} else {
				_rankIcon.setVisibility(View.VISIBLE);
				_rankLabel.setVisibility(View.GONE);
				_rankIcon.setImageResource(MResource.getIdByName(ctx, RANK_ICON_RES[position]));
			}
		} else {
			_rankContainer.setVisibility(View.GONE);
		}
	}

	public boolean isShowRank() {
		return _showRank;
	}

	public void setShowRank(boolean _showRank) {
		this._showRank = _showRank;
	}
}