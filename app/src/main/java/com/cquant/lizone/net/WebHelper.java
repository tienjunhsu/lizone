package com.cquant.lizone.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

public class WebHelper {
	private Context context;
	ArrayList<LoadThread> mReques = new ArrayList<LoadThread>();

	private Handler handler = new Handler();

	public void cancleRequest() {
		synchronized (this) {
			int n = mReques.size();
			for (int i = 0; i < n; i++) {
				mReques.remove(0).interrupt();
			}
		}
	}

	public WebHelper(Context c) {
		context = c;
	}

	public boolean isNetAccess() {
		return WebUtils.hasInternet(context);
	}

	public boolean doLoadPost(final String url, final JSONObject params,
			OnWebFinished onFinishLoad) {
		if (!isNetAccess()) {
			return false;
		}
		LoadThread thread = new LoadPostThrd(url, params, onFinishLoad);
		mReques.add(thread);
		thread.start();
		return true;
	}
	public boolean doLoadPost(final String url, final String params,
							  OnWebFinished onFinishLoad) {
		if (!isNetAccess()) {
			return false;
		}
		LoadThread thread = new StrPostThrd(url, params, onFinishLoad);
		mReques.add(thread);
		thread.start();
		return true;
	}
	public boolean doLoadGet(final String url,
			final Map<String, String> params, OnWebFinished onFinishLoad) {
		if (!isNetAccess()) {
			return false;
		}
		LoadThread thread = new LoadGetThrd(url, params, onFinishLoad);
		mReques.add(thread);
		thread.start();
		return true;
	}

	class LoadPostThrd extends LoadThread {
		JSONObject mJsnObj = null;

		public LoadPostThrd(String url, JSONObject params, OnWebFinished finish) {
			super(url, finish);
			mJsnObj = params;
		}

		public String waitForJson() throws IOException, InterruptedException {

			return WebUtils.doPost(mUrl, mJsnObj);
		}
	}
	class StrPostThrd extends LoadThread {
		String mJsnObj = null;

		public StrPostThrd(String url, String params, OnWebFinished finish) {
			super(url, finish);
			mJsnObj = params;
		}

		public String waitForJson() throws IOException, InterruptedException {

			return WebUtils.doPost(mUrl, mJsnObj);
		}
	}

	class LoadGetThrd extends LoadThread {
		Map<String, String> mMaps = null;

		public LoadGetThrd(String url, Map<String, String> params,
				OnWebFinished finish) {
			super(url, finish);
			mMaps = params;
		}

		@Override
		public String waitForJson() throws IOException, InterruptedException {

			return WebUtils.doGet(mUrl, mMaps);
		}
	}

	class LoadThread extends Thread {
		protected boolean mStop = false;
		protected OnWebFinished mOnFinishLoad = null;
		protected String mUrl = null;
		protected String mMsg = null;

		public LoadThread(String url, OnWebFinished finish) {
			mUrl = url;
			mOnFinishLoad = finish;
		}

		protected String waitForJson() throws IOException, InterruptedException {

			return "";
		}

		@Override
		public void interrupt() {
			mStop = true;
			super.interrupt();
		}

		public void callBack(boolean success) {
			mOnFinishLoad.onWebFinished(success, mMsg);
		}

		@Override
		public void run() {
			try {
				mMsg = waitForJson();
				if (!mStop) {
					handler.post(new UiRunable(this, true));
				}

			} catch (InterruptedException e) {
				return;
			} catch (IOException e) {
				mMsg = e.toString();
				if (!mStop) {
					handler.post(new UiRunable(this, false));
				}
			}
		}
	}

	class UiRunable implements Runnable {
		LoadThread loadTrd = null;
		boolean what = false;

		public UiRunable(LoadThread loadTrd, boolean what) {
			this.loadTrd = loadTrd;
			this.what = what;
		}

		public void run() {
			loadTrd.callBack(what);
		}
	}

	public interface OnWebFinished {
		public void onWebFinished(boolean success, String msg);
	}
}
