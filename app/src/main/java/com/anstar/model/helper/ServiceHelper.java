package com.anstar.model.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;

import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.models.Account;

public class ServiceHelper {

	public static final String COMMON_ERROR = "Could not connect to server, please try again later";
	public static final String WORK_ORDERS = "work_orders";
	public static final String PEST_TARGETS = "pests_targets";

	public static final String CUSTOMERS = "customers";
	public static final String CUSTOMERS_THINNED = "customers/thinned.json";
	public static final String PEST_TYPES = "pest_types";
	public static final String MATERIALS = "materials";
	public static final String INVOICEHiren = "invoice";
	public static final String LOCATION_TYPE = "location_types";
	public static final String MATERIAL_USAGES = "material_usages";
	public static final String MATERIAL_USAGE_RECORDS = "material_usage_records";
	public static final String DILUTION_RATES = "dilution_rates";
	public static final String STATUSES = "statuses";
	public static final String MEASUREMENTS = "measurements";
	public static final String APPLICATIONMETHOD = "application_methods_with_id";
	public static final String GET_SERVICE_REPORT = "service_report.pdf";
	public static final String GET_TRAPS = "traps";
	public static final String GET_USER = "user";
	public static final String ATTACHED_PDF_FORM = "attached_pdf_form.pdf";

	public static final String DEVICE_TYPES = "application_device_types";
	public static final String TRAP_TYPES = "trap_types";
	public static final String TRAP_CONDITIONS = "trap_conditions";
	public static final String BAIT_CONDITIONS = "bait_conditions";
	public static final String BILLING_TERMS = "billing_terms";
	public static final String SERVICE_ROUTES = "service_routes";

	public static final String GETPDF = "pdf_forms";
	public static final String ATTACHMENTS = "attachments";
	public static final String TAX_RATES = "tax_rates";
	public static final String SERVICES = "services";
	public static final String HISTORY = "history";
	public static final String SERVICE_LOCATIONS = "service_locations";
	public static final String RECOMENDATIONS = "recommendations";
	public static final String APPOINTMENT_CONDITIONS = "appointment_conditions";
	public static final String PHOTO_ATTACHMENTS = "photo_attachments";
	public static final String SendLocation = "user/coordinates";

	public enum RequestMethod {
		GET, POST
	};

	public enum ModelProcess {
		Insert, Delete, Update
	}

	public static final String LOGIN = "get_key";

	public interface ServiceHelperDelegate {
		/**
		 * Calls when got the response from the API
		 * 
		 * @param res
		 *            Service Response Obejct
		 */
		public void CallFinish(ServiceResponse res);

		/**
		 * Service call fail with error message
		 * 
		 * @param ErrorMessage
		 *            Error Message
		 */
		public void CallFailure(String ErrorMessage);
	}

	String m_methodName = null;
	private ServiceHelperDelegate m_delegate = null;
	private int m_tag = 0;
	// live url
	public static final String URL = "https://api.fieldworkhq.com/v2/";
	// public static final String URL = "https://api.fieldworkapp.com/v2/";
	private ArrayList<String> m_params = new ArrayList<String>();
	private static int REQUEST_TIMEOUT = 20000;
	public RequestMethod RequestMethodType = RequestMethod.GET;
	public int m_statuscode = 0;

	public ServiceHelper(String method) {
		m_methodName = method;

	}

	public ServiceHelper(String method, int tag) {
		m_methodName = method;
		m_tag = tag;
		RequestMethodType = RequestMethod.GET;
	}

	public ServiceHelper(String method, RequestMethod requestMethod) {
		m_methodName = method;
		RequestMethodType = requestMethod;
	}

	public void addParam(String key, String value) {
		m_params.add(key + "=" + value);
	}

	public void addParam(String key, int value) {
		m_params.add(key + "=" + String.valueOf(value));
	}

	public void setParams(ArrayList<String> params) {
		m_params = new ArrayList<String>(params);
	}

	private String getFinalUrl() {
		StringBuilder sb = new StringBuilder();

		sb.append(URL);
		sb.append(m_methodName.toString() + "?");
		// get api key
		sb.append("api_key=");
		sb.append(Account.getkey());
		return sb.toString();
	}

	public void call(ServiceHelperDelegate delegate) {
		m_delegate = delegate;
		if (NetworkConnectivity.isConnectedwithoutmode()) {
			// CallServiceAsync calling = new CallServiceAsync(true);
			// calling.execute();
			CallService();
		} else {
			if (RequestMethodType == RequestMethod.POST) {
				Toast.makeText(FieldworkApplication.getContext(),
						"Please check your internet connection", 2).show();
				String params = Utils.Instance().join(m_params, "##");
				// SyncData.SaveSyncInfo(RequestMethodType.toString(), params,
				// m_methodName);
			}
		}
	}

	private String call() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = getNewHttpClient();
		HttpRequestBase request = null;
		if (RequestMethodType == RequestMethod.GET) {
			request = new HttpGet(getFinalUrl());
			request.setHeader("Accept", "*/*");
			request.setHeader("Mobile-App-Version",
					ServiceCaller.getHeaderVersion());
			request.setHeader("Content-Type", "text/plain; charset=utf-8");
		} else {
			request = new HttpPost(getFinalUrl());
			request.setHeader("Mobile-App-Version",
					ServiceCaller.getHeaderVersion());
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			if (m_params.size() > 0) {
				String queryString = Utils.Instance().join(m_params, "&");
				StringEntity se = null;
				try {
					Utils.LogInfo("QueryString ::: " + queryString);
					se = new StringEntity(queryString, "UTF-8");
					((HttpPost) request).setEntity(se);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			request.getParams().setParameter("http.socket.timeout",
					REQUEST_TIMEOUT);
			Utils.LogInfo("**URL : " + getFinalUrl());
			request.setHeader("Cache-Control", "no-cache");

			HttpResponse response = client.execute(request);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			m_statuscode = statusCode;
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				builder.append(statusCode);
				Utils.LogInfo("Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			builder.append("{\"result\":{\"code\":1011,\"error\":\"Unknow error occuerd, please try again later.\"}}");
		}
		return builder.toString();
	}

	public void callUrl(String url, ServiceHelperDelegate delegate) {
		m_delegate = delegate;
		// CallServiceAsync calling = new CallServiceAsync(url);
		// calling.execute();
		CallService(url);
	}

	private DefaultHttpClient getHttpClient() {
		final HttpParams httpParams = new BasicHttpParams();

		// SchemeRegistry schemeRegistry = new SchemeRegistry();
		// schemeRegistry.register(new Scheme("http", SSLSocketFactory
		// .getSocketFactory(), 80));
		//
		// SingleClientConnManager mgr = new SingleClientConnManager(httpParams,
		// schemeRegistry);
		// HttpProtocolParams.setUseExpectContinue(httpParams, false);
		// HttpConnectionParams.setConnectionTimeout(httpParams,
		// REQUEST_TIMEOUT);
		httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				REQUEST_TIMEOUT);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
		httpclient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {

			@Override
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= 2) {
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					return true;
				} else if (exception instanceof ClientProtocolException) {
					return true;
				}
				return false;
			}
		});
		return httpclient;
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

	private String call(String url) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = getNewHttpClient();
		HttpRequestBase request = null;

		request = new HttpPost(url);
		try {
			request.setHeader("Cache-Control", "no-cache");

			HttpResponse response = client.execute(request);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			m_statuscode = statusCode;
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				builder.append(statusCode);
				Utils.LogInfo("Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	class CallServiceAsync extends AsyncTask<Void, Void, ServiceResponse> {

		boolean m_withFullHeader = false;
		String m_url = null;

		public CallServiceAsync(boolean withFullHeader) {
			m_withFullHeader = withFullHeader;
		}

		public CallServiceAsync(String url) {
			m_url = url;
		}

		@Override
		protected ServiceResponse doInBackground(Void... params) {
			int statuscode = 0;
			String strResponse = "";
			if (m_url == null) {
				strResponse = call();
			} else {
				strResponse = call(m_url);
			}
			ServiceResponse response = new ServiceResponse();
			response.RawResponse = strResponse;
			response.StatusCode = m_statuscode;
			return response;
		}

		@Override
		protected void onPostExecute(ServiceResponse result) {
			if (m_delegate != null) {
				m_delegate.CallFinish(result);
			}
			super.onPostExecute(result);
		}

	}

	public void CallService() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int statuscode = 0;
				String strResponse = "";
				strResponse = call();
				ServiceResponse response = new ServiceResponse();
				response.RawResponse = strResponse;
				response.StatusCode = m_statuscode;
				Message m = new Message();
				m.obj = response;
				handler.sendMessage(m);

			}
		}).start();

	}

	public void CallService(final String m_url) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int statuscode = 0;
				String strResponse = "";
				strResponse = call(m_url);
				ServiceResponse response = new ServiceResponse();
				response.RawResponse = strResponse;
				response.StatusCode = m_statuscode;
				Message m = new Message();
				m.obj = response;
				handler.sendMessage(m);

			}
		}).start();

	}

	Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg != null) {
				if (msg.obj != null) {
					ServiceResponse response = (ServiceResponse) msg.obj;
					if (m_delegate != null) {
						m_delegate.CallFinish(response);
					}
				}
			}

			return false;
		}
	});

}
