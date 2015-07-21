package com.anstar.fieldwork;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.Account;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.AttachmentsInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.DownloadPdf;
import com.anstar.models.DownloadPdf.UploadDelegate;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.PaymentInfo;
import com.anstar.models.PdfFormsInfo;
import com.anstar.models.PhoneEmailInfo;
import com.anstar.models.PhoneEmailInfo.ContactType;
import com.anstar.models.PhotoAttachmentsInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.StatusInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.PdfFormsList;
import com.anstar.models.list.PhotoAttachmentsList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.models.list.TrapList;
import com.anstar.print.BasePrint;
import com.anstar.print.MsgDialog;
import com.anstar.print.MsgHandle;
import com.anstar.print.PdfPrint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentDetailsActivity extends AppCompatActivity implements
		TimePickerDialog.OnTimeSetListener, OnClickListener, DatePickerDialog.OnDateSetListener {

	int appointment_id = 0;
	private TextView txtName, txtAddress, txtAddress2, txtContactname,
			txtCutomerType, txtDrivingDirections, txtPdfName, txtPdfCount,
			dividerEnvirnMent, txtAttn, dividerPic;
	private RelativeLayout rlAppointmentInfo, rlTargetPests, rlMaterialUse,
			rlNotes, rlInspections, rlTrapScaning, rlSignature, rlName,
			rlDirections, rlEnvironment, rlPdf, rlWorkOrderDetails, rlPdfs,
			rlAddress, rlPictures, rlCamera, rlServiceLocationNotes;
	private Button btnSave, btnPrintPdf;
	// private ImageView imgMap;
	// AppoinmentInfo info = null;
	boolean isFromStarted;
	AppointmentInfo appointmentInfo = null;
	CustomerInfo customerinfo = null;
	ServiceLocationsInfo serviceLocationInfo = null;
	//ActionBar action = null;
	private ListView lstContact;
	private ImageView imgCamera;
	private Spinner spnStatus;
	private SharedPreferences setting;

	private EditText edtStartedat, edtFinisedat;
	private int m_startHour, m_startMinute;
	private int m_finishHour, m_finishMinute;
	int m_day, m_month, m_year;
	boolean print;
	Button btnTrapCount;
	private UserInfo user;
	private BaseLoader mBaseLoader;

	// DateTimePickerDialog dl = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_details);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// info = MainActivity.appointmentInfo;

		setting = PreferenceManager
				.getDefaultSharedPreferences(AppointmentDetailsActivity.this);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnPrintPdf = (Button) findViewById(R.id.btnPrintPdf);

		print = setting.getBoolean("ISPRINT", false);
		if (print) {
			btnSave.setText("Save & Print");
		}
/*
		// imgMap = (ImageView) findViewById(R.id.imgMap);
		action = getSupportActionBar();
		// action.setTitle("Appointment Details");
		action.setTitle(Html
				.fromHtml("<font color='"
						+ getString(R.string.header_text_color)
						+ "'>Work Order</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);
		mBaseLoader = new BaseLoader(this);

		rlAppointmentInfo = (RelativeLayout) findViewById(R.id.rlAppointmentInfo);
		rlTargetPests = (RelativeLayout) findViewById(R.id.rlTargetPests);
		rlMaterialUse = (RelativeLayout) findViewById(R.id.rlMaterialUse);
		rlNotes = (RelativeLayout) findViewById(R.id.rlNotes);
		rlInspections = (RelativeLayout) findViewById(R.id.rlInspections);
		rlTrapScaning = (RelativeLayout) findViewById(R.id.rlTrapScaning);
		rlSignature = (RelativeLayout) findViewById(R.id.rlSignature);
		rlDirections = (RelativeLayout) findViewById(R.id.rlDirections);
		rlName = (RelativeLayout) findViewById(R.id.rlName);
		rlEnvironment = (RelativeLayout) findViewById(R.id.rlEnvironment);
		rlPdf = (RelativeLayout) findViewById(R.id.rlPdf);
		rlServiceLocationNotes = (RelativeLayout) findViewById(R.id.rlServiceLocationNotes);
		rlPdfs = (RelativeLayout) findViewById(R.id.rlPdfs);
		rlCamera = (RelativeLayout) findViewById(R.id.rlCamera);
		rlAddress = (RelativeLayout) findViewById(R.id.rlAddress);
		rlPictures = (RelativeLayout) findViewById(R.id.rlPictures);
		rlWorkOrderDetails = (RelativeLayout) findViewById(R.id.rlWorkOrderDetails);
		txtPdfName = (TextView) findViewById(R.id.txtPdfName);
		txtAddress = (TextView) findViewById(R.id.txtAddress);
		txtName = (TextView) findViewById(R.id.txtServiceLocationName);
		txtAddress2 = (TextView) findViewById(R.id.txtAddress2);
		txtContactname = (TextView) findViewById(R.id.txtContactName);
		txtCutomerType = (TextView) findViewById(R.id.txtServiceName);
		dividerEnvirnMent = (TextView) findViewById(R.id.dividerEnvirnMent);
		dividerPic = (TextView) findViewById(R.id.dividerPic);
		txtAttn = (TextView) findViewById(R.id.txtAttn);
		txtDrivingDirections = (TextView) findViewById(R.id.txtDrivingDirections);
		txtPdfCount = (TextView) findViewById(R.id.txtPdfCount);
		spnStatus = (Spinner) findViewById(R.id.spnStatus);
		lstContact = (ListView) findViewById(R.id.lstContact);
		edtStartedat = (EditText) findViewById(R.id.edtStarted);
		edtFinisedat = (EditText) findViewById(R.id.edtFinished);
		imgCamera = (ImageView) findViewById(R.id.imgCamera);
		// edtScheduledDate = (EditText) findViewById(R.id.edtScheduledDate);
		btnTrapCount = (Button) findViewById(R.id.btnCount);
		lstContact.setDivider(null);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}
		if (appointment_id == 0) {
			appointment_id = Const.app_id;
		}
		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);
		if (appointmentInfo == null) {
			Toast.makeText(getApplicationContext(),
					"Please try again, something went wrong", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		customerinfo = CustomerList.Instance().getCustomerById(
				appointmentInfo.customer_id);
		serviceLocationInfo = ServiceLocationsList.Instance()
				.getServiceLocationById(appointmentInfo.service_location_id);

		if (serviceLocationInfo == null) {
			Toast.makeText(getApplicationContext(),
					"Please try again, something went wrong", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}
		Const.customer_id = appointmentInfo.customer_id;
		btnTrapCount.setText(TrapList
				.Instance()
				.getAllTrapsByCustomerId(appointmentInfo.customer_id,
						serviceLocationInfo.id).size()
				+ "");
		try {
			UserInfo.Instance().load(new ModelDelegate<UserInfo>() {
				@Override
				public void ModelLoaded(ArrayList<UserInfo> list) {
					if (list != null) {
						user = list.get(0);
						if (!user.show_environment_fields) {
							rlEnvironment.setVisibility(View.GONE);
							dividerEnvirnMent.setVisibility(View.GONE);
						} else {
							rlEnvironment.setVisibility(View.VISIBLE);
							dividerEnvirnMent.setVisibility(View.VISIBLE);
						}
						if (user.show_photos) {
							rlPictures.setVisibility(View.VISIBLE);
							dividerPic.setVisibility(View.VISIBLE);
							rlCamera.setVisibility(View.VISIBLE);
						} else {
							rlPictures.setVisibility(View.GONE);
							dividerPic.setVisibility(View.GONE);
							rlCamera.setVisibility(View.GONE);
						}
					}
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btnSave.setOnClickListener(this);
		btnPrintPdf.setOnClickListener(this);
		rlAppointmentInfo.setOnClickListener(this);
		rlPdf.setOnClickListener(this);
		rlTargetPests.setOnClickListener(this);
		rlMaterialUse.setOnClickListener(this);
		rlNotes.setOnClickListener(this);
		rlInspections.setOnClickListener(this);
		rlTrapScaning.setOnClickListener(this);
		rlSignature.setOnClickListener(this);
		rlEnvironment.setOnClickListener(this);
		rlDirections.setOnClickListener(this);
		rlWorkOrderDetails.setOnClickListener(this);
		rlName.setOnClickListener(this);
		rlPdfs.setOnClickListener(this);
		rlPictures.setOnClickListener(this);
		imgCamera.setOnClickListener(this);
		rlServiceLocationNotes.setOnClickListener(this);
		txtDrivingDirections.setOnClickListener(this);
		rlAddress.setOnClickListener(this);
		// imgMap.setOnClickListener(this);

		btnSave.setVisibility(View.VISIBLE);
		spnStatus.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				// if
				// (spnStatus.getSelectedItem().toString().contains("Complete"))
				// {
				// PaymentInfo info = PaymentInfo
				// .getPaymentsInfoByAppId(appointment_id);
				// if (info != null && appointmentInfo != null) {
				// appointmentInfo.syncAppointmentNew(appointmentInfo);
				// }
				// }
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		// final Calendar c = Calendar.getInstance();
		// m_day = c.get(Calendar.DAY_OF_MONTH);
		// m_month = c.get(Calendar.MONTH);
		// m_year = c.get(Calendar.YEAR);
		// m_startHour = c.get(Calendar.HOUR_OF_DAY);
		// m_startMinute = c.get(Calendar.MINUTE);
		// m_finishHour = c.get(Calendar.HOUR_OF_DAY);
		// m_finishMinute = c.get(Calendar.MINUTE);
		setCalender();
		LoadValues();
		edtStartedat.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
					// showDialog(TIME_DIALOG_ID_START);
					new TimePickerDialog(AppointmentDetailsActivity.this, time1,
							m_startHour, m_startMinute, false).show();
				}
				return false;
			}
		});

		edtFinisedat.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
					// showDialog(TIME_DIALOG_ID_FINISH);
					new TimePickerDialog(AppointmentDetailsActivity.this, time2,
							m_finishHour, m_finishMinute, false).show();
				}
				return false;
			}
		});
		// edtScheduledDate.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
		// new DatePickerDialog(AppointmentDetails.this, datelistener,
		// m_year, m_month, m_day).show();
		// // dl = new
		// // DateTimePickerDialog(AppointmentInfoActivity.this);
		// // dl.show();
		// // dl.setDialogResult(new OnMyDialogResult() {
		// //
		// // @Override
		// // public void finish(String result) {
		// // if (result.length() > 0) {
		// // edtScheduledDate.setText(result);
		// // }
		// // }
		// // });
		// }
		// return false;
		// }
		// });
		// edtScheduledDate.requestFocus();
	};

	@Override
	protected void onResume() {
		super.onResume();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}
		if (appointment_id == 0) {
			appointment_id = Const.app_id;
		}
		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);
		if (customerinfo == null) {
			Toast.makeText(getApplicationContext(),
					"Please refresh your data.", Toast.LENGTH_LONG).show();
			finish();
		}
		customerinfo = CustomerList.Instance().getCustomerById(
				appointmentInfo.customer_id);
		String filepath = appointment_id + ".pdf";
		if (isFileExist(getStoragePath(filepath))) {
			rlPdf.setVisibility(View.VISIBLE);
			// String name = filepath.replace("_" + appointment_id, "");
			txtPdfName.setText("attachment_pdf_form.pdf");
			if (print) {
				btnPrintPdf.setVisibility(View.VISIBLE);
			} else {
				btnPrintPdf.setVisibility(View.GONE);
			}
		} else {
			rlPdf.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		// SaveData s = new SaveData();
		// s.execute();
		super.onPause();
	}

	private void StartedAt() {
		if (m_startHour >= 12) {
			String time = m_startHour + ":" + m_startMinute;
			ConvertDate24To12Hour(edtStartedat, time, "PM");
		} else if (m_startHour < 12) {
			String time = m_startHour + ":" + m_startMinute;
			ConvertDate24To12Hour(edtStartedat, time, "AM");
		}
		setCalender();
	}

	private void FinishedAt() {
		if (m_finishHour >= 12) {
			String time = m_finishHour + ":" + m_finishMinute;
			ConvertDate24To12Hour(edtFinisedat, time, "PM");
		} else if (m_finishHour < 12) {
			String time = m_finishHour + ":" + m_finishMinute;
			ConvertDate24To12Hour(edtFinisedat, time, "AM");
		}
		setCalender();
	}

	public void ConvertDate24To12Hour(EditText edt, String time, String am) {
		// hh = 1-12
		// KK = 0-11
		// HH = 0-23
		// kk = 1-24
		final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date dateObj;
		try {
			dateObj = sdf.parse(time);
			String text = new SimpleDateFormat("hh:mm").format(dateObj) + " "
					+ am;
			if (edt == edtStartedat)
				appointmentInfo.started_at_time = time;
			else
				appointmentInfo.finished_at_time = time;
			appointmentInfo.save();
			edt.setText(text);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCalender() {
		final Calendar c = Calendar.getInstance();
		m_day = c.get(Calendar.DAY_OF_MONTH);
		m_month = c.get(Calendar.MONTH);
		m_year = c.get(Calendar.YEAR);
		m_startHour = c.get(Calendar.HOUR_OF_DAY);
		m_startMinute = c.get(Calendar.MINUTE);
		m_finishHour = c.get(Calendar.HOUR_OF_DAY);
		m_finishMinute = c.get(Calendar.MINUTE);
	}

	TimePickerDialog.OnTimeSetListener time1 = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
			m_startHour = arg1;
			m_startMinute = arg2;
			StartedAt();

		}
	};
	TimePickerDialog.OnTimeSetListener time2 = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0, int arg1, int arg2) {

			m_finishHour = arg1;
			m_finishMinute = arg2;
			FinishedAt();

		}
	};
	DatePickerDialog.OnDateSetListener datelistener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			m_day = arg3;
			m_month = arg2;
			m_year = arg1;
			// updateDate(edtScheduledDate);
		}
	};

	private void updateDate(EditText edt1) {
		edt1.setText(new StringBuilder().append(m_year).append("-")
				.append(formatDate(String.valueOf(m_month + 1))).append("-")
				.append(formatDate(String.valueOf(m_day))));

	}

	private String formatDate(String s) {
		if (s.length() == 1) {
			String ss = "0" + s;
			return ss;
		}
		return s;
	}

	public void UploadPdf(int app_id) {
		ArrayList<AttachmentsInfo> m_list = AttachmentsInfo
				.getPdfFormsByWorkerId(app_id);
		if (m_list != null && m_list.size() > 0) {
			for (AttachmentsInfo p : m_list) {
				if (p.id < 0) {
					upload(p.attached_pdf_form_file_name, app_id, p.pdf_id,
							false);
				} else {
					upload(p.attached_pdf_form_file_name, app_id, p.id, true);
				}
			}
		}
	}

	public void upload(String filename, int app_id, final int p_id,
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

	public boolean isSDCARDMounted() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	public boolean isFileExist(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void RenameFile(String newpath, String oldpath) {
		File from = new File(newpath);
		File to = new File(oldpath);
		from.renameTo(to);
	}

	public String getStoragePath(String filename) {
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

	@Override
	public void onClick(View v) {
		if (v == btnSave) {
			PaymentInfo payment_info = PaymentInfo
					.getPaymentsInfoByAppId(appointment_id);
			String status = StatusInfo.getNameByValue(spnStatus
					.getSelectedItem().toString());
			boolean statusFlag = true;
			if (payment_info != null) {
				if (!status.contains("Complete")) {
					statusFlag = false;
				}
			}
			if (!statusFlag) {
				Toast.makeText(
						getApplicationContext(),
						"Please select Complete status to save payment information",
						Toast.LENGTH_LONG).show();
				return;
			}
			if (edtStartedat.getText().toString().length() < 1) {
				Toast.makeText(getApplicationContext(),
						"Start time required for saving", Toast.LENGTH_LONG)
						.show();
			} else if (edtFinisedat.getText().toString().length() < 1) {
				Toast.makeText(getApplicationContext(),
						"Finish time required for saving", Toast.LENGTH_LONG)
						.show();
			} else {
                mBaseLoader.showProgress("Syncing with server");
				String[] time = appointmentInfo.starts_at.split("T");
				String date = appointmentInfo.starts_at;
				// date = date + "T" + time[1];
				String start_at_time = edtStartedat.getText().toString();
				String finish_at_time = edtFinisedat.getText().toString();

				AppointmentInfo.saveAppointementNew(AppointmentDetailsActivity.this,
						appointmentInfo.getID(), status, date, start_at_time,
						finish_at_time, new UpdateInfoDelegate() {

							@Override
							public void UpdateSuccessFully(ServiceResponse res) {
                                mBaseLoader.hideProgress();
								UploadPdf(appointment_id);
								boolean print = setting.getBoolean("ISPRINT",
										false);
								if (print) {
									showPrintDialog();
								} else {
									// Intent i = new
									// Intent(AppointmentDetails.this,
									// AppointmentList.class);
									// startActivity(i);
									finish();
								}

							}

							@Override
							public void UpdateFail(String ErrorMessage) {
								Toast.makeText(getApplicationContext(),
										ErrorMessage, Toast.LENGTH_LONG).show();
                                mBaseLoader.hideProgress();
							}
						});

			}

		} else if (v == rlAppointmentInfo) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					AppointmentInfoActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlTargetPests) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					TargetPestListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlInspections) {

		} else if (v == rlMaterialUse) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					MaterialUsageListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlNotes) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					AddNotesActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlTrapScaning) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					TrapScanningListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlSignature) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					SignatureActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlDirections || v == txtDrivingDirections) {

			// StringBuilder sb = new StringBuilder();
			// sb.append(serviceLocationInfo.suite + " ")
			// .append(serviceLocationInfo.street + " ")
			// .append(serviceLocationInfo.street_two + " ")
			// .append(serviceLocationInfo.city + " ")
			// .append(serviceLocationInfo.state + " ")
			// .append(serviceLocationInfo.zip + " ");
			//
			// // sb.append("5435 Touhy Ave, Skokie, IL 60077-3233");
			// Address location = Utils
			// .getLocationFromAddress(sb.toString(), this);

			Location curruntLoc = Utils.getCurrentLocation(this);
			if (curruntLoc != null && serviceLocationInfo.hasValidLocation()) {
				String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="
						+ curruntLoc.getLatitude() + ","
						+ curruntLoc.getLongitude() + "&daddr="
						+ serviceLocationInfo.lat + ","
						+ serviceLocationInfo.lon;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(intent);
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Could not find location for this address, please check your GPS and internet connection.",
						Toast.LENGTH_LONG).show();
			}

		} else if (v == rlEnvironment) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					EnvironMentActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlPdf) {
			String filepath = appointment_id + ".pdf";
			File file = new File(DownloadPdf.Instance()
					.getStoragePath(filepath));
			Intent target = new Intent(Intent.ACTION_VIEW);
			target.setDataAndType(Uri.fromFile(file), "application/pdf");
			target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			Intent intent = Intent.createChooser(target, "Open File");
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(getApplicationContext(),
						"No Application found for open document.",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == btnPrintPdf) {
			String filepath = appointment_id + ".pdf";
			print(DownloadPdf.Instance().getStoragePath(filepath));
		} else if (v == rlWorkOrderDetails) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					LineItemsActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlName) {
			if (!user.hide_customer_details) {
				Intent i = new Intent(AppointmentDetailsActivity.this,
						CustomerDetailsActivity.class);
				i.putExtra("customer_id", appointmentInfo.customer_id);
				startActivity(i);
			}
		} else if (v == rlPdfs) {
			Intent i = new Intent(AppointmentDetailsActivity.this,
					PdfFormsActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlAddress) {
			if (!user.hide_customer_details) {
				Intent i = new Intent(AppointmentDetailsActivity.this,
						ServiceLocationDetailActivity.class);
				i.putExtra("SLID", appointmentInfo.service_location_id);
				i.putExtra("cid", appointmentInfo.customer_id);
				startActivity(i);
			}
		} else if (v == imgCamera) {
			ArrayList<PhotoAttachmentsInfo> photos = PhotoAttachmentsList
					.Instance().load(appointment_id);
			if (photos != null) {
				if (photos.size() >= 10) {
					Toast.makeText(getApplicationContext(),
							"You can only upload 10 photos.", Toast.LENGTH_LONG)
							.show();
				} else {
					Intent i = new Intent(AppointmentDetailsActivity.this,
							AddPhotosActivity.class);
					i.putExtra(Const.Appointment_Id, appointment_id);
					startActivity(i);
				}
			}
		} else if (v == rlPictures) {
			Intent i = new Intent(AppointmentDetailsActivity.this, PhotosActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlServiceLocationNotes) {

			Intent i = new Intent(AppointmentDetailsActivity.this,
					ServiceLocationNoteActivity.class);
			i.putExtra("SERVICELOCATIONID", serviceLocationInfo.getID());
			startActivity(i);
		}
	}

	public void LoadValues() {
		String[] strattemp = appointmentInfo.starts_at.split("T");
		// String time = temp[1].replace("-06:00", "");
		// edtScheduledDate.setText(strattemp[0]);

		if (appointmentInfo.started_at_time == null
				|| appointmentInfo.started_at_time.equalsIgnoreCase("null")) {
			edtStartedat.setText("");
		} else {
			final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",
					Locale.UK);
			try {
				Date dt = sdf.parse(appointmentInfo.started_at_time);
				m_startHour = dt.getHours();
				m_startMinute = dt.getMinutes();
				// String stime[] = appointmentInfo.started_at_time.substring(0,
				// appointmentInfo.started_at_time.length()).split(":");
				// String[] second = stime[1].split(" ");
				// m_startHour = Integer.parseInt(stime[0]);
				// m_startMinute = Integer.parseInt(second[0]);
				StartedAt();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// edtStartedat.setText(appointment.started_at_time);
		}
		if (appointmentInfo.finished_at_time == null
				|| appointmentInfo.finished_at_time.equalsIgnoreCase("null")) {
			edtFinisedat.setText("");
		} else {
			final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",
					Locale.UK);
			try {
				Date dt = sdf.parse(appointmentInfo.finished_at_time);
				m_finishHour = dt.getHours();
				m_finishMinute = dt.getMinutes();
				// String etime[] =
				// appointmentInfo.finished_at_time.substring(0,
				// appointmentInfo.finished_at_time.length()).split(":");
				// String[] second = etime[1].split(" ");
				// m_finishHour = Integer.parseInt(etime[0]);
				// m_finishMinute = Integer.parseInt(second[0]);
				FinishedAt();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// edtFinisedat.setText(appointment.finished_at_time);
		}

		// String[] temp = appointmentInfo.starts_at.split("T");
		// String[] temp2 = temp[1].split("-");
		// SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		// Date date = null;
		// String time = "";
		// try {
		// date = sdf.parse(temp2[0]);
		// SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
		// time = sdf1.format(date);
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		String name = "";
		txtCutomerType.setText(appointmentInfo.starts_at_time.trim() + " - "
				+ appointmentInfo.ends_at_time.trim());
		if (customerinfo != null && customerinfo.customer_type != null
				&& customerinfo.customer_type.length() > 0) {
			if (customerinfo.customer_type.equalsIgnoreCase("Commercial")) {
				name = customerinfo.name;
			} else {
				name = customerinfo.name_prefix + " " + customerinfo.first_name
						+ " " + customerinfo.last_name;
			}
		}
		if (serviceLocationInfo.name != null
				&& serviceLocationInfo.name.length() > 0) {
			txtName.setText(serviceLocationInfo.name);
		}
		String addr = "";
		if (serviceLocationInfo.street_two != null
				&& serviceLocationInfo.street_two.length() > 0) {
			addr = "\n" + serviceLocationInfo.street_two;
		}
		txtAddress.setText(serviceLocationInfo.street + addr);
		StringBuilder sb = new StringBuilder();
		if (serviceLocationInfo.city != null
				&& serviceLocationInfo.city.length() > 0) {
			sb.append(serviceLocationInfo.city + ", ");
		}
		if (serviceLocationInfo.state != null
				&& serviceLocationInfo.state.length() > 0) {
			sb.append(serviceLocationInfo.state + " ");
		}
		if (serviceLocationInfo.zip != null
				&& serviceLocationInfo.zip.length() > 0) {
			sb.append(serviceLocationInfo.zip);
		}
		txtAddress2.setText(sb.toString());

		if (serviceLocationInfo.attention != null
				&& serviceLocationInfo.attention.length() > 0) {
			txtAttn.setText("Attn : " + serviceLocationInfo.attention);
			txtAttn.setVisibility(View.VISIBLE);
		} else {
			txtAttn.setVisibility(View.GONE);
		}

		txtContactname.setText(name);
		// edtStartedat.setText(appointmentInfo.started_at_time);
		// edtFinisedat.setText(appointmentInfo.finished_at_time);
		// if (serviceLocationInfo.name != null
		// && serviceLocationInfo.name.length() > 0) {
		// txtContactname.setText(serviceLocationInfo.name);
		// } else {
		// rlName.setVisibility(View.GONE);
		// }

		ArrayList<PdfFormsInfo> pinfos = PdfFormsList.Instance()
				.getPdfFormsByWorkOrderId(appointmentInfo.id);
		if (pinfos != null) {
			txtPdfCount.setText(pinfos.size() + "");
			if (pinfos.size() <= 0) {
				rlPdfs.setVisibility(View.VISIBLE);
			} else {
				rlPdfs.setVisibility(View.VISIBLE);
			}
		}

		ArrayList<PhoneEmailInfo> content = new ArrayList<PhoneEmailInfo>();
		PhoneEmailInfo info = null;

		if (serviceLocationInfo.phone != null
				&& serviceLocationInfo.phone.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = serviceLocationInfo.phone_kind;
			info.Value = serviceLocationInfo.phone;
			info.Type = ContactType.Phone.toString();
			content.add(info);
		}
		if (serviceLocationInfo.phones != null
				&& serviceLocationInfo.phones.size() > 0) {
			int i = 0;

			ArrayList<String> temp_contact = new ArrayList<String>();
			for (String s : serviceLocationInfo.phones) {
				if (s != null && s.length() > 0) {
					s = s.replace("[", "");
					s = s.replace("]", "");
					if (s.contains(",")) {
						String h[] = s.split(",");
						for (int j = 0; j < h.length; j++) {
							if (!temp_contact.contains(h[j])) {
								temp_contact.add(h[j]);
							}
						}
					} else {
						temp_contact.add(s);
					}
				}
			}
			ArrayList<String> temp_kind = new ArrayList<String>();
			for (String s : serviceLocationInfo.phones_kinds) {
				if (s != null && s.length() > 0) {
					s = s.replace("[", "");
					s = s.replace("]", "");
					if (s.contains(",")) {
						String h[] = s.split(",");
						for (int j = 0; j < h.length; j++) {
							if (!temp_contact.contains(h[j])) {
								temp_kind.add(h[j]);
							}
						}
					} else {
						temp_kind.add(s);
					}
				}
			}
			for (String ph : temp_contact) {
				if (ph.length() > 0) {
					info = new PhoneEmailInfo();
					info.Kind = temp_kind.size() > i ? temp_kind.get(i) : "";
					info.Value = ph;
					info.Type = ContactType.Phone.toString();
					content.add(info);
					i++;
				}
			}
		}
		if (serviceLocationInfo.email != null
				&& serviceLocationInfo.email.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = "Email";
			info.Value = serviceLocationInfo.email;
			info.Type = ContactType.Email.toString();
			content.add(info);
		}
		// if (serviceLocationInfo.service_emails != null
		// && customerinfo.service_emails.size() > 0) {
		// int i = 0;
		// ArrayList<String> temp_email = new ArrayList<String>();
		// for (String s : customerinfo.service_emails) {
		// if (s != null && s.length() > 0) {
		// s = s.replace("[", "");
		// s = s.replace("]", "");
		// if (s.contains(",")) {
		// String h[] = s.split(",");
		// for (int j = 0; j < h.length; j++) {
		// if (!temp_email.contains(h[j])) {
		// temp_email.add(h[j]);
		// }
		// }
		// } else {
		// temp_email.add(s);
		// }
		// }
		// }
		// ArrayList<String> temp_email_kind = new ArrayList<String>();
		// for (String s : customerinfo.service_emails_kinds) {
		// if (s != null && s.length() > 0) {
		// s = s.replace("[", "");
		// s = s.replace("]", "");
		// if (s.contains(",")) {
		// String h[] = s.split(",");
		// for (int j = 0; j < h.length; j++) {
		// if (!temp_email_kind.contains(h[j])) {
		// temp_email_kind.add(h[j]);
		// }
		// }
		// } else {
		// temp_email_kind.add(s);
		// }
		// }
		// }
		//
		// for (String em : temp_email) {
		// if (em.length() > 0) {
		// info = new PhoneEmailInfo();
		// info.Kind = temp_email_kind.get(i);
		// info.Value = em;
		// info.Type = ContactType.Email.toString();
		// content.add(info);
		// i++;
		// }
		// }
		// }

		MyContactAdapter adapter = new MyContactAdapter(content);
		lstContact.setAdapter(adapter);
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		Utils.setListViewHeightBasedOnChildren(lstContact);
		LoadStatus();
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public void LoadStatus() {

		ArrayList<String> s = new ArrayList<String>();
		try {
			List<StatusInfo> m_statuses = FieldworkApplication.Connection()
					.findAll(StatusInfo.class);
			for (StatusInfo si : m_statuses) {
				s.add(si.statusValue);
			}
			ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
					R.layout.spinner_item, s);
			spnStatus.setAdapter(adp);
			int sign = 0;
			for (int i = 0; i < m_statuses.size(); i++) {
				if (String.valueOf(m_statuses.get(i).statusValue)
						.equalsIgnoreCase(
								String.valueOf(appointmentInfo.status))) {
					sign = i;
					break;
				}
			}
			spnStatus.setSelection(sign);
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public class MyContactAdapter extends BaseAdapter {

		ArrayList<PhoneEmailInfo> m_list = new ArrayList<PhoneEmailInfo>();

		public MyContactAdapter(ArrayList<PhoneEmailInfo> temp) {
			m_list = temp;
		}

		@Override
		public int getCount() {
			return m_list.size();
		}

		@Override
		public Object getItem(int position) {
			return m_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return m_list.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View rowView = convertView;
			holder = new ViewHolder();
			final PhoneEmailInfo info = m_list.get(position);
			if (rowView == null) {
				LayoutInflater li = getLayoutInflater();
				rowView = li.inflate(R.layout.phone_email_item, null);
				rowView.setTag(holder);
				holder.txtKind = (TextView) rowView.findViewById(R.id.txtKind);
				holder.txtValue = (TextView) rowView
						.findViewById(R.id.txtValue);
				holder.rl = (RelativeLayout) rowView
						.findViewById(R.id.rlContact);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			String kind = info.Kind;

			if (info.Kind != null && info.Kind.length() <= 0) {
				kind = "Other";
			}
			holder.txtKind.setText(kind);
			holder.txtValue.setText(info.Value);
			holder.rl.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (info.Type.equalsIgnoreCase(ContactType.Phone.toString())) {
						Utils.callPhone(info.Value, AppointmentDetailsActivity.this);
					}
					if (info.Type.equalsIgnoreCase(ContactType.Email.toString())) {
						Utils.sendEmail(info.Value, AppointmentDetailsActivity.this);
					}
				}
			});

			return rowView;
		}
	}

	public static class ViewHolder {
		TextView txtKind, txtValue;
		RelativeLayout rl;
	}

	public void showPrintDialog() {
		String message = "Do you want to print service report?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				AppointmentDetailsActivity.this);
		alt_bld.setMessage(message)
				.setTitle("Print")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PrintPdf(appointment_id);
								dialog.cancel();
								// Intent i = new
								// Intent(AppointmentDetails.this,
								// AppointmentList.class);
								// startActivity(i);
								// finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						// Intent i = new Intent(AppointmentDetails.this,
						// AppointmentList.class);
						// startActivity(i);
						finish();
					}
				});
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	public void PrintPdf(int app_id) {
		DownloadFile download = new DownloadFile(app_id);
		download.execute();
	}

	public class DownloadFile extends AsyncTask<Void, Void, String> {
		int app_id = 0;

		public DownloadFile(int id) {
			app_id = id;
		}

		@Override
		protected String doInBackground(Void... params) {
			String url = String.format("work_orders/%d", app_id);
			StringBuilder sb = new StringBuilder();

			sb.append(ServiceHelper.URL);
			sb.append(url + "/" + ServiceHelper.GET_SERVICE_REPORT + "?");
			sb.append("api_key=");
			sb.append(Account.getkey());
			String s = AppointmentInfo.DownloadFile(sb.toString());
			return s;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Intent i = new Intent();
			i.putExtra("printpath", result);
			setResult(RESULT_OK, i);
			finish();
			// print(result);
		}

	}

	protected BasePrint myPrint = null;
	protected MsgHandle mHandle;
	protected MsgDialog mDialog;

	public void print(final String path) {
		mDialog = new MsgDialog(this);
		mHandle = new MsgHandle(this, mDialog);
		myPrint = new PdfPrint(this, mHandle, mDialog);

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
		myPrint.setBluetoothAdapter(bluetoothAdapter);
		((PdfPrint) myPrint).setFiles(path);
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int pages = ((PdfPrint) myPrint).getPdfPages(path);
					((PdfPrint) myPrint).setPrintPage(1, pages);
					myPrint.print();
				}
			});

		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	@Override
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onTimeSet(TimePicker arg0, int arg1, int arg2) {

	}

	protected BluetoothAdapter getBluetoothAdapter() {
		final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
			final Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(enableBtIntent);
		}
		return bluetoothAdapter;
	}

}
