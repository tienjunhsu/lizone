package com.cquant.lizone.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.GlobalVar;
//import com.umeng.socialize.utils.Log;

public class WebUtils {
	private static int connectTimeout = 6000;
	private static int readTimeout = 10000;
	public static final String DEFAULT_CHARSET = "UTF-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";

	public static boolean hasInternet(Context context) {
		android.net.ConnectivityManager connectivity = (android.net.ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			android.net.NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == android.net.NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 */
	public static String doPost(String url, JSONObject params)
			throws IOException {
		return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
	}

	public static String doPost(String url, String params)
			throws IOException {
		return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
	}
	/**
	 */
	public static String doPost(String url, String params, String charset,
								int connectTimeout, int readTimeout) throws IOException {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query =params;
		//String query = "mobile=18682169419&id_card=330719196804253675&bank_code=009&bank_card=6222821476530862&email=xtj@qq.com&managerno=39001&verify_code=1410&name=孙ni";
		// URLEncoder.encode(params.toString(), charset);
		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	/**
	 */
	public static String doPost(String url, JSONObject params, String charset,
			int connectTimeout, int readTimeout) throws IOException {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query ;
		if(params.has("isOpenAcountOnline")){
			query = getOpenAcountStr(params);
		} else {
			query = params.toString();
		}
		//String query = "mobile=18682169419&id_card=330719196804253675&bank_code=009&bank_card=6222821476530862&email=xtj@qq.com&managerno=39001&verify_code=1410&name=孙ni";
		// URLEncoder.encode(params.toString(), charset);
		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	//begin add by TianjunXu.2015/1/10
	private static String getOpenAcountStr(JSONObject params) {
		String str ="";
		try {
			str ="mobile="+params.getString("mobile")+"&id_card="+params.getString("id_card")+"&bank_code="+params.getString("bank_code")+"&email="+params.getString("email")
					+"&managerno="+params.getString("managerno")+"&verify_code="+params.getString("verify_code")+"&name="+params.getString("name")+"&bank_card="+params.getString("bank_card");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	//end add by TianjunXu

	/**
	 */
	public static String doPost(String url, String ctype, byte[] content,
			int connectTimeout, int readTimeout) throws IOException {
		Log.d("TianjunXu", " dopost");
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				conn = getConnection(new URL(url), METHOD_POST, ctype);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {
				@SuppressWarnings("unused")
				Map<String, String> map = getParamsFromUrl(url);
				throw e;
			}
			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn);
				getSession(conn);
			} catch (IOException e) {
				@SuppressWarnings("unused")
				Map<String, String> map = getParamsFromUrl(url);
				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rsp;
	}
   private  static void setSession(HttpURLConnection conn) {
	   if (GlobalVar.SESSIONID!= null) {
		   conn.setRequestProperty("Cookie", "PHPSESSID=" + GlobalVar.SESSIONID);
	   }
   }
	private static void getSession(HttpURLConnection conn) {
		String cookieVal =conn.getHeaderField("Set-Cookie");
       // 获取session
		if (cookieVal != null) {
			GlobalVar.SESSIONID= cookieVal.substring(10, cookieVal.indexOf(";"));//10 is length(PHPSESSID=)
			Log.d("TianjunXu", " getSession:"+cookieVal);
			Log.d("TianjunXu", " GlobalVar.SESSIONID:"+GlobalVar.SESSIONID);
		}
	}


	public static String doGet(String url, Map<String, String> params)
			throws IOException {
		return doGet(url, params, DEFAULT_CHARSET);
	}

	public static String doGet(String url, Map<String, String> params,
			String charset) throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;
		try {
			String ctype = "application/x-www-form-urlencoded;charset="
					+ charset;
			String query = buildQuery(params, charset);
			try {
				conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {
				@SuppressWarnings("unused")
				Map<String, String> map = getParamsFromUrl(url);
				throw e;
			}

			try {
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				@SuppressWarnings("unused")
				Map<String, String> map = getParamsFromUrl(url);
				throw e;
			}

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static HttpURLConnection getConnection(URL url, String method,
			String ctype) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		if (method.equals("POST")) {
			// 4.0
			conn.setDoOutput(true);
		}
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "top-sdk-java");
		conn.setRequestProperty("Content-Type", ctype);
		if (method.equals("GET")) {
			setSession(conn);//hsu
		}
		return conn;
	}

	private static URL buildGetUrl(String strUrl, String query)
			throws IOException {
		URL url = new URL(strUrl);
		if (StrTool.isEmpty(query)) {
			return url;
		}

		if (StrTool.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}
		return new URL(strUrl);
	}

	protected static String getResponseAsString(HttpURLConnection conn)
			throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		} else {
			String msg = getStreamAsString(es, charset);
			if (StrTool.isEmpty(msg)) {
				throw new IOException(conn.getResponseCode() + ":"
						+ conn.getResponseMessage());
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static String getStreamAsString(InputStream stream, String charset)
			throws IOException {
		try {
			if (stream == null) {
				throw new IOException("Stream from net is null");
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StrTool.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StrTool.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	public static String buildQuery(Map<String, String> params, String charset)
			throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}
		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;
		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (!StrTool.areEmpty(name, value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

				query.append(name).append("=")
						.append(URLEncoder.encode(value, charset));
			}
		}
		return query.toString();
	}

	private static Map<String, String> getParamsFromUrl(String url) {
		Map<String, String> map = null;
		if (url != null && url.indexOf('?') != -1) {
			map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
		}
		if (map == null) {
			map = new HashMap<String, String>();
		}
		return map;
	}

	/**
	 * 浠嶶RL涓彁鍙栨墍鏈夌殑鍙傛暟锟� *
	 * 
	 * @param query
	 *            URL鍦板�?
	 * @return 鍙傛暟鏄犲皠
	 */
	public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}

		return result;
	}
}
