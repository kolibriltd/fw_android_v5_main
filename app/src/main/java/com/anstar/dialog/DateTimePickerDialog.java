package com.anstar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.anstar.fieldwork.R;

import java.util.Calendar;

public class DateTimePickerDialog extends Dialog implements OnClickListener,
		TimePicker.OnTimeChangedListener, DatePicker.OnDateChangedListener {

	Button btnCancel, btnDone;
	DatePicker datepicker;
	TimePicker timepicker;
	int m_day, m_month, m_year;
	String main = "";
	private int m_startHour, m_startMinute;
	OnMyDialogResult mDialogResult;

	public DateTimePickerDialog(Context context) {
		super(context);
		setContentView(R.layout.date_time_picker);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnDone = (Button) findViewById(R.id.btnDone);
		datepicker = (DatePicker) findViewById(R.id.datePicker1);
		timepicker = (TimePicker) findViewById(R.id.timePicker1);
		final Calendar c = Calendar.getInstance();
		m_day = c.get(Calendar.DAY_OF_MONTH);
		m_month = c.get(Calendar.MONTH);
		m_year = c.get(Calendar.YEAR);
		m_startHour = c.get(Calendar.HOUR_OF_DAY);
		m_startMinute = c.get(Calendar.MINUTE);
		datepicker.init(m_year, m_month, m_day, this);
		timepicker.setOnTimeChangedListener(this);

		btnCancel.setOnClickListener(this);
		btnDone.setOnClickListener(this);

		// datepicker.OnDateSetListe
	}

	@Override
	public void onClick(View v) {
		if (v == btnCancel) {
			this.dismiss();
		} else if (v == btnDone) {
			String hi = updateDate();
			if (mDialogResult != null) {
				mDialogResult.finish(hi);
			}
			this.dismiss();
		}

	}

	private String formatTime(String s) {
		if (s.length() == 1) {
			String ss = "0" + s;
			return ss;
		}
		return s;
	}

	private String updateDate() {
		StringBuilder sb = new StringBuilder();

		sb.append(m_year).append("-")
				.append(formatTime(String.valueOf(m_month + 1))).append("-")
				.append(formatTime(String.valueOf(m_day)))
				.append(" " + formatTime(String.valueOf(m_startHour)))
				.append(":").append(formatTime(String.valueOf(m_startMinute)))
				.append(":").append("00");
		return sb.toString();

	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		m_startHour = hourOfDay;
		m_startMinute = minute;
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		m_day = dayOfMonth;
		m_month = monthOfYear;
		m_year = year;

	}

	public void setDialogResult(OnMyDialogResult dialogResult) {
		mDialogResult = dialogResult;
	}

	public interface OnMyDialogResult {
		void finish(String result);
	}
}
