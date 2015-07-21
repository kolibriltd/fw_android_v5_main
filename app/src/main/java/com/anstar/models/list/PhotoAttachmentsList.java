package com.anstar.models.list;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Const;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.MySSLSocketFactory;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.Account;
import com.anstar.models.ModelDelegates.CommonDelegate;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.PhotoAttachmentsInfo;

public class PhotoAttachmentsList {

	public PhotoAttachmentsList() {

	}

	public static String PHOTO_ATTACHMENT_LIST_NOTIFICATION = "PHOTO_ATTACHMENT_LIST_NOTIFICATION";

	private static volatile PhotoAttachmentsList _instance = null;

	public static PhotoAttachmentsList Instance() {
		if (_instance == null) {
			synchronized (PhotoAttachmentsList.class) {
				_instance = new PhotoAttachmentsList();
			}
		}
		return _instance;
	}

	protected ArrayList<PhotoAttachmentsInfo> m_modelList = null;

	public void parseAttachments(JSONObject obj, int w_id) {

		JSONArray items;
		try {
			items = obj.getJSONArray("photo_attachments");
			if (items != null) {
				ContextWrapper cw = new ContextWrapper(
						FieldworkApplication.getContext());
				// path to /data/data/yourapp/app_data/imageDir
				File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS,
						Context.MODE_PRIVATE);

				for (int i = 0; i < items.length(); i++) {
					JSONObject form = items.getJSONObject(i);
					ModelMapHelper<PhotoAttachmentsInfo> areamaper = new ModelMapHelper<PhotoAttachmentsInfo>();
					PhotoAttachmentsInfo info = areamaper.getObject(
							PhotoAttachmentsInfo.class, form);

					String path = Const.FieldWorkImages + "_"
							+ info.appointment_occurrence_id + "_" + info.id;
					// File mypath = new File(directory, path + ".jpg");
					// boolean b = mypath.delete();
					// Utils.LogInfo("delete image file " + path + " Result :: "
					// + b);
					String url = "";
					StringBuilder sb = new StringBuilder();
					sb.append(ServiceHelper.URL);
					sb.append(ServiceHelper.WORK_ORDERS + "/" + w_id + "/"
							+ ServiceHelper.PHOTO_ATTACHMENTS);
					url = sb.toString();
					String imasgeurl = url + "/" + info.id + "/download"
							+ "?api_key=" + Account.getkey();
					downloadfile(w_id, info.id, imasgeurl, null);
					if (info != null) {
						try {
							// info.appointment_occurrence_id = w_id;
							info.save();
						} catch (Exception e) {
						}
					}
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void refreshAttachments(final int wo_id,
			final UpdateInfoDelegate delegate) {
		String url = String.format("work_orders/%d/photo_attachments", wo_id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.GET, null);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {

				try {
					deletePdfForm(wo_id);
					JSONArray items = new JSONArray(res.RawResponse);
					for (int i = 0; i < items.length(); i++) {
						JSONObject pest = items.getJSONObject(i);
						ModelMapHelper<PhotoAttachmentsInfo> maper = new ModelMapHelper<PhotoAttachmentsInfo>();
						PhotoAttachmentsInfo info = maper.getObject(
								PhotoAttachmentsInfo.class, pest);
						if (info != null) {
							try {
								info.save();
							} catch (Exception e) {
							}
						}
					}
					delegate.UpdateSuccessFully(res);
					NotificationCenter.Instance().postNotification(
							PHOTO_ATTACHMENT_LIST_NOTIFICATION);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				delegate.UpdateFail(ErrorMessage);
			}
		});
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection()
					.delete(PhotoAttachmentsInfo.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<PhotoAttachmentsInfo> lst = FieldworkApplication.Connection()
					.find(PhotoAttachmentsInfo.class,
							CamelNotationHelper
									.toSQLName("appointment_occurrence_id")
									+ "=?", new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (PhotoAttachmentsInfo i : lst) {
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	// public void refresh(int appointment_id) throws Exception {
	// m_appointmentId = appointment_id;
	// if (NetworkConnectivity.isConnected()) {
	// ServiceHelper helper = new ServiceHelper(
	// ServiceHelper.APPOINTMENT_OCCURENCES + "/" + appointment_id
	// + "/" + ServiceHelper.PEST_TARGETS);
	// helper.call(this);
	// }
	// }

	public ArrayList<PhotoAttachmentsInfo> load(int wo_id) {
		ArrayList<PhotoAttachmentsInfo> m_list = new ArrayList<PhotoAttachmentsInfo>();
		try {
			List<PhotoAttachmentsInfo> list = FieldworkApplication.Connection()
					.find(PhotoAttachmentsInfo.class,
							CamelNotationHelper
									.toSQLName("appointment_occurrence_id")
									+ "=? and "
									+ CamelNotationHelper
											.toSQLName("isDeleted") + " = ?",
							new String[] { String.valueOf(wo_id),
									String.valueOf(false) });

			if (list != null) {
				if (list.size() > 0) {
					for (PhotoAttachmentsInfo PhotoAttachmentsInfo : list) {
						if (!PhotoAttachmentsInfo.isDeleted) {
							m_list.add(PhotoAttachmentsInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public ArrayList<PhotoAttachmentsInfo> loadAll(int wo_id) {
		ArrayList<PhotoAttachmentsInfo> m_list = new ArrayList<PhotoAttachmentsInfo>();
		try {
			List<PhotoAttachmentsInfo> list = FieldworkApplication.Connection()
					.find(PhotoAttachmentsInfo.class,
							CamelNotationHelper
									.toSQLName("appointment_occurrence_id")
									+ "=?",
							new String[] { String.valueOf(wo_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (PhotoAttachmentsInfo PhotoAttachmentsInfo : list) {
						m_list.add(PhotoAttachmentsInfo);
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public void deletePdfForm(int wo_id) {
		try {
			int cnt = FieldworkApplication.Connection().delete(
					PhotoAttachmentsInfo.class,
					CamelNotationHelper.toSQLName("appointment_occurrence_id")
							+ "=?", new String[] { String.valueOf(wo_id) });
			Utils.LogInfo(String.format(
					"%d records deleted of photo attachment for work order %d",
					cnt, wo_id));
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public ArrayList<PhotoAttachmentsInfo> getPhotosByappointment_occurrence_id(
			int id) {
		ArrayList<PhotoAttachmentsInfo> m_list = new ArrayList<PhotoAttachmentsInfo>();
		try {
			List<PhotoAttachmentsInfo> list = FieldworkApplication.Connection()
					.findAll(PhotoAttachmentsInfo.class);
			if (list.size() > 0) {
				for (PhotoAttachmentsInfo record : list) {
					if (record.appointment_occurrence_id == id) {
						m_list.add(record);
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public String saveToInternalSorage(Bitmap bitmapImage, int appointment_id,
			int photoid) {
		ContextWrapper cw = new ContextWrapper(
				FieldworkApplication.getContext());
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS,
				Context.MODE_PRIVATE);
		// Create imageDir
		String path = Const.FieldWorkImages + "_" + appointment_id + "_"
				+ photoid;
		File mypath = new File(directory, path + ".jpg");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to
			// the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return directory.getAbsolutePath();
	}

	public class DownloadAsync extends AsyncTask<Void, Void, Bitmap> {

		String url = "";
		int app_id = 0;
		int photo_id = 0;
		CommonDelegate delegate;

		public DownloadAsync(String s, int appointment_id, int photoid,
				CommonDelegate del) {
			url = s;
			app_id = appointment_id;
			photo_id = photoid;
			delegate = del;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			HttpClient client = getNewHttpClient();
			try {
				HttpGet get = new HttpGet(url);
				final HttpParams httpParams = new BasicHttpParams();
				HttpClientParams.setRedirecting(httpParams, true);
				get.setParams(httpParams);
				HttpResponse resp = client.execute(get);
				int status = resp.getStatusLine().getStatusCode();
				if (status != HttpURLConnection.HTTP_OK) {

					return null;
				}
				HttpEntity entity = resp.getEntity();
				InputStream is = entity.getContent();
				Bitmap myBitmap = BitmapFactory.decodeStream(is);
				return myBitmap;
			} catch (Exception ex) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				saveToInternalSorage(result, app_id, photo_id);
				if (delegate != null)
					delegate.UpdateSuccessFully(true);
			} else {
				if (delegate != null)
					delegate.UpdateFail("Problem in downloading file");
			}
		}

	}

	public static HttpClient getNewHttpClient() {
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

	CommonDelegate delegate;

	public void downloadfile(final int appointment_id, final int photoid,
			final String imasgeurl, CommonDelegate del) {
		// DownloadAsync load = new DownloadAsync(imasgeurl, appointment_id,
		// photoid, del);
		// load.execute();

		delegate = del;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				final Handler handler = new Handler() {

					public void handleMessage(Message msg) {
						boolean response = msg.getData().getBoolean("message");
						if (response) {
							if (delegate != null)
								delegate.UpdateSuccessFully(true);
						} else {
							if (delegate != null)
								delegate.UpdateFail("Problem in downloading file");
						}
					}
				};
				HttpClient client = getNewHttpClient();
				try {
					HttpGet get = new HttpGet(imasgeurl);
					final HttpParams httpParams = new BasicHttpParams();
					HttpClientParams.setRedirecting(httpParams, true);
					get.setParams(httpParams);
					HttpResponse resp = client.execute(get);
					int status = resp.getStatusLine().getStatusCode();
					if (status != HttpURLConnection.HTTP_OK) {

					}
					HttpEntity entity = resp.getEntity();
					InputStream is = entity.getContent();
					Bitmap myBitmap = BitmapFactory.decodeStream(is);

					Message msgObj = handler.obtainMessage();
					Bundle b = new Bundle();

					if (myBitmap != null) {
						saveToInternalSorage(myBitmap, appointment_id, photoid);
						b.putBoolean("message", true);
					} else {
						b.putBoolean("message", false);
					}
					msgObj.setData(b);
					handler.sendMessage(msgObj);
					Looper.loop();
					// return myBitmap;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		t.start();

	}

}
