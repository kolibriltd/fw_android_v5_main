package com.anstar.fieldwork;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.Database;
import com.anstar.activerecords.DatabaseBuilder;
import com.anstar.common.Const;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.models.Account;
import com.anstar.models.ApplicationDeviceTypeInfo;
import com.anstar.models.ApplicationMethodInfo;
import com.anstar.models.AppointmentConditionsInfo;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.AttachmentsInfo;
import com.anstar.models.BaitConditionsInfo;
import com.anstar.models.BillingTermsInfo;
import com.anstar.models.CustomerContactInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.DeviceTypesInfo;
import com.anstar.models.DilutionInfo;
import com.anstar.models.InspectionInfo;
import com.anstar.models.InspectionMaterial;
import com.anstar.models.InspectionPest;
import com.anstar.models.InvoiceInfo;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.LocationInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.MaterialUsageTargetPestInfo;
import com.anstar.models.MeasurementInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.PaymentInfo;
import com.anstar.models.PdfFormsInfo;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.PhotoAttachmentsInfo;
import com.anstar.models.RecomendationInfo;
import com.anstar.models.ServiceLocationContactInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.ServiceRoutesInfo;
import com.anstar.models.ServicesInfo;
import com.anstar.models.StatusInfo;
import com.anstar.models.TargetPestInfo;
import com.anstar.models.TaxRates;
import com.anstar.models.TempLocation;
import com.anstar.models.TrapConditionsInfo;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.TrapTypesInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.WorkHistroyInfo;
import com.anstar.models.list.ApplicationDeviceTypeList;
import com.anstar.models.list.BaitConditionsList;
import com.anstar.models.list.BillingTermsList;
import com.anstar.models.list.DeviceTypesList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.LocationInfoList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.ServiceRoutesList;
import com.anstar.models.list.ServicesList;
import com.anstar.models.list.StatusList;
import com.anstar.models.list.TaxRateList;
import com.anstar.models.list.TrapConditionsList;
import com.anstar.models.list.TrapTypesList;
import com.kl.kitlocate.interfaces.KLLocation;
import com.kl.kitlocate.interfaces.KitLocate;

public class FieldworkApplication extends Application {

	private static FieldworkApplication _intance = null;
	private static ActiveRecordBase mDatabase;
	public static String LOAD_ALL_NOTIFICATION_KEY = "LOAD_ALL_NOTIFICATION_KEY";

	private HashMap<String, Object> m_appStoredData = new HashMap<String, Object>();

	@Override
	public void onCreate() {

		super.onCreate();
		KitLocate.initKitLocate(getApplicationContext(),
				"a2fd652b-7914-446d-a233-2b1131adf76c",
				KitLocateCallBackClass.class);
		KLLocation.registerPeriodicLocation(getApplicationContext());
		KLLocation.setPeriodicMinimumTimeInterval(this, 30);
		DatabaseBuilder builder = new DatabaseBuilder(Const.DATABASE_NAME);
		builder.addClass(Account.class);
		builder.addClass(AppointmentInfo.class);
		builder.addClass(CustomerInfo.class);
		builder.addClass(MaterialInfo.class);
		builder.addClass(MaterialUsage.class);
		builder.addClass(MaterialUsageRecords.class);
		builder.addClass(PestsTypeInfo.class);
		builder.addClass(InvoiceInfo.class);
		builder.addClass(TargetPestInfo.class);
		builder.addClass(LocationInfo.class);
		builder.addClass(LocationAreaInfo.class);
		builder.addClass(StatusInfo.class);
		builder.addClass(MeasurementInfo.class);
		builder.addClass(DilutionInfo.class);
		builder.addClass(ApplicationMethodInfo.class);
		builder.addClass(TempLocation.class);
		builder.addClass(TrapScanningInfo.class);
		builder.addClass(InspectionInfo.class);
		builder.addClass(InspectionPest.class);
		builder.addClass(UserInfo.class);
		builder.addClass(InspectionMaterial.class);
		builder.addClass(PaymentInfo.class);
		builder.addClass(DeviceTypesInfo.class);
		builder.addClass(BaitConditionsInfo.class);
		builder.addClass(TrapTypesInfo.class);
		builder.addClass(TrapConditionsInfo.class);
		builder.addClass(BillingTermsInfo.class);
		builder.addClass(ServiceLocationsInfo.class);
		builder.addClass(ServiceRoutesInfo.class);
		builder.addClass(MaterialUsageTargetPestInfo.class);
		builder.addClass(CustomerContactInfo.class);
		builder.addClass(ServiceLocationContactInfo.class);
		builder.addClass(LineItemsInfo.class);
		builder.addClass(PdfFormsInfo.class);
		builder.addClass(AttachmentsInfo.class);
		builder.addClass(TaxRates.class);
		builder.addClass(ServicesInfo.class);
		builder.addClass(RecomendationInfo.class);
		builder.addClass(AppointmentConditionsInfo.class);
		builder.addClass(WorkHistroyInfo.class);
		builder.addClass(ApplicationDeviceTypeInfo.class);
		builder.addClass(PhotoAttachmentsInfo.class);
		Database.setBuilder(builder);
		try {
			mDatabase = ActiveRecordBase.open(this, Const.DATABASE_NAME,
					Const.DATABASE_VERSION);
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Utils.LogException(e);
		}
		NotificationCenter.Instance().addObserver(FieldworkApplication.class,
				LOAD_ALL_NOTIFICATION_KEY, "loadAll", null);
	}

	public FieldworkApplication() {
		_intance = this;
	}

	public static ActiveRecordBase Connection() {
		return mDatabase;
	}

	public static Context getContext() {
		return _intance;
	}

	public Object getStoredObject(String key) {
		if (m_appStoredData.containsKey(key)) {
			return m_appStoredData.get(key);
		}
		return null;
	}

	public void storeObject(String key, Object data) {
		if (m_appStoredData.containsKey(key)) {
			this.removeStoredObject(key);
		}
		m_appStoredData.put(key, data);
	}

	public void removeStoredObject(String key) {
		if (m_appStoredData.containsKey(key)) {
			m_appStoredData.remove(key);
		}
	}

	public void loadAll() {
		if (Account.getkey().length() <= 0) {
			return;
		}
		try {
			UserInfo.Instance().load(new ModelDelegate<UserInfo>() {

				@Override
				public void ModelLoaded(ArrayList<UserInfo> list) {

				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			DilutionRatesList.Instance().load(
					new ModelDelegate<DilutionInfo>() {
						@Override
						public void ModelLoaded(ArrayList<DilutionInfo> list) {
							Toast.makeText(getApplicationContext(), "done", 1)
									.show();
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			PestTypeList.Instance().load(new ModelDelegate<PestsTypeInfo>() {

				@Override
				public void ModelLoaded(ArrayList<PestsTypeInfo> list) {
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			TaxRateList.Instance().load(new ModelDelegate<TaxRates>() {
				@Override
				public void ModelLoaded(ArrayList<TaxRates> list) {
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ApplicationDeviceTypeList.Instance().load(
					new ModelDelegate<ApplicationDeviceTypeInfo>() {
						@Override
						public void ModelLoaded(
								ArrayList<ApplicationDeviceTypeInfo> list) {
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ServicesList.Instance().load(new ModelDelegate<ServicesInfo>() {
				@Override
				public void ModelLoaded(ArrayList<ServicesInfo> list) {

				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ServiceRoutesList.Instance().load(
					new ModelDelegate<ServiceRoutesInfo>() {
						@Override
						public void ModelLoaded(
								ArrayList<ServiceRoutesInfo> list) {
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			DeviceTypesList.Instance().load(
					new ModelDelegate<DeviceTypesInfo>() {

						@Override
						public void ModelLoaded(ArrayList<DeviceTypesInfo> list) {
						}

						@Override
						public void ModelLoadFailedWithError(String error) {
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			BaitConditionsList.Instance().load(
					new ModelDelegate<BaitConditionsInfo>() {

						@Override
						public void ModelLoaded(
								ArrayList<BaitConditionsInfo> list) {
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			TrapConditionsList.Instance().load(
					new ModelDelegate<TrapConditionsInfo>() {

						@Override
						public void ModelLoaded(
								ArrayList<TrapConditionsInfo> list) {
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			TrapTypesList.Instance().load(new ModelDelegate<TrapTypesInfo>() {

				@Override
				public void ModelLoaded(ArrayList<TrapTypesInfo> list) {
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			LocationInfoList.Instance().load(new ModelDelegate<LocationInfo>() {

				@Override
				public void ModelLoaded(ArrayList<LocationInfo> list) {
					Toast.makeText(getApplicationContext(), "done", 1).show();
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			StatusList.Instance().load(new ModelDelegate<StatusInfo>() {

				@Override
				public void ModelLoaded(ArrayList<StatusInfo> list) {
					Toast.makeText(getApplicationContext(), "done", 1).show();
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			MeasurementInfo
					.getMeasurements(new ModelDelegate<MeasurementInfo>() {

						@Override
						public void ModelLoaded(ArrayList<MeasurementInfo> list) {
							Toast.makeText(getApplicationContext(), "done", 1)
									.show();
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
		} catch (Exception e) {
		}
		try {

			BillingTermsList.Instance().load(
					new ModelDelegate<BillingTermsInfo>() {

						@Override
						public void ModelLoaded(ArrayList<BillingTermsInfo> list) {
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ApplicationMethodInfo
					.getMeasurements(new ModelDelegate<ApplicationMethodInfo>() {

						@Override
						public void ModelLoaded(
								ArrayList<ApplicationMethodInfo> list) {
							Toast.makeText(getApplicationContext(), "done", 1)
									.show();
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	// upload pest type, location, material,
}
