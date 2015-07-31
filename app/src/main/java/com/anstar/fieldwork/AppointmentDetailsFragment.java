package com.anstar.fieldwork;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
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
import com.anstar.models.LineItemDetalInfo;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.ModelDelegates;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.PaymentInfo;
import com.anstar.models.PdfFormsInfo;
import com.anstar.models.PhoneEmailInfo;
import com.anstar.models.PhoneEmailInfo.ContactType;
import com.anstar.models.PhotoAttachmentsInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.StatusInfo;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.LineItemsList;
import com.anstar.models.list.MaterialUsagesList;
import com.anstar.models.list.MaterialUsagesRecordsList;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentDetailsFragment extends Fragment implements
		TimePickerDialog.OnTimeSetListener, OnClickListener, DatePickerDialog.OnDateSetListener {

	int appointment_id = 0;
	private TextView cus_name, cont_add_notes, private_notes_text, cont_edit_line_item, arrival_time,
			add_chemical_use_count, add_devices_count, btnDrivingDirections;
	private TextView duration, address, start_time, istruction, no_pdf, notes_text, no_chemical,
			no_photos, total_devices, scanned_devices, un_scanned_devices;
	private RelativeLayout marker_app, lineItem, lineItemList, instruction_touch, instruction_item, pdf_forms_touch, pdf_forms_list;
	private RelativeLayout notes_touch, notes_count, chemical_touch, chemical_count, photo_touch, photo_count, devices_touch, devices_count;
	private ListView listLineItem, listPdfForms, listChemicalUse;
	private RelativeLayout plus_count, count_plus_menu;
	private ImageView plus, add_photo, add_line_item, add_notes, add_chemical;
	private GridView photoList;
	private LineItemsListAdapter m_adapter = null;
	private MaterialUsageAdapter m_adapter_chemical = null;
	private PdfFormsListAdapter m_adapter_pdf = null;
	private PhotosGridAdapter m_adapter_photo = null;

	/*timer*/

	TextView minutes_timer, secunds_timer;
	ImageView buttom_timer;
	boolean start_time_b = false;

	private ArrayList<LineItemsInfo> m_lineitems = null;
	private ArrayList<LineItemDetalInfo> m_lineitems_type = null;
	private ArrayList<MaterialUsage> m_list_chemical = null;
	private ArrayList<PdfFormsInfo> m_pdfforms = null;
	ArrayList<PhotoAttachmentsInfo> photos = new ArrayList<PhotoAttachmentsInfo>();

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
	private ArrayList<TrapScanningInfo> m_traps = null;
	String url = "";

	// DateTimePickerDialog dl = null;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_appointment_details, container, false);

		minutes_timer = (TextView) rootView.findViewById(R.id.minutes_timer);
		secunds_timer = (TextView) rootView.findViewById(R.id.secunds_timer);
		buttom_timer = (ImageView) rootView.findViewById(R.id.buttom_timer);

		duration = (TextView) rootView.findViewById(R.id.textView16);
		address = (TextView) rootView.findViewById(R.id.textView28);
		marker_app = (RelativeLayout) rootView.findViewById(R.id.marker_app);
		start_time = (TextView) rootView.findViewById(R.id.textView15);
		istruction = (TextView) rootView.findViewById(R.id.textView47);
		no_pdf = (TextView) rootView.findViewById(R.id.no_pdf);
		notes_text = (TextView) rootView.findViewById(R.id.notes_text);
		no_chemical = (TextView) rootView.findViewById(R.id.no_chemical);
		no_photos = (TextView) rootView.findViewById(R.id.no_photo);
		total_devices = (TextView) rootView.findViewById(R.id.total_devices);
		scanned_devices = (TextView) rootView.findViewById(R.id.scannet_devices);
		un_scanned_devices = (TextView) rootView.findViewById(R.id.un_scannet_devices);
		cus_name = (TextView) rootView.findViewById(R.id.textView17);
		cont_add_notes = (TextView) rootView.findViewById(R.id.cont_add_notes);
		private_notes_text = (TextView) rootView.findViewById(R.id.private_notes_text);
		cont_edit_line_item = (TextView) rootView.findViewById(R.id.cont_edit_line_item);
		arrival_time = (TextView) rootView.findViewById(R.id.textView25);
		add_chemical_use_count = (TextView) rootView.findViewById(R.id.add_chemical_use_count);
		add_devices_count = (TextView) rootView.findViewById(R.id.add_devices_count);
		btnDrivingDirections = (TextView) rootView.findViewById(R.id.btnDrivingDirections);

		lineItem = (RelativeLayout) rootView.findViewById(R.id.line_item_cont);
		lineItemList = (RelativeLayout) rootView.findViewById(R.id.line_item_info);

		instruction_touch = (RelativeLayout) rootView.findViewById(R.id.instruction_touch);
		instruction_item = (RelativeLayout) rootView.findViewById(R.id.istruction_item);

		pdf_forms_touch = (RelativeLayout) rootView.findViewById(R.id.pdf_forms_touch);
		pdf_forms_list = (RelativeLayout) rootView.findViewById(R.id.pdf_forms_list);

		notes_touch = (RelativeLayout) rootView.findViewById(R.id.notes_touch);
		notes_count = (RelativeLayout) rootView.findViewById(R.id.notes_count);

		chemical_touch = (RelativeLayout) rootView.findViewById(R.id.chemical_touch);
		chemical_count = (RelativeLayout) rootView.findViewById(R.id.chemical_count);

		photo_touch = (RelativeLayout) rootView.findViewById(R.id.photo_touch);
		photo_count = (RelativeLayout) rootView.findViewById(R.id.photo_count);

		devices_count = (RelativeLayout) rootView.findViewById(R.id.divaces_count);
		devices_touch = (RelativeLayout) rootView.findViewById(R.id.divaces_touch);

		plus = (ImageView) rootView.findViewById(R.id.plus_right_bottom);
		plus_count = (RelativeLayout) rootView.findViewById(R.id.plus_cont);
		count_plus_menu = (RelativeLayout) rootView.findViewById(R.id.count_plus_menu);
		add_photo = (ImageView) rootView.findViewById(R.id.add_photo);
		add_notes = (ImageView) rootView.findViewById(R.id.add_notes);
		add_chemical = (ImageView) rootView.findViewById(R.id.add_chemical);

		listLineItem = (ListView) rootView.findViewById(R.id.list_line_item);
		listPdfForms = (ListView) rootView.findViewById(R.id.list_pdf);
		listChemicalUse = (ListView) rootView.findViewById(R.id.chemical_list);
		photoList = (GridView) rootView.findViewById(R.id.list_photo);

		plus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (plus_count.getVisibility() == View.GONE) {
					plus_count.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.menuplus);
					anim.reset();
					plus_count.clearAnimation();
					plus_count.startAnimation(anim);

					Animation animc = AnimationUtils.loadAnimation(getActivity(), R.anim.menupluscount);
					animc.reset();
					count_plus_menu.clearAnimation();
					count_plus_menu.startAnimation(animc);
				} else {
					plus_count.setVisibility(View.GONE);
				}
			}
		});
		buttom_timer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!start_time_b) {
					startT(true);
					buttom_timer.setImageResource(R.drawable.control_stop_timer);
					start_time_b = true;
				} else {
					startT(false);
					buttom_timer.setImageResource(R.drawable.control_start_timer);
					start_time_b = false;
				}
			}
		});

		btnDrivingDirections.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), DrivingDirectionsActivity.class);
				i.putExtra(Const.Appointment_Id, appointmentInfo.id);
				startActivity(i);
			}
		});

		add_devices_count.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), TrapScanningListActivity.class);
				i.putExtra(Const.Appointment_Id, appointmentInfo.id);
				startActivity(i);
			}
		});

		cont_edit_line_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), LineItemsActivity.class);
				i.putExtra(Const.Appointment_Id, appointmentInfo.id);
				startActivity(i);
			}
		});

		add_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), PhotosActivity.class);
				i.putExtra(Const.Appointment_Id, appointmentInfo.id);
				startActivity(i);
			}
		});

		LoadValues();

        return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// info = MainActivity.appointmentInfo;

		setting = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

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

		mBaseLoader = new BaseLoader(getActivity());

		Bundle b = getArguments();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}
		if (appointment_id == 0) {
			appointment_id = Const.app_id;
		}
		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);
		if (appointmentInfo == null) {
			Toast.makeText(getActivity(),
					"Please try again, something went wrong", Toast.LENGTH_LONG)
					.show();
			return;
		}
		m_pdfforms = new ArrayList<PdfFormsInfo>();
		m_lineitems = LineItemsList.Instance().load(appointment_id);

		customerinfo = CustomerList.Instance().getCustomerById(
				appointmentInfo.customer_id);
		serviceLocationInfo = ServiceLocationsList.Instance()
				.getServiceLocationById(appointmentInfo.service_location_id);

		if (serviceLocationInfo == null) {
			Toast.makeText(getActivity(),
					"Please try again, something went wrong", Toast.LENGTH_LONG)
					.show();
			return;
		}

		m_list_chemical = MaterialUsagesList.Instance().load(appointment_id);
		try {
			m_pdfforms = PdfFormsList.Instance().load(appointment_id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (serviceLocationInfo != null) {
			m_traps = TrapList.Instance().getAllTrapsByCustomerId(
					appointmentInfo.customer_id, serviceLocationInfo.id);
		}
		Const.customer_id = appointmentInfo.customer_id;

		StringBuilder sb = new StringBuilder();
		sb.append(ServiceHelper.URL);
		sb.append(ServiceHelper.WORK_ORDERS + "/" + appointment_id + "/"
				+ ServiceHelper.PHOTO_ATTACHMENTS);
		// sb.append("api_key=");
		// sb.append(Account.getkey());
		url = sb.toString();
		/*btnTrapCount.setText(TrapList
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
					new TimePickerDialog(getActivity(), time1,
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
					new TimePickerDialog(getActivity(), time2,
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
		// edtScheduledDate.requestFocus();*/
	};

	@Override
	public void onResume() {
		super.onResume();
		Bundle b = getArguments();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}
		if (appointment_id == 0) {
			appointment_id = Const.app_id;
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("#" + appointment_id);
		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);
		m_lineitems = LineItemsList.Instance().load(appointment_id);
		if (customerinfo == null) {
			Toast.makeText(getActivity(),
					"Please refresh your data.", Toast.LENGTH_LONG).show();
///////			finish();
		}
		customerinfo = CustomerList.Instance().getCustomerById(
				appointmentInfo.customer_id);

		m_list_chemical = MaterialUsagesList.Instance().load(appointment_id);

		if (serviceLocationInfo != null) {
			m_traps = TrapList.Instance().getAllTrapsByCustomerId(
					appointmentInfo.customer_id, serviceLocationInfo.id);
		}
		String filepath = appointment_id + ".pdf";
		/*if (isFileExist(getStoragePath(filepath))) {
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
		}*/
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
				Toast.makeText(getActivity(),
						"Please select Complete status to save payment information",
						Toast.LENGTH_LONG).show();
				return;
			}
			if (edtStartedat.getText().toString().length() < 1) {
				Toast.makeText(getActivity(),
						"Start time required for saving", Toast.LENGTH_LONG)
						.show();
			} else if (edtFinisedat.getText().toString().length() < 1) {
				Toast.makeText(getActivity(),
						"Finish time required for saving", Toast.LENGTH_LONG)
						.show();
			} else {
                mBaseLoader.showProgress("Syncing with server");
				String[] time = appointmentInfo.starts_at.split("T");
				String date = appointmentInfo.starts_at;
				// date = date + "T" + time[1];
				String start_at_time = edtStartedat.getText().toString();
				String finish_at_time = edtFinisedat.getText().toString();

				AppointmentInfo.saveAppointementNew(getActivity(),
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
/////////////////									finish();
								}

							}

							@Override
							public void UpdateFail(String ErrorMessage) {
								Toast.makeText(getActivity(),
										ErrorMessage, Toast.LENGTH_LONG).show();
                                mBaseLoader.hideProgress();
							}
						});

			}

		} /*else if (v == rlAppointmentInfo) {
			Intent i = new Intent(getActivity(),
					AppointmentInfoActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlTargetPests) {
			Intent i = new Intent(getActivity(),
					TargetPestListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlInspections) {

		} else if (v == rlMaterialUse) {
			Intent i = new Intent(getActivity(),
					MaterialUsageListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlNotes) {
			Intent i = new Intent(getActivity(),
					AddNotesActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlTrapScaning) {
			Intent i = new Intent(getActivity(),
					TrapScanningListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlSignature) {
			Intent i = new Intent(getActivity(),
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

			Location curruntLoc = Utils.getCurrentLocation(getActivity());
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
                        getActivity(),
						"Could not find location for this address, please check your GPS and internet connection.",
						Toast.LENGTH_LONG).show();
			}

		}*/ /*else if (v == rlEnvironment) {
			Intent i = new Intent(getActivity(),
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
				Toast.makeText(getActivity(),
						"No Application found for open document.",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == btnPrintPdf) {
			String filepath = appointment_id + ".pdf";
			print(DownloadPdf.Instance().getStoragePath(filepath));
		} else if (v == rlWorkOrderDetails) {
			Intent i = new Intent(getActivity(),
					LineItemsActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlName) {
			if (!user.hide_customer_details) {
				Intent i = new Intent(getActivity(),
						CustomerDetailsFragment.class);
				i.putExtra("customer_id", appointmentInfo.customer_id);
				startActivity(i);
			}
		} else if (v == rlPdfs) {
			Intent i = new Intent(getActivity(),
					PdfFormsActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlAddress) {
			if (!user.hide_customer_details) {
				Intent i = new Intent(getActivity(),
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
					Toast.makeText(getActivity(),
							"You can only upload 10 photos.", Toast.LENGTH_LONG)
							.show();
				} else {
					Intent i = new Intent(getActivity(),
							AddPhotosActivity.class);
					i.putExtra(Const.Appointment_Id, appointment_id);
					startActivity(i);
				}
			}
		} else if (v == rlPictures) {
			Intent i = new Intent(getActivity(), PhotosActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
		} else if (v == rlServiceLocationNotes) {

			Intent i = new Intent(getActivity(),
					ServiceLocationNoteActivity.class);
			i.putExtra("SERVICELOCATIONID", serviceLocationInfo.getID());
			startActivity(i);
		}*/
	}

	public void LoadValues() {
		if (appointmentInfo.status.equals("Missed Appointment")) {
			marker_app.setBackgroundResource(R.color.marck_app_miss);
		} else if (appointmentInfo.status.equals("Complete")) {
			marker_app.setBackgroundResource(R.color.marck_app_comp);
		} else if (appointmentInfo.status.equals("Scheduled")) {
			marker_app.setBackgroundResource(R.color.marck_app_sched);
		}

		start_time.setText(appointmentInfo.starts_at_time);

		String name = "";
		if (customerinfo != null) {
			if (customerinfo.customer_type.equalsIgnoreCase("Commercial")) {
				name = customerinfo.name;
			} else {
				name = customerinfo.name_prefix + " "
						+ customerinfo.first_name + " "
						+ customerinfo.last_name;
			}
		}

		cus_name.setText(name);
		if (serviceLocationInfo != null) {
			address.setText(serviceLocationInfo.name + "\n" + serviceLocationInfo.country + "\n" + serviceLocationInfo.street + "\n" +
					serviceLocationInfo.city + ", " + serviceLocationInfo.state + " " + serviceLocationInfo.zip);
		}

		notes_text.setText(appointmentInfo.notes);
		private_notes_text.setText(appointmentInfo.private_notes);

		istruction.setText(appointmentInfo.instructions);


		if (m_traps != null) {
			total_devices.setText(m_traps.size() + "");
			int scaned = 0;
			for (TrapScanningInfo trap : m_traps) {
				if (trap.isChecked) {
					scaned++;
				}
			}

			scanned_devices.setText(scaned + "");
			un_scanned_devices.setText((m_traps.size() - scaned) + "");
		}
		m_lineitems_type = new ArrayList<LineItemDetalInfo>();
		LineItemDetalInfo title_line = new LineItemDetalInfo();
		title_line.type_line = "title";
		m_lineitems_type.add(title_line);

		Double total = 0.0;
		for (LineItemsInfo item : m_lineitems) {
			LineItemDetalInfo item_line = new LineItemDetalInfo();
			item_line.type_line = "item";
			item_line.name = item.name;
			item_line.price = item.price;
			item_line.quantity = item.quantity;
			m_lineitems_type.add(item_line);
			total = total + Double.parseDouble(item.price);
		}

		LineItemDetalInfo total_line = new LineItemDetalInfo();
		total_line.type_line = "total";
		total_line.total_line = total;
		m_lineitems_type.add(total_line);

		m_adapter = new LineItemsListAdapter(m_lineitems_type);
		listLineItem.setAdapter(m_adapter);

		if (m_list_chemical != null) {
			m_adapter_chemical = new MaterialUsageAdapter(m_list_chemical);
			listChemicalUse.setAdapter(m_adapter_chemical);
		}
		if (m_pdfforms.size() > 0) {
			m_adapter_pdf = new PdfFormsListAdapter(m_pdfforms);
			listPdfForms.setAdapter(m_adapter_pdf);
		}
		else {
			no_pdf.setVisibility(View.VISIBLE);
			listPdfForms.setVisibility(View.GONE);
		}

		photos = new ArrayList<PhotoAttachmentsInfo>();
		photos = PhotoAttachmentsList.Instance().load(appointment_id);
		if (photos != null) {
			m_adapter_photo = new PhotosGridAdapter(photos);
			photoList.setAdapter(m_adapter_photo);
		}

		lineItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (lineItemList.getVisibility() == View.GONE) {
					Utility.setListViewChild(listLineItem);
					ClouseLayout("lineItemList");
				}
				else {
					collapse(lineItemList);
				}
			}
		});
		instruction_touch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (instruction_item.getVisibility() == View.GONE) {
					ClouseLayout("instruction_item");
				}
				else {
					collapse(instruction_item);
				}
			}
		});
		pdf_forms_touch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pdf_forms_list.getVisibility() == View.GONE) {
					Utility.setListViewChild(listPdfForms);
					ClouseLayout("pdf_forms_list");
				}
				else {
					collapse(pdf_forms_list);
				}
			}
		});
		notes_touch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (notes_count.getVisibility() == View.GONE) {
					ClouseLayout("notes_count");
				}
				else {
					collapse(notes_count);
				}
			}
		});
		chemical_touch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (chemical_count.getVisibility() == View.GONE) {
					Utility.setListViewChild(listChemicalUse);
					ClouseLayout("chemical_count");
				}
				else {
					collapse(chemical_count);
				}
			}
		});
		photo_touch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (photo_count.getVisibility() == View.GONE) {
					ClouseLayout("photo_count");
				}
				else {
					collapse(photo_count);
				}
			}
		});
		devices_touch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (devices_count.getVisibility() == View.GONE) {
					ClouseLayout("devices_count");
				}
				else {
					collapse(devices_count);
				}
			}
		});
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
			ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),
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



	public void showPrintDialog() {
		String message = "Do you want to print service report?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
                getActivity());
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
////////////////						finish();
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
///////////			setResult(Activity.RESULT_OK, i);
/////////////			finish();
			// print(result);
		}

	}

	protected BasePrint myPrint = null;
	protected MsgHandle mHandle;
	protected MsgDialog mDialog;

	public void print(final String path) {
		mDialog = new MsgDialog(getActivity());
		mHandle = new MsgHandle(getActivity(), mDialog);
		myPrint = new PdfPrint(getActivity(), mHandle, mDialog);

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
		myPrint.setBluetoothAdapter(bluetoothAdapter);
		((PdfPrint) myPrint).setFiles(path);
		try {
            getActivity().runOnUiThread(new Runnable() {
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

	private void startT(boolean timer) {
		if (timer) {
			timerHandler.removeCallbacks(timerRunnable);
			timerHandler.postDelayed(timerRunnable, 0);
		} else {
			timerHandler.removeCallbacks(timerRunnable);
		}
	}

	Handler timerHandler = new Handler();
	public Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
			Integer secund = Integer.parseInt(secunds_timer.getText().toString());
			Integer minutes = Integer.parseInt(minutes_timer.getText().toString());
			secund++;
			if (secund == 60) {
				secunds_timer.setText("00");
				minutes++;
				if (minutes < 10) {
					minutes_timer.setText("0" + Integer.toString(minutes));
				}
				else {
					minutes_timer.setText(Integer.toString(minutes));
				}
			}
			else {
				if (secund < 10) {
					secunds_timer.setText("0" + Integer.toString(secund));
				}
				else {
					secunds_timer.setText(Integer.toString(secund));
				}
			}
			timerHandler.postDelayed(this, 1000);
		}
	};

	private void ClouseLayout(String layout_t) {
		if (layout_t.equals("lineItemList")) {
			expand(lineItemList);
		}
		else {
			collapse(lineItemList);
		}
		if (layout_t.equals("instruction_item")) {
			expand(instruction_item);
		}
		else {
			collapse(instruction_item);
		}
		if (layout_t.equals("pdf_forms_list")) {
			expand(pdf_forms_list);
		}
		else {
			collapse(pdf_forms_list);
		}
		if (layout_t.equals("notes_count")) {
			expand(notes_count);
		}
		else {
			collapse(notes_count);
		}
		if (layout_t.equals("chemical_count")) {
			expand(chemical_count);
		}
		else {
			collapse(chemical_count);
		}
		if (layout_t.equals("photo_count")) {
			expand(photo_count);
		}
		else {
			collapse(photo_count);
		}
		if (layout_t.equals("devices_count")) {
			expand(devices_count);
		}
		else {
			collapse(devices_count);
		}
	}
	public static void expand(final View v) {
		v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1
						? RelativeLayout.LayoutParams.WRAP_CONTENT
						: (int)(targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1){
					v.setVisibility(View.GONE);
				}else{
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public class LineItemsListAdapter extends BaseAdapter {
		ArrayList<LineItemDetalInfo> m_list = new ArrayList<LineItemDetalInfo>();

		public LineItemsListAdapter(ArrayList<LineItemDetalInfo> list) {
			m_list = list;
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null || !(rowView instanceof LinearLayout)) {
				LayoutInflater vi = LayoutInflater.from(getActivity());
				rowView = vi.inflate(R.layout.line_item, null);

				ViewHolder holder = new ViewHolder();
				holder.TitleLine = (TableRow) rowView.findViewById(R.id.title_line_item);
				holder.ItemLine = (TableRow) rowView.findViewById(R.id.item_line_item);
				holder.TotalLine = (TableRow) rowView.findViewById(R.id.total_line_item);
				holder.name = (TextView) rowView.findViewById(R.id.textView41);
				holder.quantity = (TextView) rowView.findViewById(R.id.textView42);
				holder.price = (TextView) rowView.findViewById(R.id.textView43);
				holder.total = (TextView) rowView.findViewById(R.id.textView46);
				rowView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();

			final LineItemDetalInfo item = m_list.get(position);
			if (item.type_line.equals("title")) {
				holder.TitleLine.setVisibility(View.VISIBLE);
				holder.ItemLine.setVisibility(View.GONE);
				holder.TotalLine.setVisibility(View.GONE);
			}
			if (item.type_line.equals("item")) {
				holder.TitleLine.setVisibility(View.GONE);
				holder.ItemLine.setVisibility(View.VISIBLE);
				holder.TotalLine.setVisibility(View.GONE);
				holder.name.setText(item.name + "");
				holder.quantity.setText(item.quantity + "");
				holder.price.setText("$" + item.price);
			}
			if (item.type_line.equals("total")) {
				holder.TitleLine.setVisibility(View.GONE);
				holder.ItemLine.setVisibility(View.GONE);
				holder.TotalLine.setVisibility(View.VISIBLE);
				holder.total.setText("$" + item.total_line);
			}

			return rowView;
		}

		private class ViewHolder {
			TableRow TitleLine;
			TableRow ItemLine;
			TableRow TotalLine;
			TextView name;
			TextView quantity;
			TextView price;
			TextView total;
		}
	}

	public class MaterialUsageAdapter extends BaseAdapter {
		ArrayList<MaterialUsage> m_list = new ArrayList<MaterialUsage>();

		public MaterialUsageAdapter(ArrayList<MaterialUsage> list) {
			m_list = list;
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null || !(rowView instanceof LinearLayout)) {
				LayoutInflater vi = LayoutInflater.from(getActivity());
				rowView = vi.inflate(R.layout.chemical_item, null);

				ViewHolder holder = new ViewHolder();
				holder.nameMatirial = (TextView) rowView.findViewById(R.id.textView49);
				holder.nameDilution = (TextView) rowView.findViewById(R.id.textView50);
				rowView.setTag(holder);
			}

			final MaterialUsage usage = m_list.get(position);
			ViewHolder holder = (ViewHolder) rowView.getTag();
			if (usage != null) {
				holder.nameMatirial.setText(MaterialInfo
						.getMaterialNamebyId(usage.material_id));
				final ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
						.Instance().getMaterialRecordsByUsageId(usage.id);
				if (records != null && records.size() > 0) {
					final MaterialUsageRecords record = records.get(0);
					int locations = 1;
					if (records != null) {
						locations = records.size();
					}
					String appmethod = "";
					if (record.application_method != null) {
						appmethod = " , " + record.application_method;
					}

					if (record.amount != null && record.amount.length() > 0
							&& !record.amount.equalsIgnoreCase("null")) {
						holder.nameDilution.setText(DilutionRatesList
								.Instance().getDilutionNameByid(
										record.dilution_rate_id)
								+ " , "
								+ String.valueOf(Float
								.parseFloat(record.amount) * locations)
								+ " , " + record.measurement + appmethod);
					} else {
						holder.nameDilution.setText(DilutionRatesList
								.Instance().getDilutionNameByid(
										record.dilution_rate_id)
								+ " , "
								+ "0.0"
								+ " , "
								+ record.measurement
								+ appmethod);
					}
				}

			}

			return rowView;
		}

		private class ViewHolder {
			TextView nameMatirial;
			TextView nameDilution;
		}
	}

	public class PdfFormsListAdapter extends BaseAdapter {
		ArrayList<PdfFormsInfo> m_list = new ArrayList<PdfFormsInfo>();

		public PdfFormsListAdapter(ArrayList<PdfFormsInfo> list) {
			m_list = list;
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null || !(rowView instanceof LinearLayout)) {
				LayoutInflater vi = LayoutInflater.from(getActivity());
				rowView = vi.inflate(R.layout.main_list_item, null);

				ViewHolder holder = new ViewHolder();
				holder.main_item_text = (TextView) rowView.findViewById(R.id.main_item_text);
				holder.rl_main_list_item = (RelativeLayout) rowView.findViewById(R.id.rl_main_list_item);
				rowView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();
			final PdfFormsInfo pItem = m_list.get(position);
			holder.main_item_text.setText(pItem.name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// DownloadPdf(pItem);
				}
			});
			return rowView;
		}

		private class ViewHolder {
			TextView main_item_text;
			RelativeLayout rl_main_list_item;
		}
	}

	public class PhotosGridAdapter extends BaseAdapter {
		ArrayList<PhotoAttachmentsInfo> m_list = new ArrayList<PhotoAttachmentsInfo>();

		public PhotosGridAdapter(ArrayList<PhotoAttachmentsInfo> list) {
			m_list = list;
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null || !(rowView instanceof LinearLayout)) {
				LayoutInflater vi = LayoutInflater.from(getActivity());
				rowView = vi.inflate(R.layout.photos_item, null);

				ViewHolder holder = new ViewHolder();
				holder.imgPhoto = (ImageView) rowView.findViewById(R.id.imgPhoto);
				holder.rl_main_list_item = (RelativeLayout) rowView.findViewById(R.id.rl_main_list_item);
				rowView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();
			final PhotoAttachmentsInfo photo = m_list.get(position);
			ContextWrapper cw = new ContextWrapper(
					FieldworkApplication.getContext());
			// path to /data/data/yourapp/app_data/imageDir
			File directory = cw.getDir(Environment.DIRECTORY_DOWNLOADS,
					Context.MODE_PRIVATE);
			String path = Const.FieldWorkImages + "_" + appointment_id + "_"
					+ photo.id;
			File mypath = new File(directory, path + ".jpg");
			Bitmap myBitmap = BitmapFactory.decodeFile(mypath.getAbsolutePath());
			if (myBitmap != null)
				holder.imgPhoto.setImageBitmap(myBitmap);
			return rowView;
		}

		private class ViewHolder {
			ImageView imgPhoto;
			RelativeLayout rl_main_list_item;
		}
	}

	public static class Utility {
		public static void setListViewChild(ListView list) {
			ListAdapter listadapter = list.getAdapter();
			if (listadapter == null) {
				return;
			}
			int totalHight = 0;
			for (int i = 0; i < listadapter.getCount(); i++) {
				View listitem = listadapter.getView(i, null, list);
				listitem.measure(0, 0);
				totalHight += listitem.getMeasuredHeight();
				Log.w("HEIGHT" + i, String.valueOf(listitem.getMeasuredHeight()));
			}
			ViewGroup.LayoutParams params = list.getLayoutParams();
			params.height = totalHight;
			list.setLayoutParams(params);
		}

		public static void setGridViewChild(GridView list) {
			ListAdapter listadapter = list.getAdapter();
			if (listadapter == null) {
				return;
			}
			int totalHight = 0;
			for (int i = 0; i < listadapter.getCount(); i++) {
				View listitem = listadapter.getView(i, null, list);
				listitem.measure(0, 0);
				totalHight += listitem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = list.getLayoutParams();
			params.height = totalHight/3/*
                    + (list.getHeight() * (listadapter.getCount() - 1))*/;
			list.setLayoutParams(params);
		}

	}
	private Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE=70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
					o.outHeight / scale / 2 >= REQUIRED_SIZE) {
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}

}
