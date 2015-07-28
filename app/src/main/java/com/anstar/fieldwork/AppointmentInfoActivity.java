package com.anstar.fieldwork;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.dialog.DateTimePickerDialog;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.PaymentInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.LineItemsList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppointmentInfoActivity extends AppCompatActivity implements
		TimePickerDialog.OnTimeSetListener, OnClickListener, DatePickerDialog.OnDateSetListener {

	private int m_startHour, m_startMinute;
	private int m_finishHour, m_finishMinute;
	int a_id;
	DateTimePickerDialog dl = null;
	private EditText edtStartedat, edtFinisedat, edtAmount, edtCheck, edtNote,
			edtPrice;
	// EditText edtScheduledDate;
	EditText edtBalForward, edtSubtotal, edtDiscountAmount, edtTax,
			edtTotalDue;
	private TextView txtInstruction;
	private Button btnSave;
	// AppoinmentInfo info = null;
	CheckBox chkCash, chkCheck, chkCreditCard;
	// CheckBox chkPaid;
	boolean isFromStarted, isPaid, isCheck, isCash, isCredit;
	//ActionBar action = null;
	RelativeLayout RlCheck;
	AppointmentInfo appointment = null;
	int m_day, m_month, m_year;
	String PaidType = "";
	// InvoiceInfo invoice = null;
	PaymentInfo payment_info = null;
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_info);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// info = MainActivity.appointmentInfo;
		Bundle b = getIntent().getExtras();
		if (b != null) {
			a_id = b.getInt(Const.Appointment_Id);
		}

/*
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Payment Info</font>"));
		// action.setTitle("Appointment Info");
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);
		mBaseLoader = new BaseLoader(this);

		edtTotalDue = (EditText) findViewById(R.id.edtTotalDue);
		edtTax = (EditText) findViewById(R.id.edtTax);
		edtDiscountAmount = (EditText) findViewById(R.id.edtDiscount);
		edtSubtotal = (EditText) findViewById(R.id.edtSubtotal);
		edtBalForward = (EditText) findViewById(R.id.edtBalForward);

		/*edtStartedat = (EditText) findViewById(R.id.edtStarted);
		edtFinisedat = (EditText) findViewById(R.id.edtFinished);*/
		edtAmount = (EditText) findViewById(R.id.edtAmount);
		edtPrice = (EditText) findViewById(R.id.edtPrice);
		// edtScheduledDate = (EditText) findViewById(R.id.edtScheduledDate);
		edtCheck = (EditText) findViewById(R.id.edtCheck);
		edtNote = (EditText) findViewById(R.id.edtNote);
		btnSave = (Button) findViewById(R.id.btnSave);
		txtInstruction = (TextView) findViewById(R.id.txtInstruction);
		// chkPaid = (CheckBox) findViewById(R.id.chkPaid);
		chkCash = (CheckBox) findViewById(R.id.chkCash);
		chkCheck = (CheckBox) findViewById(R.id.chkCheck);
		chkCreditCard = (CheckBox) findViewById(R.id.chkCreditCard);
		RlCheck = (RelativeLayout) findViewById(R.id.RlCheck);
		final Calendar c = Calendar.getInstance();
		m_day = c.get(Calendar.DAY_OF_MONTH);
		m_month = c.get(Calendar.MONTH);
		m_year = c.get(Calendar.YEAR);
		m_startHour = c.get(Calendar.HOUR_OF_DAY);
		m_startMinute = c.get(Calendar.MINUTE);
		m_finishHour = c.get(Calendar.HOUR_OF_DAY);
		m_finishMinute = c.get(Calendar.MINUTE);
		isCheck();
		isCash();
		isCreditcard();
		// isPaid();
		// edtStartedat.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
		// // showDialog(TIME_DIALOG_ID_START);
		// new TimePickerDialog(AppointmentInfoActivity.this, time1,
		// m_startHour, m_startMinute, false).show();
		// }
		// return false;
		// }
		// });
		//
		// edtFinisedat.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
		// // showDialog(TIME_DIALOG_ID_FINISH);
		// new TimePickerDialog(AppointmentInfoActivity.this, time2,
		// m_finishHour, m_finishMinute, false).show();
		// }
		// return false;
		// }
		// });
		// edtScheduledDate.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
		// new DatePickerDialog(AppointmentInfoActivity.this,
		// datelistener, m_year, m_month, m_day).show();
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
		// info = AppointmentList.Instance().getAppointmentById(Customer_id);

		btnSave.setOnClickListener(this);

	};

	@Override
	protected void onResume() {
		super.onResume();
		appointment = AppointmentModelList.Instance().getAppointmentById(a_id);
		// payment_info = InvoiceInfo.getInvoiceByAppId(a_id);
		payment_info = PaymentInfo.getPaymentsInfoByAppId(a_id);
		loadValue();
	}

	/*private void StartedAt() {
		if (m_startHour >= 12) {
			String time = m_startHour + ":" + m_startMinute;
			ConvertDate24To12Hour(edtStartedat, time, "PM");
		} else if (m_startHour < 12) {
			String time = m_startHour + ":" + m_startMinute;
			ConvertDate24To12Hour(edtStartedat, time, "AM");
		}
	}*/

	private void FinishedAt() {
		if (m_finishHour >= 12) {
			String time = m_finishHour + ":" + m_finishMinute;
			ConvertDate24To12Hour(edtFinisedat, time, "PM");
		} else if (m_finishHour < 12) {
			String time = m_finishHour + ":" + m_finishMinute;
			ConvertDate24To12Hour(edtFinisedat, time, "AM");
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

	TimePickerDialog.OnTimeSetListener time1 = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0,
				int arg1, int arg2) {
			m_startHour = arg1;
			m_startMinute = arg2;
			///StartedAt();

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

	// DatePickerDialog.OnDateSetListener datelistener = new OnDateSetListener()
	// {
	//
	// @Override
	// public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
	// m_day = arg3;
	// m_month = arg2;
	// m_year = arg1;
	// updateDate(edtScheduledDate);
	// }
	// };

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

	@Override
	public void onClick(View v) {
		if (v == btnSave) {
			if (isCheck || isCash || isCredit) {
				if (payment_info == null) {
					payment_info = new PaymentInfo();
				}
				if (isCheck) {
					String checknumber = edtCheck.getText().toString();
					if (checknumber != null && checknumber.length() > 0) {
						payment_info.check_number = checknumber;
					} else {
						Toast.makeText(getApplicationContext(),
								"Please provide check number.",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				String amt = edtAmount.getText().toString().length() > 0 ? edtAmount
						.getText().toString() : "0";
				if (amt.equalsIgnoreCase("0") || amt.length() <= 0) {
					Toast.makeText(getApplicationContext(),
							"Please enter valid amount.", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				appointment.price = amt;
				try {
					appointment.save();
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
				mBaseLoader.showProgress();
				// if(invoice == null)
				// invoice = new InvoiceInfo();
				payment_info.amount = amt;
				payment_info.AppointmentId = appointment.id;
				payment_info.created_from_mobile = true;
				// payment_info.paid = true;
				payment_info.payment_method = PaidType;

				// InvoiceInfo.AddInvoice(payment_info, a_id, appointment,
				// new UpdateInfoDelegate() {
				//
				// @Override
				// public void UpdateSuccessFully(ServiceResponse res) {
				// hideProgress();
				// if (!res.isError()) {
				// Toast.makeText(getApplicationContext(),
				// "Appointment Info Saved",
				// Toast.LENGTH_LONG).show();
				// // InvoiceList.Instance().ClearDB();
				// }
				// finish();
				// }
				//
				// @Override
				// public void UpdateFail(String ErrorMessage) {
				// hideProgress();
				// Toast.makeText(getApplicationContext(),
				// ErrorMessage, Toast.LENGTH_LONG).show();
				// }
				// });
				PaymentInfo.AddPaymentInfo(payment_info, a_id, appointment,
						new UpdateInfoDelegate() {

							@Override
							public void UpdateSuccessFully(ServiceResponse res) {
								mBaseLoader.hideProgress();
								if (!res.isError()) {
									Toast.makeText(
											getApplicationContext(),
											"Payment information saved successfully",
											Toast.LENGTH_LONG).show();
									// InvoiceList.Instance().ClearDB();
								}
								finish();
							}

							@Override
							public void UpdateFail(String ErrorMessage) {
								mBaseLoader.hideProgress();
								Toast.makeText(getApplicationContext(),
										ErrorMessage, Toast.LENGTH_LONG).show();
							}
						});

			} else {
				Toast.makeText(getApplicationContext(),
						"Please select payment method", Toast.LENGTH_LONG)
						.show();
			}

		}

	}

	@Override
	public void onPause() {
		// SaveData s = new SaveData();
		// s.execute();
		super.onPause();
	}

	public void isCheck() {
		chkCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isCheck = isChecked;
				if (isChecked) {
					// RlCheck.setVisibility(View.VISIBLE);
					chkCash.setChecked(false);
					chkCreditCard.setChecked(false);
					PaidType = "check";
				} else {
					// RlCheck.setVisibility(View.GONE);
				}
			}
		});
	}

	public void isCash() {
		chkCash.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isCash = isChecked;
				if (isChecked) {
					chkCheck.setChecked(false);
					chkCreditCard.setChecked(false);
					PaidType = "cash";
				}
			}
		});
	}

	public void isCreditcard() {
		chkCreditCard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isCredit = isChecked;
				if (isChecked) {
					chkCheck.setChecked(false);
					chkCash.setChecked(false);
					PaidType = "card";
				}
			}
		});
	}

	// public void isPaid() {
	// chkPaid.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView,
	// boolean isChecked) {
	// isPaid = isChecked;
	// if (isChecked) {
	// chkCheck.setEnabled(true);
	// chkCreditCard.setEnabled(true);
	// chkCash.setEnabled(true);
	// } else {
	// PaidType = "";
	// chkCheck.setChecked(false);
	// chkCreditCard.setChecked(false);
	// chkCash.setChecked(false);
	// chkCheck.setEnabled(false);
	// chkCreditCard.setEnabled(false);
	// chkCash.setEnabled(false);
	// }
	// }
	// });
	// }

	@Override
	public void onTimeSet(TimePicker arg0, int arg1,
			int arg2) {

	}

	public void loadValue() {
		if (appointment != null) {
			String[] temp = appointment.starts_at.split("T");
			// String time = temp[1].replace("-06:00", "");
			// edtScheduledDate.setText(temp[0]);
			edtNote.setText(appointment.notes);
			// if (appointment.instructions != null
			// && appointment.instructions.length() > 0)
			// txtInstruction.setText(appointment.instructions);
			// else
			// txtInstruction.setText("No instructions.");
			// edtPrice.setText(""
			// + LineItemsList.Instance().getLineItemsPriceByAppt(
			// appointment.id));
			float discount = 0, tax_amount = 0, balForward = 0;
			double subtotal;
			if (appointment.discount_amount != null
					&& appointment.discount_amount.length() > 0) {
				discount = Utils.ConvertToFloat(appointment.discount_amount);
				edtDiscountAmount.setText(String.format("%.2f", discount));
			}
			if (appointment.tax_amount != null
					&& appointment.tax_amount.length() > 0) {
				tax_amount = Utils.ConvertToFloat(appointment.tax_amount);
				edtTax.setText(String.format("%.2f", tax_amount));
			}
			subtotal = LineItemsList.Instance().getLineItemsPriceByAppt(
					appointment.id);

			edtSubtotal.setText(""
					+ String.format("%.2f", (float) LineItemsList.Instance()
							.getLineItemsPriceByAppt(appointment.id)));
			CustomerInfo customer = CustomerList.Instance().getCustomerById(
					Const.customer_id);
			if (customer != null) {
				if (customer.balance != null && customer.balance.length() > 0) {
					balForward = Utils.ConvertToFloat(customer.balance);
					edtBalForward.setText(String.format("%.2f", balForward));
				}
			}

			// float total = subtotal + balForward;
			float total = (float) subtotal;
			total = total - discount;
			total = total + tax_amount;
			edtTotalDue.setText(String.format("%.2f", total));
			// edtPrice.setText(String.format("%.2f",
			// Double.parseDouble(appointment.price)));
			if (appointment.started_at_time == null
					|| appointment.started_at_time.equalsIgnoreCase("null")) {
				// edtStartedat.setText("");
			} else {
				try {
					String stime[] = appointment.started_at_time.substring(0,
							appointment.started_at_time.length()).split(":");
					String[] second = stime[1].split(" ");
					m_startHour = Integer.parseInt(stime[0]);
					m_startMinute = Integer.parseInt(second[0]);
				} catch (Exception e) {

				}
				// StartedAt();
				// edtStartedat.setText(appointment.started_at_time);
			}
			if (appointment.finished_at_time == null
					|| appointment.finished_at_time.equalsIgnoreCase("null")) {
				// edtFinisedat.setText("");
			} else {
				try {
					String etime[] = appointment.finished_at_time.substring(0,
							appointment.finished_at_time.length()).split(":");
					String[] second = etime[1].split(" ");
					m_finishHour = Integer.parseInt(etime[0]);
					m_finishMinute = Integer.parseInt(second[0]);
				} catch (Exception e) {

				}
				// FinishedAt();
				// edtFinisedat.setText(appointment.finished_at_time);
			}

			if (payment_info != null) {

				edtAmount.setText(String.format("%.2f",
						Utils.ConvertToDouble(payment_info.amount)));
				// chkPaid.setChecked(payment_info.paid);
				if (payment_info.payment_method != null) {
					if (payment_info.payment_method.equalsIgnoreCase("cash")) {
						chkCash.setChecked(true);
					}
					if (payment_info.payment_method.equalsIgnoreCase("check")) {
						chkCheck.setChecked(true);
						// RlCheck.setVisibility(View.VISIBLE);
						edtCheck.setText(payment_info.check_number);
					}
					if (payment_info.payment_method.equalsIgnoreCase("card")) {
						chkCreditCard.setChecked(true);
					}
				}
			}
		}
	}

	public Dialog dialogDateTime() {
		DateTimePickerDialog dl = new DateTimePickerDialog(this);
		dl.show();
		return dl;

	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub

	}

}
