package com.ledong.lib.minigame.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ledong.lib.leto.api.constant.Constant;
import com.ledong.lib.minigame.AllRankingActivity;
import com.ledong.lib.minigame.IGameSwitchListener;
import com.ledong.lib.minigame.SingleGameListFragment;
import com.leto.game.base.bean.GameCenterData;
import com.leto.game.base.bean.GameCenterData_Game;
import com.leto.game.base.bean.GameCenterData_Rank;
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.MResource;
import com.leto.game.base.view.tablayout.CommonTabLayout;
import com.leto.game.base.view.tablayout.entity.TabEntity;
import com.leto.game.base.view.tablayout.listener.CustomTabEntity;
import com.leto.game.base.view.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class GameCenterTabRankingHolder extends CommonViewHolder<GameCenterData> implements OnTabSelectListener, ViewPager.OnPageChangeListener {
	// views
	private CommonTabLayout _tabs;
	private ViewPager _viewPager;
	private TextView _moreLabel;

	// title of tabs
	private List<String> _titles;

	// model
	private GameCenterData _model;

	// original id of view pager
	private int _originalPagerId;

	public static GameCenterTabRankingHolder create(Context ctx, ViewGroup parent, IGameSwitchListener switchListener) {
		View convertView = LayoutInflater.from(ctx).inflate(MResource.getIdByName(ctx, "R.layout.leto_gamecenter_item_tab_ranking"), parent, false);
		return new GameCenterTabRankingHolder(convertView, switchListener);
	}

	public GameCenterTabRankingHolder(View itemView, IGameSwitchListener switchListener) {
		super(itemView, switchListener);

		// views
		Context ctx = itemView.getContext();
		_tabs = itemView.findViewById(MResource.getIdByName(ctx, "R.id.tabs"));
		_originalPagerId = MResource.getIdByName(ctx, "R.id.viewPager");
		_viewPager = itemView.findViewById(_originalPagerId);
		_moreLabel = itemView.findViewById(MResource.getIdByName(ctx, "R.id.more"));

		// setup view pager
		_titles = new ArrayList<>();
		_viewPager.setOffscreenPageLimit(2);
		_viewPager.addOnPageChangeListener(this);
		_viewPager.setAdapter(new TabPagerAdapter(((FragmentActivity)itemView.getContext()).getSupportFragmentManager()));

		// setup tabs
		_tabs.setOnTabSelectListener(this);
	}

	@Override
	public void onBind(GameCenterData model, int position) {
		// save
		_model = model;

		// setup tabs
		int size = Math.min(3, _model.getRankList().size());
		ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			// add title
			GameCenterData_Rank c = _model.getRankList().get(i);
			String title = c.getName();
			_titles.add(c.getName());

			// add tab
			tabEntities.add(new TabEntity(title, 0, 0));
		}

		// set tabs
		_tabs.setTabData(tabEntities);
		_tabs.setCurrentTab(0);

		// setup pages
		// XXX: you must set id for view pager because they won't share same id in same fragment
		Context ctx = itemView.getContext();
		_viewPager.setId(_originalPagerId + position + 1);
		_viewPager.getAdapter().notifyDataSetChanged();
		_viewPager.setCurrentItem(0);

		// set view pager height, 85dp is height of game row cell
		ViewGroup.LayoutParams llp = _viewPager.getLayoutParams();
		llp.height = DensityUtil.dip2px(ctx, 85 * _model.getRankList().get(0).getGameList().size());
		_viewPager.setLayoutParams(llp);

		// more
		_moreLabel.setVisibility(model.isShowMore() ? View.VISIBLE : View.INVISIBLE);
		_moreLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AllRankingActivity.start(itemView.getContext(), _model, _orientation, _srcAppId, _srcAppPath);
			}
		});
	}

	@Override
	public void onTabSelect(int position) {
		if(_viewPager.getCurrentItem() != position) {
			_viewPager.setCurrentItem(position);
		}
	}

	@Override
	public void onTabReselect(int position) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		if(_tabs.getCurrentTab() != position) {
			_tabs.setCurrentTab(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private boolean isGameListSame(List<GameCenterData_Game> list1, List<GameCenterData_Game> list2) {
		if(list1.size() != list2.size()) {
			return false;
		}
		int size = list1.size();
		for(int i = 0; i < size; i++) {
			GameCenterData_Game m1 = list1.get(i);
			GameCenterData_Game m2 = list2.get(i);
			if(m1.getId() != m2.getId()) {
				return false;
			}
		}
		return true;
	}

	private class TabPagerAdapter extends FragmentStatePagerAdapter {
		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return _titles.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return _titles.get(position);
		}

		@Override
		public Fragment getItem(int position) {
			return SingleGameListFragment.getInstance(Constant.GAME_LIST_TAB_RANKING,
				(ArrayList<GameCenterData_Game>)_model.getRankList().get(position).getGameList(),
				_orientation, _srcAppId, _srcAppPath, 15, 15);
		}

		@Override
		public int getItemPosition(@NonNull Object object) {
			SingleGameListFragment f = (SingleGameListFragment)object;
			int size = Math.min(3, _model.getRankList().size());
			for(int i = 0; i < size; i++) {
				GameCenterData_Rank c = _model.getRankList().get(i);
				if(isGameListSame(f.getGameList(), c.getGameList())) {
					return POSITION_UNCHANGED;
				}
			}
			return POSITION_NONE;
		}
	}
}
