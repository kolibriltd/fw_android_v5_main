package com.anstar.models;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.MySSLSocketFactory;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.CommonDelegate;
import com.anstar.models.list.PhotoAttachmentsList;

public class PhotoAttachmentsInfo extends ActiveRecordBase {
	public PhotoAttachmentsInfo() {

	}

	public interface UploadDelegate {

		public void UploadSuccessFully(String message, boolean isedited,
				String name);

		public void UploadFailed(String error);

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "attachment_file_name")
	public String attachment_file_name = "";
	@ModelMapper(JsonKey = "attachment_content_type")
	public String attachment_content_type = "";
	@ModelMapper(JsonKey = "comments")
	public String comments = "";
	@ModelMapper(JsonKey = "appointment_occurrence_id")
	public int appointment_occurrence_id = 0;
	@ModelMapper(JsonKey = "attachment_file_size")
	public int attachment_file_size = 0;

	public boolean isDeleted = false;
	public boolean isEdit = false;

	public static ArrayList<PhotoAttachmentsInfo> getSyncAttachmentsByWorkerId(
			int w_id) {

		ArrayList<PhotoAttachmentsInfo> m_list = new ArrayList<PhotoAttachmentsInfo>();

		try {
			// List<PhotoAttachmentsInfo> lst =
			// FieldworkApplication.Connection()
			// .find(PhotoAttachmentsInfo.class,
			// CamelNotationHelper
			// .toSQLName("appointment_occurrence_id")
			// + "=? and ("
			// + CamelNotationHelper.toSQLName("id")
			// + " < 0 or"
			// + CamelNotationHelper.toSQLName("isEdit")
			// + " = ?)",
			// new String[] { String.valueOf(w_id), String.valueOf(true) });
			List<PhotoAttachmentsInfo> lst = FieldworkApplication.Connection()
					.find(PhotoAttachmentsInfo.class,
							CamelNotationHelper
									.toSQLName("appointment_occurrence_id")
									+ "=?",
							new String[] { String.valueOf(w_id) });
			if (lst != null && lst.size() > 0) {
				m_list = new ArrayList<PhotoAttachmentsInfo>(lst);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public static ArrayList<PhotoAttachmentsInfo> getDeletedAttachmentsByWorkerId(
			int w_id) {

		ArrayList<PhotoAttachmentsInfo> m_list = new ArrayList<PhotoAttachmentsInfo>();

		try {
			List<PhotoAttachmentsInfo> lst = FieldworkApplication.Connection()
					.find(PhotoAttachmentsInfo.class,
							CamelNotationHelper
									.toSQLName("appointment_occurrence_id")
									+ "=? and "
									+ CamelNotationHelper
											.toSQLName("isDeleted") + " = ?",
							new String[] { String.valueOf(w_id),
									String.valueOf(true) });
			if (lst != null && lst.size() > 0) {
				m_list = new ArrayList<PhotoAttachmentsInfo>(lst);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public static PhotoAttachmentsInfo getPhotoAttachemtByID(int id) {
		try {
			List<PhotoAttachmentsInfo> lst = FieldworkApplication.Connection()
					.find(PhotoAttachmentsInfo.class,
							CamelNotationHelper.toSQLName("id") + "=?",
							new String[] { String.valueOf(id) });
			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deletePhoto(final CommonDelegate delegate) {
		if (NetworkConnectivity.isConnected()) {
			String json = "{\"photo_attachments_attributes\":[{\"id\":%d, \"_destroy\":true}]}";
			json = String.format(json, this.id);
			String url = String.format("work_orders/%d",
					this.appointment_occurrence_id);
			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.PUT, json);
			caller.startRequest(new ServiceHelperDelegate() {

				@Override
				public void CallFinish(ServiceResponse res) {
					if (!res.isError) {
						try {
							PhotoAttachmentsInfo.this.delete();
							delegate.UpdateSuccessFully(true);
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void CallFailure(String ErrorMessage) {
					delegate.UpdateFail(ErrorMessage);
				}
			});
		} else {
			try {
				// this.id = Utils.getRandomInt();
				if (this.id < 0) {
					this.delete();
				} else {
					this.isDeleted = true;
					this.save();
				}
				delegate.UpdateSuccessFully(true);
				AppointmentInfo.updateDirtyFlag(this.appointment_occurrence_id);
			} catch (ActiveRecordException e) {
				e.printStackTrace();
			}
		}
	}

	public void UploadPhoto(int app_id, boolean isEdit, Bitmap bitmap,
			UploadDelegate delegate) {
		if (NetworkConnectivity.isConnected()) {
			StringBuilder sb = new StringBuilder();
			sb.append(ServiceHelper.URL);
			if (isEdit) {
				sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
						+ ServiceHelper.PHOTO_ATTACHMENTS + "/" + this.id + "?");
			} else {
				sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
						+ ServiceHelper.PHOTO_ATTACHMENTS + "?");
			}
			sb.append("api_key=");
			sb.append(Account.getkey());
			String url = sb.toString();
			// Upload(filepath, isEdit, app_id, url, notes, delegate);

			Uploader upload = new Uploader(this, isEdit, app_id, url, delegate);
			upload.execute();
		} else {
			try {
				if (isEdit) {
					if (bitmap != null) {
						PhotoAttachmentsList.Instance().saveToInternalSorage(
								bitmap, app_id, this.id);
					}
					if (this.id > 0)
						this.isEdit = true;

					this.save();

				} else {
					PhotoAttachmentsInfo info = FieldworkApplication
							.Connection().newEntity(PhotoAttachmentsInfo.class);
					info.id = Utils.getRandomInt();
					info.attachment_file_name = this.attachment_file_name;
					info.appointment_occurrence_id = app_id;
					info.comments = this.comments;
					info.save();
					if (bitmap != null) {
						PhotoAttachmentsList.Instance().saveToInternalSorage(
								bitmap, app_id, info.id);
					}
				}
				delegate.UploadSuccessFully("", isEdit,
						this.attachment_file_name);
				AppointmentInfo.updateDirtyFlag(app_id);
			} catch (ActiveRecordException e) {
				e.printStackTrace();
			}
		}
	}

	// [{"attachment_content_type":"image/jpeg","attachment_file_name":"RackMultipart20150129-23114-g4ocq0","attachment_file_size":40309,"comments":""}]
	private class Uploader extends AsyncTask<Void, Void, String> {

		String URL;
		int Appointment_id;
		UploadDelegate delegate;
		boolean isEdited = false;
		PhotoAttachmentsInfo pinfo;

		public Uploader(PhotoAttachmentsInfo info, boolean isEdit, int app_id,
				String url, UploadDelegate del) {
			URL = url;
			delegate = del;
			Appointment_id = app_id;
			isEdited = isEdit;
			pinfo = info;
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = "";
			try {
				if (isEdit)
					response = uploadFile(URL, pinfo.comments, pinfo.attachment_file_name, true);
				else
					response = uploadFile(URL, pinfo.comments, pinfo.attachment_file_name, false);
				
//				MultipartUtility multipart;
//				if (isEdited)
//					multipart = new MultipartUtility(URL, charset, "PUT");
//				else
//					multipart = new MultipartUtility(URL, charset, "POST");
//				multipart.addHeaderField("User-Agent", "CodeJava");
//				multipart.addHeaderField("Test-Header", "Header-Value");
//
//				multipart.addFormField("photo_attachment[comments]",
//						pinfo.comments);
//				String path = pinfo.attachment_file_name;
//				if (path.length() > 0) {
//					Utils.LogInfo("image path :: " + path);
//					if (!path.contains("http")) {
//						File sourceFile = new File(path);
//						multipart.addFilePart("image", sourceFile);
//					}
//				}
//				response = multipart.finish();
//				Log.i("mFood", response.toString());
//				if (response != null)
//					ret = response.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(final String result) {
			super.onPostExecute(result);

			if (result.length() > 0) {
				if (delegate != null) {
					if (result.length() > 0) {
						try {
//							JSONArray main = new JSONArray(result.toString());
							PhotoAttachmentsInfo info;
							JSONObject obj = new JSONObject(result);
							obj = obj.getJSONObject("photo_attachment");
							int appid = obj.optInt("appointment_occurrence_id");
							int photoid = obj.optInt("id");
							if (isEdited) {
								if (!this.isEdited) {
									pinfo.id = photoid;
									pinfo.isEdit = false;
									pinfo.attachment_file_name = obj
											.optString("attached_pdf_form_file_name");
									pinfo.attachment_content_type = obj
											.optString("attached_pdf_form_content_type");
									pinfo.attachment_file_size = obj
											.optInt("attached_pdf_form_file_size");
									pinfo.appointment_occurrence_id = appid;
									pinfo.comments = obj.optString("comments");
									pinfo.save();
								}
							} else {
								info = FieldworkApplication.Connection()
										.newEntity(PhotoAttachmentsInfo.class);
								info.id = photoid;
								info.attachment_file_name = obj
										.optString("attached_pdf_form_file_name");
								info.attachment_content_type = obj
										.optString("attached_pdf_form_content_type");
								info.attachment_file_size = obj
										.optInt("attached_pdf_form_file_size");
								info.appointment_occurrence_id = appid;
								info.comments = obj.optString("comments");
								info.save();
							}
							String url = "";
							StringBuilder sb = new StringBuilder();
							sb.append(ServiceHelper.URL);
							sb.append(ServiceHelper.WORK_ORDERS + "/" + appid
									+ "/" + ServiceHelper.PHOTO_ATTACHMENTS);
							url = sb.toString();
							String imasgeurl = url + "/" + photoid
									+ "/download" + "?api_key="
									+ Account.getkey();
							PhotoAttachmentsList.Instance().downloadfile(appid,
									photoid, imasgeurl, new CommonDelegate() {
										@Override
										public void UpdateSuccessFully(boolean b) {
											delegate.UploadSuccessFully(result,
													isEdited,
													pinfo.attachment_file_name);
										}

										@Override
										public void UpdateFail(
												String ErrorMessage) {

										}
									});

						} catch (JSONException e) {
							e.printStackTrace();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}
					}
				}

			} else {
				if (delegate != null) {
					delegate.UploadFailed("File Upload Failed.");
				}
			}
		}
	}

	public void uploadPhotoSync(int app_id, boolean isEdit) {
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceHelper.URL);
		if (isEdit) {
			sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
					+ ServiceHelper.PHOTO_ATTACHMENTS + "/" + this.id + "?");
		} else {
			sb.append(ServiceHelper.WORK_ORDERS + "/" + app_id + "/"
					+ ServiceHelper.PHOTO_ATTACHMENTS + "?");
		}
		sb.append("api_key=");
		sb.append(Account.getkey());
		String url = sb.toString();

		List<String> response = null;
		String charset = "UTF-8";
		try {

			if (isEdit)
				uploadFile(url, this.comments, this.attachment_file_name, true);
			else
				uploadFile(url, this.comments, this.attachment_file_name, false);
			// MultipartUtility multipart;
			// if (isEdit)
			// multipart = new MultipartUtility(url, charset, "PUT");
			// else
			// multipart = new MultipartUtility(url, charset, "POST");
			//
			//
			// multipart.addHeaderField("User-Agent", "CodeJava");
			// multipart.addHeaderField("Test-Header", "Header-Value");
			//
			// multipart.addFormField("photo_attachment[comments]",
			// this.comments);
			// String path = this.attachment_file_name;
			// if (path.length() > 0) {
			// if (!path.contains("http")) {
			// File sourceFile = new File(path);
			// multipart.addFilePart("image", sourceFile);
			// }
			// }
			// response = multipart.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String uploadFile(String upload_url, String comment, String filePath,
			boolean isedit) {
		Bitmap bitmapOrg = BitmapFactory.decodeFile(filePath);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 50, bao);
		byte[] data = bao.toByteArray();
//		Utils.LogInfo("Iamge size ::: "+data.length);
		String jsonString ="";
		try {
			HttpClient httpClient = getNewHttpClient();
			// Set Data and Content-type header for the image
			StringBody contentString = new StringBody(comment);
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			if (!filePath.contains("http")) {
				FileBody fb = new FileBody(new File(filePath), "image/jpeg");
				//entity.addPart("image", fb);
				entity.addPart("image", new ByteArrayBody(data,"image/jpeg", "test.jpg"));
			}
			entity.addPart("photo_attachment[comments]", contentString);
			HttpResponse response;
			if (isedit) {
				HttpPut postRequest = new HttpPut(upload_url);
				postRequest.setEntity(entity);
				response = httpClient.execute(postRequest);
			} else {
				HttpPost postRequest = new HttpPost(upload_url);
				postRequest.setEntity(entity);
				response = httpClient.execute(postRequest);
			}
			// Read the response
			jsonString = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			Log.e("Error in uploadFile", e.getMessage());
		}
		return jsonString;
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
