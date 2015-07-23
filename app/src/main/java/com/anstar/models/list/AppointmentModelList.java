package com.anstar.models.list;

import android.os.AsyncTask;

import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.DownloadPdf;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.TrapScanningInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentModelList implements ServiceHelperDelegate {

	private AppointmentModelList() {

	}

	private static volatile AppointmentModelList _instance = null;

	// private static volatile int cat_id = 0;

	public static AppointmentModelList Instance() {
		if (_instance == null) {
			synchronized (AppointmentModelList.class) {
				_instance = new AppointmentModelList();
				_instance.m_modelList = new ArrayList<AppointmentInfo>();
			}
		}
		return _instance;
	}

	protected ArrayList<AppointmentInfo> m_modelList = null;
	private ModelDelegate<AppointmentInfo> m_delegate = null;

	public void load(ModelDelegate<AppointmentInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null || m_modelList.size() <= 0) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.WORK_ORDERS);
				helper.call(this);
			} else {
				m_delegate.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
			}
		} else {
			m_delegate.ModelLoaded(m_modelList);
		}
	}

	public void ClearDB() {
		try {
			getApoointments();
			if (m_modelList != null) {
				for (AppointmentInfo app : m_modelList) {
					if (app != null) {

						// service_report.pdf?api_key=efe5059067b8729ff863aad530a6139626159f17755cd73d
						String filepath = "service_report" + app.id + ".pdf";
						File file = new File(DownloadPdf.Instance()
								.getStoragePath(filepath));
						boolean deleted = file.delete();
					}
				}
			}
			FieldworkApplication.Connection().delete(AppointmentInfo.class);
			InvoiceList.Instance().ClearDB();
			MaterialUsagesList.Instance().ClearDB();
			TargetPestList.Instance().ClearDB();
			InspectionList.Instance().ClearDB();
			TrapList.Instance().ClearDB();
			PaymentsList.Instance().ClearDB();
			LineItemsList.Instance().ClearDB();
			PdfFormsList.Instance().ClearDB();
			AttachmentsList.Instance().ClearDB();
			PhotoAttachmentsList.Instance().ClearDB();
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public ArrayList<AppointmentInfo> getApoointments() {
		loadFromDB();
		return m_modelList;
	}

	public void loadFromDB() {
		try {
			List<AppointmentInfo> list = FieldworkApplication.Connection()
					.findAll(AppointmentInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<AppointmentInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	@Override
	public void CallFinish(ServiceResponse res) {
		if (!res.isError()) {
			try {
				Utils.LogInfo("Reponse get for appointment.....");
				ClearDB();
				// AppointmentInfo ainfo = new AppointmentInfo();
				// ainfo.storeWorkOrderResponse(res, true);
				m_modelList = new ArrayList<AppointmentInfo>();
				JSONArray subjectList = new JSONArray(res.RawResponse);
				ArrayList<Integer> custids = new ArrayList<Integer>();
				for (int i = 0; i < subjectList.length(); i++) {
					JSONObject data = subjectList.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<AppointmentInfo> mapper = new ModelMapHelper<AppointmentInfo>();
						AppointmentInfo info = mapper.getObject(
								AppointmentInfo.class, data);
						// if (info.has_attached_form) {

						// }
						if (!custids.contains(info.customer_id)) {
							custids.add(info.customer_id);
							CustomerInfo customer = CustomerList.Instance()
									.getCustomerById(info.customer_id);
							if (customer != null) {
								customer.RetriveData(null);
							} else {
								CustomerInfo cust = FieldworkApplication
										.Connection().newEntity(
												CustomerInfo.class);
								cust.id = info.customer_id;
								// CustomerInfo cust = CustomerList.Instance()
								// .getCustomerById(info.customer_id);
								// if (cust == null) {
								// cust.id = info.customer_id;
								// }
								cust.RetriveData(null);
							}
							try {
								TrapList.Instance().load(
										new ModelDelegate<TrapScanningInfo>() {

											@Override
											public void ModelLoaded(
													ArrayList<TrapScanningInfo> list) {
											}

											@Override
											public void ModelLoadFailedWithError(
													String error) {
											}
										}, info.customer_id,
										info.service_location_id);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						InspectionList.Instance().parseInspectionRecords(data,
								info.id);
						InvoiceList.Instance().parseInvoice(data, info.id);
						MaterialUsagesList.Instance().parseMatrialUsages(data,
								info.id, false);
						TargetPestList.Instance().parseTargetPests(data,
								info.id);
						LineItemsList.Instance().parseLineItems(data, info.id);
						AttachmentsList.Instance().parseAttachments(data,
								info.id);
						PhotoAttachmentsList.Instance().parseAttachments(data,
								info.id);
						PdfFormsList.Instance().parsePdfForms(data, info.id);
						Utils.LogInfo("appointment parsed id :: " + info.id);
						if (info != null) {
							info.save();
							m_modelList.add(info);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			loadAsync load = new loadAsync(m_delegate);
			load.execute();
		} else {
			m_delegate.ModelLoadFailedWithError(res.getErrorMessage());
		}
	}

	public class loadAsync extends AsyncTask<Void, Void, Void> {

		ModelDelegate<AppointmentInfo> del;

		public loadAsync(ModelDelegate<AppointmentInfo> delegate) {
			del = delegate;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Utils.LogInfo("after parsing all appointments.....");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (del != null) {
				del.ModelLoaded(m_modelList);
			}
		}
	}

	@Override
	public void CallFailure(String ErrorMessage) {
		m_delegate.ModelLoadFailedWithError(ErrorMessage);
	}

	public ArrayList<AppointmentInfo> getAppointmentBydate(String date) {
		ArrayList<AppointmentInfo> m_list = new ArrayList<AppointmentInfo>();
		for (AppointmentInfo appointmentInfo : m_modelList) {
			String d[] = appointmentInfo.starts_at.split("T");
			if (d[0].equalsIgnoreCase(date)) {
				m_list.add(appointmentInfo);
			}
		}
		return m_list;
	}

	public ArrayList<AppointmentInfo> getAppointmentBydate(Date date) {
		ArrayList<AppointmentInfo> m_list = new ArrayList<AppointmentInfo>();
		if (m_modelList != null) {
			for (AppointmentInfo appointmentInfo : m_modelList) {
				if (Utils.isSameDate(appointmentInfo.getStartAtDate(), date)) {
					m_list.add(appointmentInfo);
				}
			}
		}
		return m_list;
	}

	// public boolean hasNextDateRecords(Date date) {
	// Date newdate = new Date((date.getTime() + (24 * 60 * 60 * 1 * 1000)));
	// return getAppointmentBydate(newdate).size() > 0;
	// }

	public ArrayList<AppointmentInfo> getAppointmentFromToday(Date today) {
		ArrayList<AppointmentInfo> m_list = new ArrayList<AppointmentInfo>();
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (AppointmentInfo appointmentInfo : m_modelList) {
				String d[] = appointmentInfo.starts_at.split("T");
				Date date = Utils.ConvertToDate(d[0]);
				if (date.after(today)) {
					m_list.add(appointmentInfo);
				}
			}
		}
		return m_list;
	}

	public AppointmentInfo getAppointmentById(int id) {
		if (m_modelList == null || m_modelList.size() == 0) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (AppointmentInfo appointmentInfo : m_modelList) {
				if (appointmentInfo.id == id) {
					return appointmentInfo;
				}
			}
		}
		return null;
	}
}
