package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ledong.lib.minigame.AllCategoryActivity;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Category;
import com.leto.game.base.bean.GameTypeModel;
import com.leto.game.base.bean.MoreCategory;
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.recycleview.GridSpacingItemDecoration;

public class GameCenterCategoryRankingHolder extends CommonViewHolder<GameCenterData> {
	// model
	private GameCenterData _model;

	// views
	private RecyclerView _listView;
	private TextView _titleLabel;
	private TextView _moreLabel;

	public static GameCenterCategoryRankingHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_category_ranking"), parent, false);
		return new GameCenterCategoryRankingHolder(convertView, switchListener);
	}

	public GameCenterCategoryRankingHolder(View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// views
		Context ctx = itemView.getContext();
		_titleLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.title"));
		_listView = itemView.findViewById(MResource.getIdByName(ctx, "R.id.list"));
		_moreLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.more"));

		// setup views
		_listView.setLayoutManager(new GridLayoutManager(ctx, 2));
		_listView.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtil.dip2px(ctx, 7), false));
		_listView.setAdapter(new CategoryAdapter());
	}

	@Override
	public void onBind(GameCenterData model, int position) {
		// save
		_model = model;

		// title
		_titleLabel.setText(model.getName());

		// reload
		_listView.getAdapter().notifyDataSetChanged();

		// more
		_moreLabel.setVisibility(model.isShowMore() ? View.VISIBLE : View.INVISIBLE);
		_moreLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AllCategoryActivity.start(itemView.getContext(), _model, 0, _orientation, _srcAppId, _srcAppPath);
			}
		});
	}

	private class CategoryAdapter extends RecyclerView.Adapter<CommonViewHolder<GameCenterData>> {
		@NonNull
		@Override
		public CommonViewHolder<GameCenterData> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return CategoryHolder.create(itemView.getContext(), parent);
		}

		@Override
		public void onBindViewHolder(@NonNull CommonViewHolder<GameCenterData> holder, int position) {
			holder.setSourceGame(_orientation, _srcAppId, _srcAppPath);
			holder.onBind(_model, position);
		}

		@Override
		public int getItemCount() {
			return (_model == null || _model.getCategoryList() == null) ? 0 : _model.getCategoryList().size();
		}
	}
}
