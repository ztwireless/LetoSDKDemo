package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leto.game.base.bean.GameCenterData_Category;
import com.leto.game.base.util.MResource;

/**
 * category的类似tab的cell, 用在AllCategoryActivity
 */
public class CategoryTabHolder extends CommonViewHolder<GameCenterData_Category> {
	// views
	private TextView _label;
	private View _activeFlag;

	public static CategoryTabHolder create(Context ctx, ViewGroup parent) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_list_item_category_tab"), parent, false);
		return new CategoryTabHolder(convertView);
	}

	public CategoryTabHolder(View itemView) {
		super(itemView, null);

		// views
		Context ctx = itemView.getContext();
		_label = itemView.findViewById(MResource.getIdByName(ctx, "R.id.label"));
		_activeFlag = itemView.findViewById(MResource.getIdByName(ctx, "R.id.active_flag"));
	}

	@Override
	public void onBind(GameCenterData_Category model, int position) {
		// label
		_label.setText(model.getName());
	}

	public void setActive(boolean active) {
		_activeFlag.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
		itemView.setBackgroundColor(active ? Color.WHITE : 0xfff5f5f5);
	}
}
