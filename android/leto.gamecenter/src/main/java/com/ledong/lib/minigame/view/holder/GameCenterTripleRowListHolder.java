package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.FavoriteActivity;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.SingleGameListActivity;
import com.ledong.lib.minigame.SingleGameListAdapter;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.recycleview.ScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 最多三行, 水平滚动的游戏列表
 */
public class GameCenterTripleRowListHolder extends CommonViewHolder<GameCenterData> {
	// views
	private TextView _titleLabel;
	private ScrollRecyclerView _recyclerView;
	private TextView _moreLabel;

	// adapter
	private SingleGameListAdapter _adapter;

	// game list
	private List<GameCenterData_Game> _gameList;

	public static GameCenterTripleRowListHolder create(Context ctx, ViewGroup parent, IGameSwitchListener sl) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_triple_row_list"), parent, false);
		return new GameCenterTripleRowListHolder(convertView, sl);
	}

	public GameCenterTripleRowListHolder(View view, IGameSwitchListener switchListener) {
		super(view, switchListener);

		// views
		_titleLabel = view.findViewById(MResource.getIdByName(view.getContext(), "R.id.title"));
		_recyclerView = view.findViewById(MResource.getIdByName(view.getContext(), "R.id.recyclerView"));
		_moreLabel = view.findViewById(MResource.getIdByName(view.getContext(), "R.id.more_textview"));

		// setup views
		_gameList = new ArrayList<>();
		_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
			StaggeredGridLayoutManager.HORIZONTAL));

		// set adapter
		_adapter = new SingleGameListAdapter(view.getContext(), _gameList, Constant.GAME_LIST_TRIPLE_ROW, switchListener);
		_recyclerView.setAdapter(_adapter);
	}

	@Override
	public void onBind(final GameCenterData model, int position) {
		// update model
		_gameList.clear();
		_gameList.addAll(model.getGameList());
		if(_gameList.size() < 3) {
			_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(_gameList.size(),
				StaggeredGridLayoutManager.HORIZONTAL));
		} else {
			_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
				StaggeredGridLayoutManager.HORIZONTAL));
		}
		_adapter.notifyDataSetChanged();

		// title
		_titleLabel.setText(model.getName());

		// more
		_moreLabel.setVisibility(model.isShowMore() ? View.VISIBLE : View.INVISIBLE);
		_moreLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (model.getName().equalsIgnoreCase(view.getContext().getResources().getString(MResource.getIdByName(view.getContext(), "R.string.leto_recently_played")))) {
					FavoriteActivity.start(view.getContext(), "", "", 1);
				} else {
					SingleGameListActivity.start(view.getContext(), model, 0, Constant.GAME_LIST_SINGLE,
						model.getName(), _orientation, _srcAppId, _srcAppPath);
				}
			}
		});
	}
}