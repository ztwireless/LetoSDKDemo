package com.ledong.lib.minigame;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.view.holder.CommonViewHolder;
import com.ledong.lib.minigame.view.holder.GameCenterBigPicHolder;
import com.ledong.lib.minigame.view.holder.GameCenterButtonListHolder;
import com.ledong.lib.minigame.view.holder.GameCenterCategoryRankingHolder;
import com.ledong.lib.minigame.view.holder.GameCenterGalleryHolder;
import com.ledong.lib.minigame.view.holder.GameCenterGridListHolder;
import com.ledong.lib.minigame.view.holder.GameCenterOneRankingHolder;
import com.ledong.lib.minigame.view.holder.GameCenterRotationChartHolder;
import com.ledong.lib.minigame.view.holder.GameCenterTabRankingHolder;
import com.ledong.lib.minigame.view.holder.GameCenterTripleRowListHolder;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterResultBean;

import java.util.ArrayList;
import java.util.List;

public class GameCenterHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GameCenterData> gameModels;
    private List<Integer> _itemTypes;
    private Context mContext;

    private IGameSwitchListener switchListener;

    private String mOrientation;
    private String mSrcAppId;
    private String mSrcAppPath;

    public GameCenterHomeAdapter(Context ctx, GameCenterResultBean data, IGameSwitchListener sl) {
        mContext = ctx;
        gameModels = data.getGameCenterData();
        switchListener = sl;

        // build item types
        _itemTypes = new ArrayList<>();
        for(GameCenterData m : gameModels) {
            _itemTypes.add(m.getCompact());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return _itemTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return _itemTypes.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType) {
            case Constant.GAME_LIST_ROTATION_CHART:
                return GameCenterRotationChartHolder.create(mContext, parent, switchListener);
            case Constant.GAME_LIST_BUTTON:
                return GameCenterButtonListHolder.create(mContext, parent);
            case Constant.GAME_LIST_TRIPLE_ROW:
                return GameCenterTripleRowListHolder.create(mContext, parent, switchListener);
            case Constant.GAME_LIST_SIMPLE_GRID:
            case Constant.GAME_LIST_GRID:
                return GameCenterGridListHolder.create(mContext, parent, switchListener);
            case Constant.GAME_LIST_GALLERY:
                return GameCenterGalleryHolder.create(mContext, parent, switchListener);
            case Constant.GAME_LIST_TAB_RANKING:
                return GameCenterTabRankingHolder.create(mContext, parent, switchListener);
            case Constant.GAME_LIST_CATEGORY_RANKING:
                return GameCenterCategoryRankingHolder.create(mContext, parent, switchListener);
            case Constant.GAME_LIST_ONE_RANKING:
                return GameCenterOneRankingHolder.create(mContext, parent, switchListener);
            case Constant.GAME_LIST_BIG_PIC:
                return GameCenterBigPicHolder.create(mContext, parent, switchListener);
            default:
                return GameCenterTripleRowListHolder.create(mContext, parent, switchListener);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int pos) {
        CommonViewHolder<GameCenterData> vh = (CommonViewHolder<GameCenterData>)holder;
        vh.onBind(gameModels.get(pos), pos);
        vh.setSourceGame(mOrientation, mSrcAppId, mSrcAppPath);
    }

    public void setSourceGame(String orientation, String srcAppId, String srcAppPath) {
        mOrientation = orientation;
        mSrcAppId = srcAppId;
        mSrcAppPath = srcAppPath;
    }
}
