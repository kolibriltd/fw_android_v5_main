package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.BaseLoader;
import com.anstar.common.CustomTimePickerDialog;
import com.anstar.common.MultipleSelectionSpinner;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.dialog.ConfirmDialog;
import com.anstar.models.Account;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateAppointmentDelegate;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.ServiceRoutesInfo;
import com.anstar.models.TaxRates;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.models.list.ServiceRoutesList;
import com.anstar.models.list.TaxRateList;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAppointmentActivity extends AppCompatActivity implements
		OnClickListener, OnDateSetListener, TimePickerDialog.OnTimeSetListener, ConfirmDialog.OnConfirmDialogListener {
	private Button btnSave;
	private EditText edtStartDate, edtStartTime, edtEndTime, edtPoNumber,
			edtDiscount, edtServiceInstruction;
	//ActionBar action = null;
	private ImageView imgAddLineItem;
	private MultipleSelectionSpinner spnServiceRoutes;
	private RelativeLayout rlChooseCustomer, rlChooseServiceLocation;
	private TextView txtServiceLocationName, txtServiceLocationAddress, txtTax,
			txtTotal, txtCustomer, txtServiceLocation;
	private ListView lstLineItems;
	int m_day, m_month, m_year, cid = 0, sid = 0;
	private int CHOOSE_CUSTOMER = 1;
	private int CHOOSE_SERVICE_LOCATION = 2;
	private int ADD_LINE_ITEM = 3;
	private LineItemAdapter adapter;
	private int m_startHour, m_startMinute, m_finishHour, m_finishMinute;
	public static ArrayList<LineItemsInfo> lineitems;
	private TaxRates trate;
	private ArrayList<ServiceRoutesInfo> routesInfo = new ArrayList<ServiceRoutesInfo>();
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_appointment);

		mBaseLoader = new BaseLoader(this);

//		action = getSupportActionBar();
		// action.setTitle("Add Material");
/*
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>New Appointment</font>"));
*/
		if (!NetworkConnectivity.isConnected()) {
			Toast.makeText(getApplicationContext(),
					"You need internet connection to add Appointment",
					Toast.LENGTH_LONG).show();
		}
/*
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar action = getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.setDisplayShowHomeEnabled(true);

		btnSave = (Button) findViewById(R.id.btnSubmit);

		lineitems = new ArrayList<LineItemsInfo>();

		edtStartDate = (EditText) findViewById(R.id.edtStartDate);
		edtStartTime = (EditText) findViewById(R.id.edtStartTime);
		edtEndTime = (EditText) findViewById(R.id.edtEndTime);
		edtPoNumber = (EditText) findViewById(R.id.edtPoNumber);
		edtDiscount = (EditText) findViewById(R.id.edtDiscount);
		imgAddLineItem = (ImageView) findViewById(R.id.imgAddLineItem);
		txtServiceLocationName = (TextView) findViewById(R.id.txtServiceLocationName);
		txtServiceLocationAddress = (TextView) findViewById(R.id.txtServiceLocationAddress);
		edtServiceInstruction = (EditText) findViewById(R.id.edtServiceInstruction);
		txtTax = (TextView) findViewById(R.id.txtTax);
		txtTotal = (TextView) findViewById(R.id.txtTotal);
		txtCustomer = (TextView) findViewById(R.id.txtCustomer);
		txtServiceLocation = (TextView) findViewById(R.id.txtServiceLocation);
		lstLineItems = (ListView) findViewById(R.id.lstLineItems);
		rlChooseCustomer = (RelativeLayout) findViewById(R.id.rlChooseCustomer);
		rlChooseServiceLocation = (RelativeLayout) findViewById(R.id.rlChooseServiceLocation);
		spnServiceRoutes = (MultipleSelectionSpinner) findViewById(R.id.spnServiceRoutes);
		spnServiceRoutes.setPrompt("select route");

		btnSave.setOnClickListener(this);
		rlChooseCustomer.setOnClickListener(this);
		rlChooseServiceLocation.setOnClickListener(this);
		imgAddLineItem.setOnClickListener(this);
		setCalender();

		edtStartDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
					new DatePickerDialog(AddAppointmentActivity.this,
							datelistener, m_year, m_month, m_day).show();
					// DateTimePickerDialog dl = new DateTimePickerDialog(
					// AddAppointmentActivity.this);
					// dl.show();
					// dl.setDialogResult(new OnMyDialogResult() {
					//
					// @Override
					// public void finish(String result) {
					// if (result.length() > 0) {
					// edtStartDate.setText(result);
					// }
					// }
					// });
				}
				return false;
			}
		});

		edtStartTime.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
					// showDialog(TIME_DIALOG_ID_START);

					new CustomTimePickerDialog(AddAppointmentActivity.this,
							time1, m_startHour, m_startMinute, false).show();
				}
				return false;
			}
		});
		edtEndTime.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
					// showDialog(TIME_DIALOG_ID_START);
					new CustomTimePickerDialog(AddAppointmentActivity.this,
							time2, m_finishHour, m_finishMinute, false).show();
				}
				return false;
			}
		});

		edtDiscount.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				calculateTotal();
			}
		});
		edtPoNumber.requestFocus();
		loadValues();
	}

	private NumberPicker getMinuteSpinner(TimePicker t) {
		try {
			Field f = t.getClass().getDeclaredField("mMinuteSpinner"); // NoSuchFieldException
			f.setAccessible(true);
			return (NumberPicker) f.get(t); // IllegalAccessException
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		lineitems = null;
	}

	TimePickerDialog.OnTimeSetListener time1 = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0,
				int arg1, int arg2) {
			m_startHour = arg1;
			m_startMinute = arg2;
			StartedAt();

		}
	};
	TimePickerDialog.OnTimeSetListener time2 = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0,
				int arg1, int arg2) {

			m_finishHour = arg1;
			m_finishMinute = arg2;
			FinishedAt();

		}
	};

	private void StartedAt() {
		if (m_startHour >= 12) {
			String time = m_startHour + ":" + m_startMinute;
			ConvertDate24To12Hour(edtStartTime, time, "PM");
		} else if (m_startHour < 12) {
			String time = m_startHour + ":" + m_startMinute;
			ConvertDate24To12Hour(edtStartTime, time, "AM");
		}
	}

	private void FinishedAt() {
		if (m_finishHour >= 12) {
			String time = m_finishHour + ":" + m_finishMinute;
			ConvertDate24To12Hour(edtEndTime, time, "PM");
		} else if (m_finishHour < 12) {
			String time = m_finishHour + ":" + m_finishMinute;
			ConvertDate24To12Hour(edtEndTime, time, "AM");
		}
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
			edt.setText(new SimpleDateFormat("hh:mm").format(dateObj) + " "
					+ am);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void loadValues() {
		Date d = new Date();
		SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat tformat = new SimpleDateFormat("hh:mm a");

		edtStartDate.setText(dformat.format(d));
		edtStartTime.setText(getRangeTime(tformat.format(d), true));
		d.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		edtEndTime.setText(getRangeTime(tformat.format(d), false));

		try {
			ServiceRoutesList.Instance().load(
					new ModelDelegate<ServiceRoutesInfo>() {
						@Override
						public void ModelLoaded(
								ArrayList<ServiceRoutesInfo> list) {
							if (list != null || list.size() > 0) {
								routesInfo.addAll(list);
								String[] routes = new String[list.size()];
								for (int i = 0; i < list.size(); i++) {
									routes[i] = list.get(i).name;
								}
								spnServiceRoutes.setItems(routes);
								spnServiceRoutes.setSelection(0);
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
	}

	private String getRangeTime(String currenttime, boolean isstart) {
		String temp[] = currenttime.split(" ");
		String ampm = temp[1];
		String minutes[] = temp[0].split(":");
		String mins = minutes[1];
		int m = Integer.parseInt(mins.substring(0, 1));
		int mm = Integer.parseInt(mins.substring(1, 2));
		int min = Integer.parseInt(mins);
		do {
			min += 1;
			if (min == 60)
				min = 00;
			if (min % 15 == 0) {
				break;
			}
		} while (min < 60);
		String ans = "";
		// if(m == 0)
		// ans ="15";
		// else if(m == 1){
		// if(mm <= 5)
		// ans = "15";
		// else
		// ans = "30";
		// }else if(m == 2)
		// ans = "30";
		// else if(m == 3)
		// ans = "45";
		// else if(m == 4){
		// if(mm <= 5)
		// ans = "45";
		// else
		// ans = "60";
		// }else if(m == 5)
		// ans = "60";
		// else if(m == 6)
		// ans = "60";
		if (min == 0)
			ans = "00";
		else
			ans = String.valueOf(min);

		String time = minutes[0] + ":" + ans + " " + ampm;
		if (isstart) {
			m_startHour = Integer.parseInt(minutes[0]);
			m_startMinute = min;
		} else {
			m_finishHour = Integer.parseInt(minutes[0]);
			m_finishMinute = min;
		}

		return time;
	}

    public class LineItemAdapter extends BaseAdapter {
		private ArrayList<LineItemsInfo> m_list = new ArrayList<LineItemsInfo>();

		public LineItemAdapter(ArrayList<LineItemsInfo> list) {
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
			ViewHolder holder;
			View rowView = convertView;
			holder = new ViewHolder();
			if (rowView == null) {
				LayoutInflater li = getLayoutInflater();
				rowView = li
						.inflate(R.layout.addappt_line_item_list_item, null);
				rowView.setTag(holder);
				holder.txtName = (TextView) rowView.findViewById(R.id.txtName);
				holder.txtQty = (TextView) rowView.findViewById(R.id.txtQty);
				holder.txtPrice = (TextView) rowView
						.findViewById(R.id.txtPrice);

				holder.ll = (LinearLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}
			LineItemsInfo item = m_list.get(position);

			holder.txtName.setText(item.name);
			holder.txtQty.setText(item.quantity);
			holder.txtPrice.setText("$" + item.price);
			final int pos = position;
			holder.ll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(AddAppointmentActivity.this,
							AddLineItemActivity.class);
					i.putExtra("isedit", true);
					i.putExtra("position", pos);
					startActivityForResult(i, ADD_LINE_ITEM);
				}
			});
			holder.ll.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder alt_bld = new AlertDialog.Builder(
							AddAppointmentActivity.this);

					alt_bld.setMessage("Are you sure want to delete it?")
							.setCancelable(false)
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
											lineitems.remove(pos);
											adapter = new LineItemAdapter(
													lineitems);
											lstLineItems.setAdapter(adapter);
											Utils.setListViewHeightBasedOnChildren(lstLineItems);
											calculateTotal();
										}
									})
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});

					AlertDialog alert = alt_bld.create();
					alert.setTitle("Alert");
					alert.show();

					return false;
				}
			});
			return rowView;
		}
	}

	private class ViewHolder {
		TextView txtName, txtQty, txtPrice;
		LinearLayout ll;
	}

	DatePickerDialog.OnDateSetListener datelistener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			m_day = arg3;
			m_month = arg2;
			m_year = arg1;
			edtStartDate.setText((m_month + 1) + "/" + m_day + "/" + m_year);
			// updateDate(edtScheduledDate);
		}
	};

	public void setCalender() {
		final Calendar c = Calendar.getInstance();
		m_day = c.get(Calendar.DAY_OF_MONTH);
		m_month = c.get(Calendar.MONTH);
		m_year = c.get(Calendar.YEAR);
	}

	@Override
	public void onClick(View v) {

		if (v == btnSave) {
			boolean flag = true;
			String msg = "";
			List<Integer> rts = spnServiceRoutes.getSelectedIndicies();
			if (cid == 0) {
				flag = false;
				msg += "Please choose Customer\n";
			} else if (sid == 0) {
				flag = false;
				msg += "Please choose Service location\n";
			} else if (edtStartTime.getText().toString().trim().length() < 0) {
				flag = false;
				msg += "Start time should not be blank\n";
			} else if (edtEndTime.getText().toString().trim().length() < 0) {
				flag = false;
				msg += "End time should not be blank\n";
			} else if (lineitems.size() <= 0) {
				flag = false;
				msg += "Please choose atleast 1 line item\n";
			} else if (rts == null || rts.size() <= 0) {
				flag = false;
				msg += "Please choose Service route\n";
			}
			if (flag) {
				AppointmentInfo ainfo = new AppointmentInfo();
				ainfo.purchase_order_no = edtPoNumber.getText().toString()
						.trim();
				ainfo.starts_at_date = edtStartDate.getText().toString().trim();
				ainfo.starts_at_time = edtStartTime.getText().toString().trim();
				ainfo.ends_at_time = edtEndTime.getText().toString().trim();
				ainfo.discount_amount = edtDiscount.getText().toString().trim();
				ainfo.instructions = edtServiceInstruction.getText().toString()
						.trim();
				ainfo.customer_id = cid;
				ainfo.service_location_id = sid;
				ainfo.tax_amount = String.valueOf(taxamount);

				List<String> routeids = new ArrayList<String>();
				if (rts != null && rts.size() > 0) {
					for (Integer i : rts) {
						routeids.add(String.valueOf(routesInfo.get(i).id));
					}
					if (routeids.size() > 0)
						ainfo.technician_signature_name = Utils.Instance()
								.join(routeids, ",");
				}

				String dt = edtStartDate.getText().toString();
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				try {
					Date d = format.parse(dt);
					format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssZZZ");
					dt = format.format(d);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dt = dt.replace(" ", "T");
				ainfo.starts_at = dt;

				// serviceInfo.email = edtServiceEmail.getText().toString();
				// serviceInfo.name =
				// edtServiceLocationName.getText().toString();

				mBaseLoader.showProgress("Saving Appointment...");
				AppointmentInfo.AddAppointment(new UpdateAppointmentDelegate() {

					@Override
					public void UpdateSuccessFully(AppointmentInfo info) {
						mBaseLoader.hideProgress();
						Toast.makeText(FieldworkApplication.getContext(),
								"Appointment added successfully",
								Toast.LENGTH_LONG).show();
						finish();
					}

					@Override
					public void UpdateFail(String ErrorMessage) {
						mBaseLoader.hideProgress();
						Toast.makeText(FieldworkApplication.getContext(),
								ErrorMessage, Toast.LENGTH_LONG).show();
					}
				}, ainfo, lineitems);

			} else {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		} else if (v == rlChooseCustomer) {
			Account a = Account.getUser();
			if (a != null) {
				if (!a.isCustomerLoded) {
					// AlertToloadCustomer();
					downloadCustomer();
				} else {
					if (CustomerList.Instance().getAllCustomer().size() == 0) {
						downloadCustomer();
						a.isCustomerLoded = false;
						try {
							a.save();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}

					} else {
						Intent i = new Intent(AddAppointmentActivity.this,
								CustomerListFragment.class);
						i.putExtra("FromAddAppointment", true);
						startActivityForResult(i, CHOOSE_CUSTOMER);
					}
				}
			}

			// Intent i = new Intent(AddAppointmentActivity.this,
			// CustomerListActivity.class);
			// i.putExtra("FromAddAppointment", true);
			// startActivityForResult(i, CHOOSE_CUSTOMER);
		} else if (v == rlChooseServiceLocation) {
			if (cid != 0) {
				Intent i = new Intent(AddAppointmentActivity.this,
						ServiceLocationListActivity.class);
				i.putExtra("CID", cid);
				i.putExtra("FromAddAppointment", true);
				startActivityForResult(i, CHOOSE_SERVICE_LOCATION);
			} else {
				Toast.makeText(getApplicationContext(),
						"Please select customer first.", Toast.LENGTH_LONG)
						.show();
			}
		} else if (v == imgAddLineItem) {
			if (sid == 0) {
				Toast.makeText(getApplicationContext(),
						"Please select Service Location first.",
						Toast.LENGTH_LONG).show();
			} else {
				try {
					Intent i = new Intent(AddAppointmentActivity.this,
							AddLineItemActivity.class);
					i.putExtra("service_taxable", trate.service_taxable);
					startActivityForResult(i, ADD_LINE_ITEM);
				} catch (Exception e) {

				}
			}
		}

	}

	private void downloadCustomer() {
		if (NetworkConnectivity.isConnected()) {
			mBaseLoader.showProgress("Syncing customer database");
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
								mBaseLoader.hideProgress();
								Intent i = new Intent(
										AddAppointmentActivity.this,
										CustomerListFragment.class);
								i.putExtra("FromAddAppointment", true);
								startActivityForResult(i, CHOOSE_CUSTOMER);
							}

							@Override
							public void ModelLoadFailedWithError(String error) {
								Toast.makeText(AddAppointmentActivity.this,
										error, Toast.LENGTH_LONG).show();
								mBaseLoader.hideProgress();
							}
						});
			} catch (Exception e) {
				mBaseLoader.hideProgress();
				e.printStackTrace();
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"Syncing customer data needs internet connection.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHOOSE_CUSTOMER) {
			if (resultCode == RESULT_OK) {
				cid = data.getIntExtra("customer_id", 0);
				if (cid != 0) {
					CustomerInfo info = CustomerList.Instance()
							.getCustomerById(cid);
					String name = info.setCustomerName();
					txtCustomer.setText(name);
					sid = 0;
					txtServiceLocationName.setText("");
					txtServiceLocation.setText("Choose Service Location");
					txtServiceLocationAddress.setText("");
					txtTax.setText("");
					trate = new TaxRates();
				}
			}
		} else if (requestCode == CHOOSE_SERVICE_LOCATION) {
			if (resultCode == RESULT_OK) {
				sid = data.getIntExtra("service_location_id", 0);
				if (sid != 0) {
					ServiceLocationsInfo info = ServiceLocationsList.Instance()
							.getServiceLocationById(sid);
					txtServiceLocationName.setText(info.name);
					txtServiceLocation.setText(info.name);
					txtServiceLocationAddress.setText(info.street + " "
							+ info.street_two);
					if (info.tax_rate_id != 0) {
						trate = TaxRateList.Instance().getTaxRateByid(
								info.tax_rate_id);
						txtTax.setText((trate.total_sales_tax * 100) + "%");
					}
					calculateTotal();
				}

			}
		} else if (requestCode == ADD_LINE_ITEM) {
			if (resultCode == RESULT_OK) {
				adapter = new LineItemAdapter(lineitems);
				lstLineItems.setAdapter(adapter);
				Utils.setListViewHeightBasedOnChildren(lstLineItems);
				calculateTotal();
			}
		}
	}

	public int taxamount = 0;

	private void calculateTotal() {
		float price = 0;

		for (LineItemsInfo item : lineitems) {
			float p = item.total;
			float disc = 0, tax = 0;
			if (edtDiscount.getText().length() > 0)
				disc = (p * Utils.ConvertToFloat(edtDiscount.getText()
						.toString())) / 100;
			p = p - disc;
			if (item.taxable) {
				tax = (p * trate.total_sales_tax);
				taxamount += tax;
			}
			p = p + tax;
			price += p;

		}
		txtTotal.setText("" + price);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {

	}

	@Override
	public void onTimeSet(TimePicker arg0, int arg1,
			int arg2) {

	}

	@Override
	public void onBackPressed() {
		ConfirmDialog dlg = ConfirmDialog.newInstance("Are your sure want to exit from this screen?");
		dlg.show(getSupportFragmentManager(), "confirm_exit");
	}

    @Override
    public void onDialogConfirm(String tag) {
        if (tag.equals("confirm_exit")) {
            finish();
        }
    }

    @Override
    public void onDialogCancel(String tag) {

    }
}
