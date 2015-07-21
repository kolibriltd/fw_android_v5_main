package com.anstar.fieldwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.models.Account;
import com.anstar.models.ApplicationDeviceTypeInfo;
import com.anstar.models.ApplicationMethodInfo;
import com.anstar.models.AppointmentConditionsInfo;
import com.anstar.models.BaitConditionsInfo;
import com.anstar.models.BillingTermsInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.DeviceTypesInfo;
import com.anstar.models.DilutionInfo;
import com.anstar.models.LocationInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MeasurementInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.RecomendationInfo;
import com.anstar.models.ServiceRoutesInfo;
import com.anstar.models.ServicesInfo;
import com.anstar.models.StatusInfo;
import com.anstar.models.TaxRates;
import com.anstar.models.TrapConditionsInfo;
import com.anstar.models.TrapTypesInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.list.ApplicationDeviceTypeList;
import com.anstar.models.list.AppointmentConditionsList;
import com.anstar.models.list.BaitConditionsList;
import com.anstar.models.list.BillingTermsList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.DeviceTypesList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.LocationInfoList;
import com.anstar.models.list.MaterialList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.RecomendationsList;
import com.anstar.models.list.ServiceRoutesList;
import com.anstar.models.list.ServicesList;
import com.anstar.models.list.StatusList;
import com.anstar.models.list.TaxRateList;
import com.anstar.models.list.TrapConditionsList;
import com.anstar.models.list.TrapTypesList;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

	ProgressDialog m_pd = null;
//	ActionBar action = null;

	public static final int BARCODE_REQUEST_CODE = 0x0000c0de;
	public static BluetoothAdapter bluetoothAdapter;
	SharedPreferences setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_pd = new ProgressDialog(this);
		m_pd.setMessage("Please wait...");
/*
		action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
				R.color.header_color)));
*/
		setting = PreferenceManager
				.getDefaultSharedPreferences(BaseActivity.this);
	}

	protected void showProgress() {
		if (m_pd != null) {
			m_pd.show();
		}
		// else {
		// m_pd = new ProgressDialog(this);
		// m_pd.setMessage("Please wait...");
		// m_pd.show();
		// }
	}

	protected void showProgress(String msg) {
		if (m_pd != null) {
			m_pd.setCancelable(false);
			m_pd.setMessage(msg);
			m_pd.show();
		}
		// else {
		// m_pd = new ProgressDialog(this);
		// m_pd.setCancelable(false);
		// m_pd.setMessage(msg);
		// m_pd.show();
		// }
	}

	protected void hideProgress() {
		if (m_pd != null) {
			m_pd.dismiss();
			// m_pd = null;
		}
	}

	protected void initiateBarCodeReader(Activity act, int requestCode) {
		Intent intent = new Intent("com.anstar.barcodereader.zxing.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		try {
			act.startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			Toast.makeText(act, "Barcode Scanner not installed ", Toast.LENGTH_LONG).show();
		}
	}

	protected void initiateBarCodeReader(Activity act) {
		initiateBarCodeReader(act, BARCODE_REQUEST_CODE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void loadAll(final boolean isCust) {

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

		if (isCust) {
			showProgress("Syncing customer database and settings");
			try {
				CustomerList.Instance().refreshCustomerList(
						new ModelDelegate<CustomerInfo>() {

							@Override
							public void ModelLoaded(ArrayList<CustomerInfo> list) {
								Account info = Account.getUser();
								info.LastModifiedCustomerData = String
										.valueOf(System.currentTimeMillis());
								info.isCustomerLoded = true;
								try {
									info.save();
								} catch (ActiveRecordException e) {
									e.printStackTrace();
								}
								hideProgress();
							}

							@Override
							public void ModelLoadFailedWithError(String error) {
								hideProgress();
							}
						});
			} catch (Exception e) {
				hideProgress();
				e.printStackTrace();
			}
		} else {
			showProgress();
		}
		try {
			DilutionRatesList.Instance().load(
					new ModelDelegate<DilutionInfo>() {
						@Override
						public void ModelLoaded(ArrayList<DilutionInfo> list) {
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
			LocationInfoList.Instance().load(new ModelDelegate<LocationInfo>() {

				@Override
				public void ModelLoaded(ArrayList<LocationInfo> list) {
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

			MaterialList.Instance().load(new ModelDelegate<MaterialInfo>() {

				@Override
				public void ModelLoaded(ArrayList<MaterialInfo> list) {
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
			RecomendationsList.Instance().load(
					new ModelDelegate<RecomendationInfo>() {

						@Override
						public void ModelLoaded(
								ArrayList<RecomendationInfo> list) {
						}

						@Override
						public void ModelLoadFailedWithError(String error) {

						}
					});
			AppointmentConditionsList.Instance().load(
					new ModelDelegate<AppointmentConditionsInfo>() {

						@Override
						public void ModelLoaded(
								ArrayList<AppointmentConditionsInfo> list) {
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

							if (!isCust) {
								hideProgress();
								Intent i = new Intent(BaseActivity.this,
										DashboardActivity.class);
								startActivity(i);
								finish();
							}
						}

						@Override
						public void ModelLoadFailedWithError(String error) {
							if (!isCust) {
								hideProgress();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void AlertBack(String message) {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(BaseActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = alt_bld.create();
		alert.setTitle("Alert");
		alert.show();
	}

}
