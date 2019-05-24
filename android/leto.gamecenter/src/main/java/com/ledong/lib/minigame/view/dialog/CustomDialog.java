package com.ledong.lib.minigame.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Keep;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ledong.lib.leto.utils.DeviceInfo;
import com.leto.game.base.util.DensityUtil;
import com.leto.game.base.util.MResource;

/**
 * Created by zzh on 2018/3/14.
 */

@Keep
public class CustomDialog {
    private Dialog dialog;
    private ConfirmDialogListener mlistener;

    public void showClearCacheDialog(Context context,final ConfirmDialogListener listener) {
        dismiss();
        this.mlistener = listener;
        View dialogview = LayoutInflater.from(context).inflate(MResource.getIdByName(context,"R.layout.leto_gamecenter_dialog_custom"), null);
        dialog = new Dialog(context, MResource.getIdByName(context,"R.style.LetoCustomDialog"));
        //设置view
        dialog.setContentView(dialogview);
        dialog.setCanceledOnTouchOutside(true);
        //dialog默认是环绕内容的
        //通过window来设置位置、高宽
        Window window = dialog.getWindow();
        WindowManager.LayoutParams windowparams = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        windowparams.width = DeviceInfo.getWidth(context);
//        设置背景透明,但是那个标题头还是在的，只是看不见了
        //注意：不设置背景，就不能全屏
//        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView tv_title = dialogview.findViewById(MResource.getIdByName(context,"R.id.tv_title"));
        TextView tv_hint = dialogview.findViewById(MResource.getIdByName(context,"R.id.tv_hint"));
        TextView btn_left = dialogview.findViewById(MResource.getIdByName(context,"R.id.mgc_dialog_btn_left"));
        TextView btn_right = dialogview.findViewById(MResource.getIdByName(context,"R.id.mgc_dialog_btn_right"));
        tv_title.setText(context.getString(MResource.getIdByName(context,"R.string.leto_title_clear_cache")));
        tv_hint.setText(context.getString(MResource.getIdByName(context,"R.string.leto_clear_cache_content")));

        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onCancel();
                }
                dialog.dismiss();
            }
        });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onConfirm();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showUnFavoriteDialog(Context context,final ConfirmDialogListener listener) {
        dismiss();
        this.mlistener = listener;
        View dialogview = LayoutInflater.from(context).inflate(MResource.getIdByName(context,"R.layout.leto_gamecenter_dialog_custom"), null);
        dialog = new Dialog(context, MResource.getIdByName(context,"R.style.LetoCustomDialog"));
        //设置view
        dialog.setContentView(dialogview);
        dialog.setCanceledOnTouchOutside(true);
        //dialog默认是环绕内容的
        //通过window来设置位置、高宽
        Window window = dialog.getWindow();
        WindowManager.LayoutParams windowparams = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        windowparams.width = DeviceInfo.getWidth(context);
//        设置背景透明,但是那个标题头还是在的，只是看不见了
        //注意：不设置背景，就不能全屏
//        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView tv_title = dialogview.findViewById(MResource.getIdByName(context,"R.id.tv_title"));
        TextView tv_hint = dialogview.findViewById(MResource.getIdByName(context,"R.id.tv_hint"));
        TextView btn_left = dialogview.findViewById(MResource.getIdByName(context,"R.id.mgc_dialog_btn_left"));
        TextView btn_right = dialogview.findViewById(MResource.getIdByName(context,"R.id.mgc_dialog_btn_right"));
        tv_title.setText(context.getString(MResource.getIdByName(context, "R.string.leto_title_cancel_favorite")));
        tv_hint.setText("");

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(DensityUtil.dip2px(context, 19));
        drawable.setColor(Color.parseColor("#FC3371"));
        btn_right.setBackground(drawable);

        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onCancel();
                }
                dialog.dismiss();
            }
        });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onConfirm();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            mlistener = null;
        }
    }

    public interface ConfirmDialogListener {
        void onConfirm();

        void onCancel();
    }


}
