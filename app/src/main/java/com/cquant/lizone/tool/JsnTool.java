package com.cquant.lizone.tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsnTool {
 

	public static String getString(JSONObject obj, String key) {
		String str = null;
		if (obj.has(key)) {
			try {
				str = obj.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public static long getLong(JSONObject obj, String key) {
		long val = 0;
		if (obj.has(key)) {
			try {
				val = obj.getLong(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return val;
	}
	public static double getDouble(JSONObject obj, String key) {
		double val = 0;
		if (obj.has(key)) {
			try {
				val = obj.getDouble(key);  
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return val;
	}
	public static int getInt(JSONObject obj, String key) {
		int val = 0;
		if (obj.has(key)) {
			try {
				val = obj.getInt(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return val;
	}

	public static JSONObject getObject(JSONObject obj, String key) {
		JSONObject o = null;
		if (obj.has(key)) {
			try {
				o = obj.getJSONObject(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return o;
	}

	public static JSONArray getArray(JSONObject obj, String key) {
		JSONArray ary = null;
		if (obj.has(key)) {
			try {
				ary = obj.getJSONArray(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ary;
	}

	public static JSONObject getObject(String json) {
		JSONObject o = null;
		try {
			o = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o;
	}

	public static JSONArray getArray(String json) {
		JSONArray ary = null;
		try {
			ary = new JSONArray(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ary;
	}

	public static boolean getBoolean(JSONObject obj, String key) {
		boolean res = false;
		try {
			res = obj.getBoolean(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}
}
