package com.cquant.lizone.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;

public class AppTool {
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	public static String getRelease() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static int getApiVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static int getVersion(Context cx) {
		try {
			PackageManager manager = cx.getPackageManager();
			PackageInfo info = manager.getPackageInfo(cx.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	public static String getVersionName(Context cx) {
		try {
			PackageManager manager = cx.getPackageManager();
			PackageInfo info = manager.getPackageInfo(cx.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "1.2.0";
		}
	}
	public static PackageInfo getPackageInf(Context cx) {
		try {
			PackageManager manager = cx.getPackageManager();
			return manager.getPackageInfo(cx.getPackageName(), 0);  
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static int[] getDisplay(Activity activity) {
		int[] result = new int[2];
		Display display = activity.getWindowManager().getDefaultDisplay();
		result[0] = display.getWidth();
		result[1] = display.getHeight();
		return result;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = getDensity(context);
		return (int) (dpValue * scale + 0.5f);
	}

	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static void installApk(String file_path, Context context) {
		File apkfile = new File(file_path);
		if (!apkfile.exists()) {
			return;
		}
		Uri uri = Uri.fromFile(apkfile);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(i);
	}

	
	/**
	 * 
	 * @param n
	 *            the range of the random values.
	 * @param m
	 *            the num of the generate random values.
	 * @return return the generated ramdom values.
	 */
	public int[] random(int n, int m) {
		int[] x = new int[m];
		x[0] = (int) (Math.random() * n) + 1;

		for (int j = 1; j < m;) {
			x[j] = (int) (Math.random() * n) + 1;
			int t = 0;
			for (int i = 0; i < j; i++) {
				if (x[j] != x[i])
					t++;
				else
					t = 0;
			}
			if (t == j)
				j++;
		}
		return x;
	}

	public static String readShareValues(Context cx, String fileName, String key) {
		return cx.getSharedPreferences(fileName, 0).getString(key, "");

	}

	public static void writeShareValues(Context cx, String fileName,
			String key, String value) {
		Editor editor = cx.getSharedPreferences(fileName, 0).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void writeShareValues(Context cx, String fileName,
			HashMap<String, String> maps) {
		Editor editor = cx.getSharedPreferences(fileName, 0).edit();
		Set<Entry<String, String>> entrys = maps.entrySet();
		for (Entry<String, String> entry : entrys) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		editor.commit();
	}

	public static String readFromAssets(Context cx, String filename) {
		String result = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(cx
					.getAssets().open(filename)));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
			reader.close();

			result = builder.toString();
		} catch (IOException e) {
			result = null;
		}
		return result;
	}
	public static String readFromStream(InputStream is) {
		String result = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();

			result = builder.toString();
		} catch (IOException e) {
			result = null;
		}
		return result;
	}
	public static String[] insertElement(String original[], String element,
			int index) {
		int length = original.length;
		String destination[] = new String[length + 1];
		System.arraycopy(original, 0, destination, 0, index);
		destination[index] = element;
		System.arraycopy(original, index, destination, index + 1, length
				- index);
		return destination;
	}
}
