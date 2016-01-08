package com.minkbox.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ServiceHandler {

	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;

	public ServiceHandler() {

	}

	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	public String makeServiceCall(String url, int method,
			List<NameValuePair> params) {
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);
				if (params != null) {
					JSONObject jsonObj = new JSONObject();
					for (int i = 0; i < params.size(); i++) {
						NameValuePair np = params.get(i);
						try {
							jsonObj.put(np.getName(), np.getValue());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					JSONArray jsonArray = new JSONArray();
					jsonArray.put(jsonObj);
					System.out.println("json array :--------- "
							+ jsonArray.toString());
					Log.d("json Array :----", jsonArray.toString());
					StringEntity se = null;
					try {
						se = new StringEntity(jsonArray.toString());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					httpPost.setEntity(se);
				}

				httpResponse = httpClient.execute(httpPost);

			} else if (method == GET) {
				// appending params to url
				if (params != null) {
					String paramString = URLEncodedUtils
							.format(params, "utf-8");
					url += "?" + paramString;
				}
				HttpGet httpGet = new HttpGet(url);

				httpResponse = httpClient.execute(httpGet);

			}
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;

	}
}
