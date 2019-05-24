package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ledong.lib.minigame.AllCategoryActivity;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Category;
import com.leto.game.base.util.MResource;

public class CategoryHolder extends CommonViewHolder<GameCenterData> {
	// views
	private ImageView _iconView;
	private TextView _label;

	public static CategoryHolder create(Context ctx, ViewGroup parent) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_list_item_category"), parent, false);
		return new CategoryHolder(convertView);
	}

	public CategoryHolder(View itemView) {
		super(itemView, null);

		// views
		Context ctx = itemView.getContext();
		_iconView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.icon"));
		_label = itemView.findViewById(MResource.getIdByName(ctx, "R.id.label"));
	}

	@Override
	public void onBind(final GameCenterData model, final int position) {
		final Context ctx = itemView.getContext();

		// label
		GameCenterData_Category cat = model.getCategoryList().get(position);
		_label.setText(cat.getName());

		// icon
		Glide.with(ctx)
			.load(cat.getIcon())
			.placeholder(MResource.getIdByName(ctx, "R.drawable.leto_category_contest"))
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.into(_iconView);

		// handle click
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AllCategoryActivity.start(ctx, model, position, _orientation, _srcAppId, _srcAppPath);
			}
		});
	}
}
