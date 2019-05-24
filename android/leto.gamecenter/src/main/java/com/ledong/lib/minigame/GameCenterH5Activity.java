package com.ledong.lib.minigame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ledong.lib.leto.Leto;
import com.ledong.lib.leto.interfaces.ILetoContainer;
import com.ledong.lib.leto.interfaces.ILetoContainerProvider;
import com.ledong.lib.leto.main.LetoWebContainerFragment;
import com.leto.game.base.util.BaseAppUtil;
import com.leto.game.base.util.IntentConstant;
import com.leto.game.base.util.MResource;

public class GameCenterH5Activity extends FragmentActivity implements ILetoContainerProvider {
	// views
	private View _titleBar;
	private ImageView _backImgView;
	private TextView _titleText;

	// fragment
	private LetoWebContainerFragment _fragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// init leto
		Leto.init(this);

		// set content
		setContentView(MResource.getIdByName(this, "R.layout.leto_game_center_h5_activity"));

		// find views
		_titleBar = findViewById(MResource.getIdByName(this, "R.id.title_bar"));
		_backImgView = findViewById(MResource.getIdByName(this, "R.id.back"));
		_titleText = findViewById(MResource.getIdByName(this, "R.id.title"));

		// get extras
		Intent intent = getIntent();
		boolean showTitleBar = intent.getBooleanExtra(IntentConstant.SHOW_TITLE_BAR, false);
		String title = intent.getStringExtra(IntentConstant.TITLE);

		// title bar & title
		_titleBar.setVisibility(showTitleBar ? View.VISIBLE : View.GONE);
		if(!TextUtils.isEmpty(title)) {
			_titleText.setText(title);
		}

		// back
		_backImgView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// add fragment
		_fragment = LetoWebContainerFragment.create(BaseAppUtil.getMetaStringValue(this, "H5_GAME_CENTER_URL"));
		getSupportFragmentManager()
			.beginTransaction()
			.add(MResource.getIdByName(this, "R.id.container"), _fragment)
			.commit();
	}

	@Keep
	public static void start(Context context) {
		if(null != context) {
			Intent intent = new Intent(context, GameCenterH5Activity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
			context.startActivity(intent);
		}
	}

	@Override
	public ILetoContainer getLetoContainer() {
		return _fragment;
	}
}
