package com.cquant.lizone.tool;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

public class DlgTool {
	private static PopupWindow mPop = null;
	private static TextView mTv_msg = null;
    private static Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
		  if(msg.what==0){
			  closePopDlg();
		  }
		}
    	
    };
	public static void closePopDlg() {
		if (mPop != null) {
			if (mPop.isShowing()) {
				mPop.dismiss();
				mPop = null;
			}
		}
	}
	public static void closeDelayed(int time){
		mHandler.sendEmptyMessageDelayed(0, time);
	}

	public static boolean isShowing() {
		return mPop != null && mPop.isShowing();
	}
	public static void showAsDropDown(View anchor, final View layout,
			int[] offset, boolean isLeft) {
		mPop = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		mPop.setFocusable(true);
		//mPop.setBackgroundDrawable(anchor.getContext().getResources().getDrawable(
		//		isLeft ? R.drawable.img_list_bk_left : R.drawable.img_list_bk_right));
		mPop.setTouchInterceptor(new OnTouchListener() {
			private boolean isOutsideTouch(MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				if (x < 0 || y < 0 || x > layout.getWidth() || y > layout.getHeight()) {
					return true;
				}
				return false;

			}

			public boolean onTouch(View v, MotionEvent event) {
				if (isOutsideTouch(event)) {
					return false;
				}
				return false;
			}
		});
		mPop.showAsDropDown(anchor, offset[0], offset[1]);
	}
	public static void showAtLocation(View anchor, final View layout,int gravity,int ox,int oy) {
		mPop = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		mPop.setFocusable(true);
		mPop.setOutsideTouchable(true);
		mPop.setTouchInterceptor(new OnTouchListener() {
			private boolean isOutsideTouch(MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				if (x < 0 || y < 0 || x > layout.getWidth() || y > layout.getHeight()) {
					return true;
				}
				return false;

			}

			public boolean onTouch(View v, MotionEvent event) {
				if (isOutsideTouch(event)) {
					return true;
				}
				return false;
			}
		});
		mPop.showAtLocation(anchor, gravity, ox, oy);
	}
	private static Drawable getDrawable(){
        ShapeDrawable bgdrawable =new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(0xffffffff);
        return  bgdrawable;
   }
	public static void showAsDropDown(View anchor, String msg, int duration,
			int[] offset, boolean isDown) {
		Context cx = anchor.getContext();
		TextView im = new TextView(cx);
		im.setText(msg);
		im.setTextColor(Color.BLACK);
		mPop = new PopupWindow(im, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		mPop.setFocusable(true);
		mPop.setOutsideTouchable(true);
		//mPop.setBackgroundDrawable(cx.getResources().getDrawable(
		//		isDown ? R.drawable.talkbox_down : R.drawable.talkbox_up));
		mPop.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mHandler.sendEmptyMessage(0);
				return true;
			}
		});
		//mPop.setAnimationStyle(R.style.style_pop_center_scale);
		mPop.showAsDropDown(anchor, offset[0], offset[1]);
		if (duration > 50 && duration < 10000) {
			new Timer().schedule(new TimerTask() {
				public void run() {
					mHandler.sendEmptyMessage(0);
				}
			}, duration);
		}
	}




	public static boolean updateStateDlg(String msg) {
		if (isShowing()) {
			mTv_msg.setText(msg);
			mTv_msg.invalidate();
			mPop.update();
			return true;
		}
		return false;
	}

	public static void updateStateDlg(String msg, int duration) {
		if (updateStateDlg(msg)) {
			if (duration > 50 && duration < 10000) {
				new Timer().schedule(new TimerTask() {
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				}, duration);
			}
		}
	}
	
}
