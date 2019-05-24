package com.ledong.lib.minigame.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ledong.lib.minigame.IGameSwitchListener;

/**
 * view holder的通用基类, 封装了一些通用字段和功能
 */
public abstract class CommonViewHolder<T> extends RecyclerView.ViewHolder {
	// game jump
	protected IGameSwitchListener _switchListener;

	// long press
	protected IHolderLongClickListener _longClickListener;

	// source app info
	protected String _orientation;
	protected String _srcAppId;
	protected String _srcAppPath;

	public CommonViewHolder(View itemView, IGameSwitchListener switchListener) {
		super(itemView);

		// init
		_switchListener = switchListener;
	}

	/**
	 * 设置来源信息
	 */
	public void setSourceGame(String orientation, String srcAppId, String srcAppPath) {
		_orientation = orientation;
		_srcAppId = srcAppId;
		_srcAppPath = srcAppPath;
	}

	/**
	 * 设置一个长按事件监听器, 如果为null, 则删除长按事件监听器
	 */
	public void setHolderLongClickListener(IHolderLongClickListener listener, final int position) {
		_longClickListener = listener;
		if(_longClickListener != null) {
			itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (null != _longClickListener) {
						_longClickListener.onItemLongClick(position);
					}
					return false;
				}
			});
		} else {
			itemView.setOnLongClickListener(null);
		}
	}

	public abstract void onBind(T model, int position);
}
