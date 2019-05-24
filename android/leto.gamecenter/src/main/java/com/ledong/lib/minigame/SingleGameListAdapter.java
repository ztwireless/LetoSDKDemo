package com.ledong.lib.minigame;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.view.holder.GameBigPic2Holder;
import com.ledong.lib.minigame.view.holder.CommonViewHolder;
import com.ledong.lib.minigame.view.holder.GalleryHolder;
import com.ledong.lib.minigame.view.holder.GameGridHolder;
import com.ledong.lib.minigame.view.holder.GameRowHolder;
import com.ledong.lib.minigame.view.holder.GameSimpleGridHolder;
import com.ledong.lib.minigame.view.holder.IHolderLongClickListener;
import com.leto.game.base.bean.GameCenterData_Game;

import java.util.List;

/**
 * 一个游戏列表的adapter, 根据列表样式使用不同的item形式, 目前只支持长格式和紧凑格式,
 * 对于新游戏中心, 有更多样式需要支持
 */
public class SingleGameListAdapter extends RecyclerView.Adapter<CommonViewHolder<GameCenterData_Game>> {
	// context
	private Context _ctx;

	// models
	private List<GameCenterData_Game> _gameList;

	// style
	private int _style;

	// listener
	private IGameSwitchListener _switchListener;
	private IHolderLongClickListener _longClickListener;

	public SingleGameListAdapter(Context context, List<GameCenterData_Game> list, int compact, IGameSwitchListener switchListener) {
		this._gameList = list;
		this._ctx = context;
		this._switchListener = switchListener;
		this._style = compact;
	}

	public void setHolderLongClickListener(IHolderLongClickListener listener) {
		_longClickListener = listener;
	}

	@Override
	public CommonViewHolder<GameCenterData_Game> onCreateViewHolder(ViewGroup parent, int viewType) {
		switch(_style) {
			case Constant.GAME_LIST_TRIPLE_ROW:
				return GameRowHolder.create(_ctx, parent, 25, 10, _switchListener);
			case Constant.GAME_LIST_GALLERY:
				return GalleryHolder.create(_ctx, parent, _switchListener);
			case Constant.GAME_LIST_TAB_RANKING:
			case Constant.GAME_LIST_CATEGORY_RANKING:
			case Constant.GAME_LIST_ONE_RANKING:
			{
				GameRowHolder holder = GameRowHolder.create(_ctx, parent, 0, _switchListener);
				holder.setShowRank(true);
				return holder;
			}
			case Constant.GAME_LIST_FAVORITE:
			case Constant.GAME_LIST_RECENT_PLAYED:
			case Constant.GAME_LIST_SINGLE:
				return GameRowHolder.create(_ctx, parent, 0, _switchListener);
			case Constant.GAME_LIST_RECOMMENDED:
				return GameBigPic2Holder.create(_ctx, parent, _switchListener);
			case Constant.GAME_LIST_GRID:
				return GameGridHolder.create(_ctx, parent, _switchListener);
			default:
				return GameSimpleGridHolder.create(_ctx, parent, _switchListener);
		}
	}

	@Override
	public void onBindViewHolder(final CommonViewHolder<GameCenterData_Game> holder, int position) {
		holder.onBind(_gameList.get(position), position);
		holder.setHolderLongClickListener(_longClickListener, position);
	}

	@Override
	public int getItemCount() {
		return _gameList == null ? 0 : _gameList.size();
	}
}