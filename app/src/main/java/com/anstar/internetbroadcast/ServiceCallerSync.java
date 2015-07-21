package com.anstar.internetbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;
import android.os.Build;

import com.anstar.common.Utils;
import com.anstar.model.helper.MySSLSocketFactory;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.Account;

public class ServiceCallerSync {

	public enum RequestMethod {
		GET, POST, PUT, DELETE
	};

	public static final String URL = ServiceHelper.URL;
	private static int REQUEST_TIMEOUT = 20000;
	public RequestMethod RequestMethodType = RequestMethod.GET;
	private ServiceHelperDelegate m_delegate = null;

	private String m_mainUrl = "";
	private String m_data = "";
	public int m_statuscode = 0;
	private int m_tag = 0;

	public ServiceCallerSync(String urlPart, RequestMethod method, String data) {
		m_mainUrl = String.format("%s%s?api_key=%s", ServiceCallerSync.URL,
				urlPart, Account.getkey());
		RequestMethodType = method;
		m_data = data;
	}

	public ServiceResponse startRequest() {
		String strResponse = "";
		strResponse = call();
		ServiceResponse response = new ServiceResponse();
		response.RawResponse = strResponse;
		response.StatusCode = m_statuscode;
		response.Tag = m_tag;
		return response;
	}

	public void SetTag(int tag) {
		m_tag = tag;
	}

	private String call() {
		boolean isException = false;
		StringBuilder builder = new StringBuilder();
		HttpClient client = getNewHttpClient();
		HttpRequestBase request = null;
		if (RequestMethodType == RequestMethod.GET) {
			request = new HttpGet(m_mainUrl);

		} else if (RequestMethodType == RequestMethod.POST) {

			request = new HttpPost(m_mainUrl);
			StringEntity se = null;
			try {
				se = new StringEntity(m_data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			((HttpPost) request).setEntity(se);
		} else if (RequestMethodType == RequestMethod.PUT) {

			request = new HttpPut(m_mainUrl);

			StringEntity se = null;
			try {
				se = new StringEntity(m_data, "UTF-8");
				Utils.LogInfo("**QUery string : " + m_data);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			((HttpPut) request).setEntity(se);
		} else if (RequestMethodType == RequestMethod.DELETE) {
			request = new HttpDelete(m_mainUrl);
		}

		request.setHeader("Accept", "*/*");
		request.setHeader("Mobile-App-Version", ServiceCaller.getHeaderVersion());
		request.setHeader("Content-Type", "application/json");

		try {
			request.getParams().setParameter("http.socket.timeout",
					REQUEST_TIMEOUT);
			Utils.LogInfo("**URL : " + m_mainUrl);
			request.setHeader("Cache-Control", "no-cache");

			HttpResponse response = client.execute(request);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			m_statuscode = statusCode;

			if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				}
			} else {
				builder.append(statusCode);
				Utils.LogInfo("Failed to download file");
			}
		} catch (ClientProtocolException e) {
			builder = new StringBuilder();
			builder.append("error");
			e.printStackTrace();
			isException = true;
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
			builder.append("{\"result\":{\"code\":1011,\"error\":\"Unknow errro occuerd, please try again later.\"}}");
		}
		return builder.toString();
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}
