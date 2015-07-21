package com.anstar.internetbroadcast;

import java.util.ArrayList;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.AttachmentsInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.DownloadPdf;
import com.anstar.models.InspectionInfo;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.PhotoAttachmentsInfo;
import com.anstar.models.PhotoAttachmentsInfo.UploadDelegate;
import com.anstar.models.TargetPestInfo;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.LineItemsList;
import com.anstar.models.list.MaterialList;
import com.anstar.models.list.MaterialUsagesList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.TargetPestList;
import com.anstar.models.list.TrapList;

public class SyncHelper {

	private static volatile SyncHelper _instance = null;

	/**
	 * Get the Instance of the Utils Class
	 * 
	 * @return Utils Object
	 */
	public static SyncHelper Instance() {
		if (_instance == null) {
			synchronized (SyncHelper.class) {
				_instance = new SyncHelper();
			}
		}
		return _instance;
	}

	public void startSyncing() {
		NotificationCenter.Instance().postNotification("refresh",
				new Object[] { false });

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Utils.LogInfo("startSyncing   **********");
				MaterialInfo.sync1();
				PestsTypeInfo.sync1();
				LocationAreaInfo.sync1();
				TrapScanningInfo.sync1();
				startAppointmentSyncing();
				Utils.LogInfo("stop Syncing   **********");
				// startTrapSyncing();
				// startInspectionSyncing();
				// startLineItemSyncing();

			}
		});
		t.start();
	}

	public void startTargetPestSyncing() {
		ArrayList<AppointmentInfo> appt_list = AppointmentModelList.Instance()
				.getApoointments();
		if (appt_list != null) {
			for (AppointmentInfo appt : appt_list) {
				ArrayList<TargetPestInfo> tp_list = TargetPestList.Instance()
						.loadAll(appt.id);
				if (tp_list != null && tp_list.size() > 0) {
					for (TargetPestInfo tpInfo : tp_list) {
						if (tpInfo.id < 0) {
							PestsTypeInfo ptinfo = PestTypeList.Instance()
									.getPestById(tpInfo.pest_type_id);

							tpInfo.m_pest_info = ptinfo;
							tpInfo.sync();
						} else {
							if (tpInfo.isDeleted) {
								tpInfo.syncDelete();
							}
						}
					}
				}
			}
		}
	}

	public void startLineItemSyncing() {
		ArrayList<AppointmentInfo> appt_list = AppointmentModelList.Instance()
				.getApoointments();
		if (appt_list != null) {
			for (AppointmentInfo appt : appt_list) {
				ArrayList<LineItemsInfo> li_list = LineItemsList.Instance()
						.loadAll(appt.id);
				if (li_list != null && li_list.size() > 0) {
					for (LineItemsInfo liInfo : li_list) {
						if (liInfo.tempid < 0) {// in add , edit , delete tempid
												// < 0
							if (liInfo.id < 0) {// offline added record
								liInfo.syncAdd(true);
							} else {
								if (liInfo.isDeleted) {
									liInfo.syncDelete();
								} else
									liInfo.syncAdd(false);
							}
						}
					}
				}
			}
		}
	}

	public void startMaterialSyncing() {
		ArrayList<AppointmentInfo> appt_list = AppointmentModelList.Instance()
				.getApoointments();
		if (appt_list != null) {
			Utils.LogInfo("Start Material syncing :::: ");
			for (AppointmentInfo appt : appt_list) {
				ArrayList<MaterialUsage> usages = MaterialUsagesList.Instance()
						.loadAll(appt.id);
				if (usages != null && usages.size() > 0) {
					for (MaterialUsage m : usages) {
						if (m.id < 0 && m.isForInspection == false) {
							Utils.LogInfo("Material syncing :::: " + m.id);
							MaterialInfo m_info = MaterialList.Instance()
									.getMaterialById(m.material_id);
							if (m_info != null) {
								m.m_material_info = m_info;
								m.sync();
							}
						}
						if (m.isDeleted) {
							m.syncDelete();
						}
					}
				}
			}
		}
	}

	public void startTrapSyncing() {
		ArrayList<CustomerInfo> customers = CustomerList.Instance()
				.getAllCustomer();
		for (CustomerInfo c : customers) {
			ArrayList<TrapScanningInfo> traps = TrapList.Instance()
					.getTrapByCustomerId(c.id);
			for (TrapScanningInfo trap : traps) {
				if (trap.id < 0) {
					trap.syncTrap();
				}
			}
		}
	}

	public void startInspectionSyncing() {
		ArrayList<AppointmentInfo> appt_list = AppointmentModelList.Instance()
				.getApoointments();
		if (appt_list != null) {
			for (AppointmentInfo appt : appt_list) {
				ArrayList<InspectionInfo> m_inspections = InspectionList
						.Instance().loadAll(appt.id);
				if (m_inspections != null && m_inspections.size() > 0) {
					for (InspectionInfo i : m_inspections) {
						if (i.id < 0) {
							i.sync();
						}
						if (i.isDeleted) {
							i.syncDelete();
						}
					}
				}
			}
		}
	}

	public void startAppointmentSyncing() {
		ArrayList<AppointmentInfo> appt_list = AppointmentModelList.Instance()
				.getApoointments();
		Utils.LogInfo("startAppointmentSyncing ***********");
		if (appt_list != null) {
			for (final AppointmentInfo appt : appt_list) {
				// DownloadPdf.Instance().UploaddPdf(appt.id, null);
				if (appt.isdirty) {
//					if (appt.customer_sign_id < 0) {
//						appt.syncCustSignature(appt);
//					}
//					if (appt.Tech_sign_id < 0) {
//						appt.syncTechSignature(appt);
//					}
					String apptJson = appt.getAppointmentJson();
					ArrayList<PhotoAttachmentsInfo> photos = PhotoAttachmentsInfo
							.getSyncAttachmentsByWorkerId(appt.id);
					for (final PhotoAttachmentsInfo pic : photos) {
						if (pic.id < 0 || pic.isEdit) {
							pic.uploadPhotoSync(appt.id, pic.isEdit);
						}
					}
					ArrayList<AttachmentsInfo> m_list = AttachmentsInfo
							.getPdfFormsByWorkerId(appt.id);
					if (m_list != null && m_list.size() > 0) {
						for (AttachmentsInfo p : m_list) {
							if (p.id < 0) {
								DownloadPdf.syncUpload(
										p.attached_pdf_form_file_name, appt.id,
										p.pdf_id, false);
							} else {
								DownloadPdf.syncUpload(
										p.attached_pdf_form_file_name, appt.id,
										p.id, true);
							}
						}
					}
					appt.sync1(apptJson);
				}
			}
		}
		Utils.LogInfo("send refresh notification to active********");

		NotificationCenter.Instance().postNotification("refresh",
				new Object[] { true });

		NotificationCenter.Instance().postNotification("hide");
		NotificationCenter.Instance().postNotification("hidedash");
	}

}
