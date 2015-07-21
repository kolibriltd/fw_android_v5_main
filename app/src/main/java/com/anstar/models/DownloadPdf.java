package com.anstar.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.Const;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.MySSLSocketFactory;
import com.anstar.model.helper.ServiceHelper;

public class DownloadPdf {

	private static volatile DownloadPdf _instance = null;
	static ConcurrentHashMap<String, Integer> m_downloadQueue = new ConcurrentHashMap<String, Integer>();

	/**
	 * Get the Instance of the Utils Class
	 * 
	 * @return Utils Object
	 */

	public interface UploadDelegate {

		public void UploadSuccessFully(String message, boolean isedited,
				String name);

		public void UploadFailed(String error);

	}

	public interface DownLoadDelegate {

		public void DownLoadSuccessFully(String message);

		public void DownLoadFailed(String error);

	}

	public static DownloadPdf Instance() {
		if (_instance == null) {
			synchronized (DownloadPdf.class) {
				_instance = new DownloadPdf();
			}
		}
		return _instance;
	}

	public void downloadPdf(int app_id, int pdf_id, DownLoadDelegate delegate) {
		String filepath = "pdf_form_" + app_id + "_" + pdf_id + ".pdf";
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceHelper.URL);
		sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
				+ ServiceHelper.GETPDF + "/" + pdf_id + ".pdf" + "?");
		sb.append("api_key=");
		sb.append(Account.getkey());
		String url = sb.toString();
		if (isFileExist(getStoragePath(filepath))) {
			// return;
			if (delegate != null) {
				delegate.DownLoadSuccessFully("Download Successfully.");
			}
		} else {
			download(app_id, pdf_id, url, delegate);
		}
	}

	private void download(int app_id, int pdf_id, String url,
			DownLoadDelegate delegate) {
		if (m_downloadQueue.contains(url)) {
			m_downloadQueue.remove(url);
		}
		m_downloadQueue.put(url, app_id);
		if (m_downloadQueue.size() > 0) {
			Downloader downloader = new Downloader(app_id, pdf_id, url,
					delegate);
			downloader.execute();
		}
	}

	public void downloadAttachment(int app_id, int attch_id,
			DownLoadDelegate delegate) {
		String filepath = "attachment_" + app_id + "_" + attch_id + ".pdf";
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceHelper.URL);
		sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
				+ ServiceHelper.ATTACHMENTS + "/" + attch_id + ".pdf" + "?");
		sb.append("api_key=");
		sb.append(Account.getkey());
		String url = sb.toString();
		if (isFileExist(getStoragePath(filepath))) {
			// return;
			if (delegate != null) {
				delegate.DownLoadSuccessFully("Download Successfully.");
			}
		} else {
			downloadAttach(app_id, attch_id, url, delegate);
		}
	}

	private void downloadAttach(int app_id, int attch_id, String url,
			DownLoadDelegate delegate) {
		if (m_downloadQueue.contains(url)) {
			m_downloadQueue.remove(url);
		}
		m_downloadQueue.put(url, app_id);
		if (m_downloadQueue.size() > 0) {
			AttachmentDownloader downloader = new AttachmentDownloader(app_id,
					attch_id, url, delegate);
			downloader.execute();
		}
	}

	public void UploaddPdf(String filename, boolean isEdit, int app_id,
			int pdf_id, UploadDelegate delegate) {
		String filepath = "attachment_" + app_id + "_" + pdf_id + ".pdf";
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceHelper.URL);
		if (isEdit) {
			sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
					+ ServiceHelper.ATTACHMENTS + "/" + pdf_id + "?");
		} else {
			sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
					+ ServiceHelper.ATTACHMENTS + "?");
		}
		sb.append("api_key=");
		sb.append(Account.getkey());
		String url = sb.toString();
		if (isFileExist(getStoragePath(filepath))) {
			Upload(filename, isEdit, app_id, url, pdf_id, delegate);
		} else {
			return;
		}
	}

	private void Upload(String filename, boolean isEdit, int app_id,
			String url, int pdf_id, UploadDelegate delegate) {
		// Downloader downloader = new Downloader(app_id, url);
		// downloader.execute();
		Uploader upload = new Uploader(filename, isEdit, app_id, url, pdf_id,
				delegate);
		upload.execute();
	}

	public static boolean isFileExist(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private class Downloader extends AsyncTask<Void, Void, Boolean> {

		String localFilePath = "";
		String URL;
		int Appointment_id;
		DownLoadDelegate m_delegate;
		int pdf_id;

		public Downloader(int app_id, int p, String url,
				DownLoadDelegate delegate) {
			URL = url;
			Appointment_id = app_id;
			m_delegate = delegate;
			pdf_id = p;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
			String downloadUrl = "";
			String filename = "";
			downloadUrl = URL;
			filename = "pdf_form_" + Appointment_id + "_" + pdf_id + ".pdf";
			downloadUrl = downloadUrl.replace("https", "http");
			// result = CallTemp(downloadUrl, localFilePath, filename);
			try {
				URL url = new URL(downloadUrl);
				URLConnection connection = url.openConnection();
				connection.connect();
				InputStream in = connection.getInputStream();
				localFilePath = getStoragePath(filename);

				int totalSize = connection.getContentLength();
				File file = new File(localFilePath);
				FileOutputStream fileOutput = new FileOutputStream(file);
				int downloadedSize = 0;
				byte[] buffer = new byte[1024];
				int bufferLength = 0; // used to store a temporary size of the
										// buffer
				while ((bufferLength = in.read(buffer)) > 0) {
					fileOutput.write(buffer, 0, bufferLength);
					downloadedSize += bufferLength;
					int progress = (int) (downloadedSize * 100 / totalSize);
				}
				fileOutput.close();
				result = true;
				String attachment = "attachment_" + Appointment_id + "_"
						+ pdf_id + ".pdf";
				String ltemp = getStoragePath(attachment);
				File attachmentfile = new File(ltemp);
				File dst = new File(attachmentfile.toString());
				Utils.Instance().copy(file, dst);
			} catch (Exception ex) {
				Utils.LogInfo(ex.toString());
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// DataDownloaderDelegate delegate = m_downloadQueue.get(m_info);
			if (result) {
				if (m_downloadQueue.containsKey(URL)) {
					m_downloadQueue.remove(URL);
				}
			}
			if (m_downloadQueue.size() > 0) {
				for (String rec : m_downloadQueue.keySet()) {
					String temp[] = rec.split("work_orders/");
					String id[] = temp[1].split("/");
					int app_id = Utils.ConvertToInt(id[0]);
					// Downloader downloader = new Downloader(app_id, ,rec,
					// null);
					// downloader.execute();
					break;
				}
			}
			if (result) {
				if (m_delegate != null)
					m_delegate.DownLoadSuccessFully("DownLoad Completed");
			} else {
				if (m_delegate != null)
					m_delegate.DownLoadFailed("DownLoad Failed");
			}
			super.onPostExecute(result);
		}

	}

	private class Uploader extends AsyncTask<Void, Void, String> {

		String URL;
		int Appointment_id;
		UploadDelegate delegate;
		int p_id = 0;
		boolean isEdited = false;
		String name = "";

		public Uploader(String filename, boolean isEdit, int app_id,
				String url, int pdf_id, UploadDelegate del) {
			URL = url;
			delegate = del;
			Appointment_id = app_id;
			p_id = pdf_id;
			isEdited = isEdit;
			name = filename;
		}

		@Override
		protected String doInBackground(Void... params) {
			StringBuffer responseBody = new StringBuffer();

			HttpClient client = getNewHttpClient();
			client.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpRequestBase request = null;
			if (isEdited) {
				request = new HttpPut(URL);
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				String filename = "attachment_" + Appointment_id + "_" + p_id
						+ ".pdf";
				File file1 = new File(getStoragePath(filename));
				// String original_name = filename.replace("_" + Appointment_id,
				// "");
				// File to = new File(getStoragePath(original_name));
				// file1.renameTo(to);
				entity.addPart(name, new FileBody((file1), "application/pdf"));
				((HttpPut) request).setEntity(entity);
			} else {
				request = new HttpPost(URL);
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				String filename = "attachment_" + Appointment_id + "_" + p_id
						+ ".pdf";
				File file1 = new File(getStoragePath(filename));
				// String original_name = filename.replace("_" + Appointment_id,
				// "");
				// File to = new File(getStoragePath(original_name));
				// file1.renameTo(to);
				// PdfFormsInfo pdfInfo = PdfFormsInfo.getPdfFormsByPdfId(p_id);
				entity.addPart(name, new FileBody((file1), "application/pdf"));
				((HttpPost) request).setEntity(entity);
			}
			BufferedReader bs = null;
			try {
				HttpEntity hEntity = client.execute(request).getEntity();
				bs = new BufferedReader(new InputStreamReader(
						hEntity.getContent()));
				String s = "";
				while (s != null) {
					responseBody.append(s);
					s = bs.readLine();
				}
				bs.close();
				// Utils.LogInfo("JSON---->>>>" + responseBody.toString());

			} catch (IOException ioe) {
				ioe.printStackTrace();
				responseBody.append("");
				return "";
			}

			return responseBody.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			// DataDownloaderDelegate delegate = m_downloadQueue.get(m_info);
			super.onPostExecute(result);

			if (result.length() > 0) {
				if (delegate != null) {
					delegate.UploadSuccessFully(result, isEdited, name);
				}

			} else {
				if (delegate != null) {
					delegate.UploadFailed("File Upload Failed.");
				}
			}
		}
	}

	public static boolean isSDCARDMounted() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	public static String getStoragePath(String filename) {
		String path = "";
		if (isSDCARDMounted()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		path = path + File.separator + Const.DIRECTORY_NAME;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}

		path = path + File.separator + filename;

		return path;
	}

	public class NullHostNameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
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

	public static int copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] stuff = new byte[1024];
		int read = 0;
		int total = 0;
		while ((read = input.read(stuff)) != -1) {
			output.write(stuff, 0, read);
			total += read;
		}
		return total;
	}

	private class AttachmentDownloader extends AsyncTask<Void, Void, Boolean> {

		String localFilePath = "";
		String URL;
		int Appointment_id;
		DownLoadDelegate m_delegate;
		int attach_id;

		public AttachmentDownloader(int app_id, int p, String url,
				DownLoadDelegate delegate) {
			URL = url;
			Appointment_id = app_id;
			m_delegate = delegate;
			attach_id = p;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
			String downloadUrl = "";
			String filename = "";
			downloadUrl = URL;
			filename = "attachment_" + Appointment_id + "_" + attach_id
					+ ".pdf";
			downloadUrl = downloadUrl.replace("https", "http");
			// result = CallTemp(downloadUrl, localFilePath, filename);
			try {
				URL url = new URL(downloadUrl);
				URLConnection connection = url.openConnection();
				connection.connect();
				InputStream in = connection.getInputStream();
				localFilePath = getStoragePath(filename);

				int totalSize = connection.getContentLength();
				File file = new File(localFilePath);
				FileOutputStream fileOutput = new FileOutputStream(file);
				int downloadedSize = 0;
				byte[] buffer = new byte[1024];
				int bufferLength = 0; // used to store a temporary size of the
										// buffer
				while ((bufferLength = in.read(buffer)) > 0) {
					fileOutput.write(buffer, 0, bufferLength);
					downloadedSize += bufferLength;
					int progress = (int) (downloadedSize * 100 / totalSize);
				}
				fileOutput.close();
				result = true;
			} catch (Exception ex) {
				Utils.LogInfo(ex.toString());
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// DataDownloaderDelegate delegate = m_downloadQueue.get(m_info);
			if (result) {
				if (m_downloadQueue.containsKey(URL)) {
					m_downloadQueue.remove(URL);
				}
			}
			if (m_downloadQueue.size() > 0) {
				for (String rec : m_downloadQueue.keySet()) {
					String temp[] = rec.split("work_orders/");
					String id[] = temp[1].split("/");
					int app_id = Utils.ConvertToInt(id[0]);
					// Downloader downloader = new Downloader(app_id, ,rec,
					// null);
					// downloader.execute();
					break;
				}
			}
			if (result) {
				if (m_delegate != null)
					m_delegate.DownLoadSuccessFully("DownLoad Completed");
			} else {
				if (m_delegate != null)
					m_delegate.DownLoadFailed("DownLoad Failed, please check internet connection");
			}
			super.onPostExecute(result);
		}

	}

	public static void RenameFile(String newpath, String oldpath) {
		File from = new File(newpath);
		File to = new File(oldpath);
		from.renameTo(to);
	}
	
	
	public static void syncUpload(String filename, int app_id, final int p_id,
			boolean isEdit) {
		if (NetworkConnectivity.isConnected()) {
			DownloadPdf.Instance().UploaddPdf(filename, isEdit, app_id, p_id,
					new UploadDelegate() {

						@Override
						public void UploadSuccessFully(String result,
								boolean isEdited, String name) {
							if (result.length() > 0) {
								if (!isEdited) {
									AttachmentsInfo temp = AttachmentsInfo
											.getPdfFormsByfilename1(name);
									if (temp != null) {
										if (result.toString() != null
												&& result.toString().length() > 0) {
											try {
												JSONObject main = new JSONObject(
														result.toString());
												if (main.has("attachment")) {
													JSONObject obj = main
															.getJSONObject("attachment");
													temp.id = main.optInt("id");
													temp.attached_pdf_form_file_name = obj
															.optString("attached_pdf_form_file_name");
													temp.attached_pdf_form_content_type = obj
															.optString("attached_pdf_form_content_type");
													temp.attached_pdf_form_file_size = obj
															.optInt("attached_pdf_form_file_size");
													temp.save();
												}

											} catch (JSONException e) {
												e.printStackTrace();
											} catch (ActiveRecordException e) {
												e.printStackTrace();
											}
										}
										String filepath = "attachment_" + p_id
												+ ".pdf";
										if (isFileExist(filepath)) {
											RenameFile(
													getStoragePath("attachment_"
															+ temp.id + ".pdf"),
													getStoragePath(filepath));
										}
									}
								}
							}
						}

						@Override
						public void UploadFailed(String error) {

						}
					});
		}
	}

}
